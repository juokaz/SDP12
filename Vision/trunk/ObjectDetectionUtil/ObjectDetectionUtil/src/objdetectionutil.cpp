#include "objdetectionutil.h"
bool clr_picker=0;
CvScalar color;
CvScalar max_color;
CvScalar min_color;

// Color threshold for Black Dot
CvScalar hsv_min_D = cvScalar(0,5,15);
CvScalar hsv_max_D = cvScalar(61,255,255);

// Color threshold for Red Ball
CvScalar hsv_min_B = cvScalar(0,131,104);
CvScalar hsv_max_B = cvScalar(10,255,255);

// Color threshold for Yellow T 
CvScalar hsv_min_TY = cvScalar(0,8,75);
CvScalar hsv_max_TY = cvScalar(20,255,255);

// Color threshold for Blue T
CvScalar hsv_min_TB = cvScalar(29,14,0);
CvScalar hsv_max_TB = cvScalar(78,255,255);

void objDetection::utilities::showResults(IplImage*& frame,config& conf,IplImage*& thresh1,IplImage*& thresh2,IplImage*& thresh3,int64 diffTime)
{

	//cvShowImage("Test Window4",frame);

	IplImage* frame_c=cvCreateImage(cvSize(frame->width,frame->height),frame->depth,frame->nChannels);
	IplImage* thresh1_c=cvCreateImage(cvSize(thresh1->width,thresh1->height),frame->depth,frame->nChannels);
	IplImage* thresh2_c=cvCreateImage(cvSize(thresh2->width,thresh2->height),frame->depth,frame->nChannels);
	IplImage* thresh3_c=cvCreateImage(cvSize(thresh3->width,thresh3->height),frame->depth,frame->nChannels);

	cvCopy(frame,frame_c);
	cvConvertImage(thresh1,thresh1_c);
	cvConvertImage(thresh2,thresh2_c);

	cvConvertImage(thresh3,thresh3_c);

	IplImage* disp=objDetection::utilities::cvShowManyImages("Camera",4,frame_c,thresh1_c,thresh2_c,thresh3_c);
	conf.totalTime+=diffTime;
	conf.Opcount++;
	float meanfps=1/( ( ( (float)conf.totalTime )/conf.Opcount) /cv::getTickFrequency());
	float fps=1/(((float)diffTime)/cv::getTickFrequency());
	if(disp && conf.show)
	{
		int w=disp->width;
		int h=disp->height;

		CvFont font;
		cvInitFont(&font, CV_FONT_HERSHEY_SIMPLEX, 0.4, 0.4, 0, 1, CV_AA);
		stringstream ss;
		ss.setf(ios::fixed,ios::floatfield);
		ss.precision(1);
		ss<<"fps: "<<fps;
		CvPoint point=cvPoint(10, h-100);
		cvPutText(disp,ss.str().c_str(),point, &font, cvScalar(0, 0, 0, 0));
		ss.str("");
		ss<<"mean fps: "<<meanfps;
		CvPoint point2=point;
		point2.x+=80;
		cvPutText(disp,ss.str().c_str(),point2, &font, cvScalar(0, 0, 0, 0));
		ss.str("");
		point.y+=20;
		ss<<"Blue T pos:"<<conf.sel_TB.center.x<<","<<conf.sel_TB.center.y<<","<<conf.sel_TB.angle*180/PI;
		cvPutText(disp,ss.str().c_str(),point, &font, cvScalar(0, 0, 0, 0));
		ss.str("");
		point.y+=20;
		ss<<"Yellow T pos:"<<conf.sel_TY.center.x<<","<<conf.sel_TY.center.y<<","<<conf.sel_TY.angle*180/PI;
		cvPutText(disp,ss.str().c_str(),point, &font, cvScalar(0, 0, 0, 0));
		ss.str("");
		point.y+=20;
		ss<<"Ball pos:"<<conf.rect_B.x<<","<<conf.rect_B.y;
		cvPutText(disp,ss.str().c_str(),point, &font, cvScalar(0, 0, 0, 0));
		cvShowImage("Camera",disp);

	}
	//std::cout.setf(ios::fixed,ios::floatfield);
	std::cout.precision(1);
	//std::cout<<"fps: "<<fps<<" - mean fps: "<<meanfps<<std::endl;
	cvReleaseImage(&disp);
	cvReleaseImage(&frame_c);
	cvReleaseImage(&thresh1_c);
	cvReleaseImage(&thresh2_c);
	cvReleaseImage(&thresh3_c);
}
void objDetection::utilities::getImageFromCamera(config& conf,IplImage*& image)
{
	if( !cvGrabFrame( conf.capture ))
	{
		std::cout<<"Camera Stopped working, Closing"<<std::endl;
		conf.current_frame=NULL;
		return;
	}
	image = cvRetrieveFrame( conf.capture );
}
void objDetection::utilities::getImageFromFile(const char* file,IplImage*& image)
{
	image=cvLoadImage(file);
}
void objDetection::utilities::releaseCurrentFrame(config& conf)
{
	if((!conf.current_frame)&&(conf.image_file))
	{	
		cvReleaseImage(&conf.current_frame);

	}
}
std::string objDetection::utilities::getNextImageFileName(config& conf)
{
	if(conf.i_base.current>=conf.i_base.image_end)
		return "";
	std::stringstream ss;
	ss<<conf.i_base.basefile<<conf.i_base.current<<".jpg";
	std::string currentFile=ss.str();
	conf.i_base.current++;
	return currentFile;
}
bool objDetection::utilities::getNextFrame(config& conf)
{
	if(conf.camera)
	{
		getImageFromCamera(conf,conf.current_frame);
		if(conf.current_frame!=NULL)
			return true;
		else
			return false;
	}
	else if(conf.image_file)
	{
		std::string add=getNextImageFileName(conf);
		if(strcmp(add.c_str(),"")!=0)
			getImageFromFile(add.c_str(),conf.current_frame);
		else
			return false;
	}
	if(conf.current_frame!=NULL)
	{
		cropFrame(conf,conf.current_frame);
		return true;
	}
	else
		return false;
}
void objDetection::utilities::cropFrame(config& conf,IplImage*& img)
{
	int w=conf.windowOfInterest.Width;
	int h=conf.windowOfInterest.Height; 
	cvSetImageROI(img,cvRect(conf.windowOfInterest.X,conf.windowOfInterest.Y,w,h));
	IplImage* temp=cvCreateImage(cvSize(w,h), img->depth,3);
	cvCopy(img,temp);
	cvReleaseImage(&img);
	img=temp;
}

void objDetection::utilities::setupBackgroundImage(config& conf)
{
	if(!conf.back)
	{
		if(conf.camera)
		{
			getImageFromFile("bg.jpg",conf.background);
			if(conf.background)
			{
				std::cout<<"background loaded"<<std::endl;
			}
			else
			{
				std::cout<<"Setup for Background image"<<std::endl;
				getImageFromCamera(conf,conf.background);	
			}
		}
		else if(conf.image_file)
		{
			getImageFromFile(getNextImageFileName(conf).c_str(),conf.background);
		}
		conf.back=true;
		cropFrame(conf,conf.background);
	}
}
void objDetection::utilities::initImageStack(config& conf)
{
	if(conf.camera)
	{
		std::cout<<"Going for Camera"<<std::endl;
		conf.capture=cvCaptureFromCAM(0);
	}
	if(conf.image_file)
	{
		conf.i_base.current=conf.i_base.image_start;
	}
	if(conf.image_file)
	{
		objDetection::utilities::cb_init(&conf.TY_Buffer,1,sizeof(float));
		objDetection::utilities::cb_init(&conf.TB_Buffer,1,sizeof(float));
	}
	if(conf.camera)
	{
		objDetection::utilities::cb_init(&conf.TY_Buffer,1,sizeof(float));
		objDetection::utilities::cb_init(&conf.TB_Buffer,1,sizeof(float));
	}
}
void objDetection::utilities::output(config& conf,ofstream* file)
{
	if(conf.outputToText||conf.outputToConsole)
	{

		//std::cout<<"outputToConsole"<<std::endl;
		stringstream ss;
		ss<<conf.sel_TB.center.x<<","<<conf.sel_TB.center.y<<","<<conf.sel_TB.angle<<",";	
		ss<<conf.rect_B.x<<","<<conf.rect_B.y<<",";
		ss<<conf.sel_TY.center.x<<","<<conf.sel_TY.center.y<<","<<conf.sel_TY.angle;
		ss<<std::endl;
		if(conf.outputToText)
			*(file)<<ss.str();
		if(conf.outputToConsole)
			std::cerr<<ss.str();

	}
}
void objDetection::utilities::show(config& conf)
{
	if(conf.show)
	{

		try
		{

			objDetection::drawOrientation(conf.current_frame,conf.sel_TB);
			objDetection::drawOrientation(conf.current_frame,conf.sel_TY);
			cvRectangle(conf.current_frame,cvPoint(conf.rect_B.x,conf.rect_B.y),cvPoint(conf.rect_B.x+conf.rect_B.width,conf.rect_B.y+conf.rect_B.height),cvScalar(0,0,0),3);
		}
		catch(std::exception ex)
		{
			return;
		}
	}
}
void objDetection::utilities::initFile(config& conf,ofstream*& file)
{
	if(conf.outputToText)
		file=new ofstream(conf.outputfile);
}
config objDetection::utilities::get_Config(int argc, char* argv[])
{
	config res;
	res.camera=false;
	res.image_file=false;
	res.train_major=false;
	res.train_minor=false;
	res.show=false;
	res.back=false;
	res.outputfile=false;	res.outputToConsole=false;
	res.outputToText=false;
	res.predict_major=false;
	res.predict_minor=false;
	res.totalTime=0;
	res.Opcount=0;
	res.hsv_max_B=hsv_max_B;
	res.hsv_max_D=hsv_max_D;
	res.hsv_max_TB=hsv_max_TB;
	res.hsv_max_TY=hsv_max_TY;
	res.hsv_min_B=hsv_min_B;
	res.hsv_min_TB=hsv_min_TB;
	res.hsv_min_TY=hsv_min_TY;
	res.hsv_min_D=hsv_min_D;
	res.storage=cvCreateMemStorage(0);
	res.windowOfInterest.X=80;
	res.windowOfInterest.Y= 110;
	res.windowOfInterest.Width=540;
	res.windowOfInterest.Height= 290;


	int currentIndex=1;
	if(argv[currentIndex][0]=='c')
	{
		res.camera=true;
	}
	if(argv[currentIndex][0]=='i')
	{
		res.image_file=true;
		res.i_base.current=1;	
		//go for image file

	}
	currentIndex++;
	if(res.image_file)
	{
		res.i_base.basefile=argv[currentIndex];
		currentIndex++;
	}
	if(res.image_file)
	{
		res.i_base.image_start=atoi(argv[currentIndex]);
		currentIndex++;
	}
	if(res.image_file)
	{
		res.i_base.step=atoi(argv[currentIndex]);
		currentIndex++;
	}
	if(res.image_file)
	{
		res.i_base.image_end=atoi(argv[currentIndex]);
		currentIndex++;
	}
	if(!strcmp(argv[currentIndex],"window"))
	{
		currentIndex++;
		res.windowOfInterest.X=atoi(argv[currentIndex]);
		currentIndex++;
		res.windowOfInterest.Y=atoi(argv[currentIndex]);
		currentIndex++;
		res.windowOfInterest.Width=atoi(argv[currentIndex]);
		currentIndex++;
		res.windowOfInterest.Height=atoi(argv[currentIndex]);
		currentIndex++;
	}
	if(!strcmp(argv[currentIndex],"train_major"))
	{
		res.train_major=true;
	}
	if(!strcmp(argv[currentIndex],"train_minor"))
	{
		res.train_minor=true;
	}
	if(!strcmp(argv[currentIndex],"predict_major"))
	{
		res.predict_major=true;
	}
	if(!strcmp(argv[currentIndex],"predict_minor"))
	{
		res.predict_minor=true;
	}
	currentIndex++;
	if(currentIndex==argc)
	{
		return res;
	}
	for(int i=0;i<3;i++)
	{
		if(!strcmp(argv[currentIndex],"show"))
		{
			res.show=true;
		} else if(!strcmp(argv[currentIndex],"outputToText"))
		{
			res.outputToText=true;
			currentIndex++;
			res.outputfile=argv[currentIndex];
		} else if(!strcmp(argv[currentIndex],"outputToConsole"))
		{
			res.outputToConsole=true;
		}else
			break;
		currentIndex++;
		
	}
	if(currentIndex==argc)
	{
		return res;
	}
	if(!strcmp(argv[currentIndex],"TY_min"))
	{
		currentIndex++;
		stringstream ss(argv[currentIndex]);
		double m1;
		ss>>m1;
		currentIndex++;
		stringstream ss2(argv[currentIndex]);
		double m2;
		ss2>>m2;
		currentIndex++;
		stringstream ss3(argv[currentIndex]);
		double m3;
		ss3>>m3;
		currentIndex++;
		res.hsv_min_TY= cvScalar(m1,m2,m3);
	} 
	if(currentIndex==argc)
	{
		return res;
	}
	if(!strcmp(argv[currentIndex],"TY_max"))
	{
		currentIndex++;
		stringstream ss(argv[currentIndex]);
		double m1;
		ss>>m1;
		currentIndex++;
		stringstream ss2(argv[currentIndex]);
		double m2;
		ss2>>m2;
		currentIndex++;
		stringstream ss3(argv[currentIndex]);
		double m3;
		ss3>>m3;
		currentIndex++;
		res.hsv_max_TY= cvScalar(m1,m2,m3);
	}
	if(!strcmp(argv[currentIndex],"TB_min"))
	{
		currentIndex++;
		stringstream ss(argv[currentIndex]);
		double m1;
		ss>>m1;
		currentIndex++;
		stringstream ss2(argv[currentIndex]);
		double m2;
		ss2>>m2;
		currentIndex++;
		stringstream ss3(argv[currentIndex]);
		double m3;
		ss3>>m3;
		currentIndex++;
		res.hsv_min_TB= cvScalar(m1,m2,m3);
	} 
	if(currentIndex==argc)
	{
		return res;
	}
	if(!strcmp(argv[currentIndex],"TB_max"))
	{
		currentIndex++;
		stringstream ss(argv[currentIndex]);
		double m1;
		ss>>m1;
		currentIndex++;
		stringstream ss2(argv[currentIndex]);
		double m2;
		ss2>>m2;
		currentIndex++;
		stringstream ss3(argv[currentIndex]);
		double m3;
		ss3>>m3;
		currentIndex++;
		res.hsv_max_TB= cvScalar(m1,m2,m3);
	}
	return res;

}
void objDetection::utilities::cb_init(circular_buffer *cb, size_t capacity, size_t sz)
{
	cb->buffer = malloc(capacity * sz);
	if(cb->buffer == NULL);
	// handle error
	cb->buffer_end = (char *)cb->buffer + capacity * sz;
	cb->capacity = capacity;
	cb->count = 0;
	cb->sz = sz;
	cb->head = cb->buffer;
	cb->tail = cb->buffer;
}
float objDetection::utilities::average_cb_buffer(circular_buffer *cb)
{
	void* item=cb->tail;
	int count=cb->count;
	float value;
	float sum=0;
	while(true)
	{
		if(count == 0)
			break;
		memcpy(&value, item, cb->sz);
		sum+=value;
		item = (char*)item + cb->sz;
		if(item == cb->buffer_end)
			item = cb->buffer;
		count--;
	}
	return sum/cb->count;
}
void objDetection::utilities::cb_free(circular_buffer *cb)
{
	free(cb->buffer);
	// clear out other fields too, just to be safe
}

void objDetection::utilities::cb_push_back(circular_buffer *cb, const void *item)
{
	if(cb->count == cb->capacity)
		cb->count--;
	memcpy(cb->head, item, cb->sz);
	cb->head = (char*)cb->head + cb->sz;
	if(cb->head == cb->buffer_end)
		cb->head = cb->buffer;
	cb->count++;
}

void objDetection::utilities::cb_pop_front(circular_buffer *cb, void *item)
{
	if(cb->count == 0);
	{
		item=NULL;
		return;
	}
	memcpy(item, cb->tail, cb->sz);
	cb->tail = (char*)cb->tail + cb->sz;
	if(cb->tail == cb->buffer_end)
		cb->tail = cb->buffer;
	cb->count--;
}
IplImage* objDetection::utilities::cvShowManyImages(const char* title, int nArgs, ...) {

	// img - Used for getting the arguments 
	IplImage *img;

	// DispImage - the image in which input images are to be copied
	IplImage *DispImage;

	int size;
	int i;
	int m, n;
	int x, y;

	// w - Maximum number of images in a row 
	// h - Maximum number of images in a column 
	int w, h;

	// scale - How much we have to resize the image
	float scale;
	int max;

	// If the number of arguments is lesser than 0 or greater than 12
	// return without displaying 
	if(nArgs <= 0) {
		printf("Number of arguments too small....\n");
		return NULL;
	}
	else if(nArgs > 12) {
		printf("Number of arguments too large....\n");
		return NULL;
	}
	// Determine the size of the image, 
	// and the number of rows/cols 
	// from number of arguments 
	else if (nArgs == 1) {
		w = h = 1;
		size = 300;
	}
	else if (nArgs == 2) {
		w = 2; h = 1;
		size = 300;
	}
	else if (nArgs == 3 || nArgs == 4) {
		w = 2; h = 2;
		size = 300;
	}
	else if (nArgs == 5 || nArgs == 6) {
		w = 3; h = 2;
		size = 200;
	}
	else if (nArgs == 7 || nArgs == 8) {
		w = 4; h = 2;
		size = 200;
	}
	else {
		w = 4; h = 3;
		size = 150;
	}

	// Create a new 3 channel image
	DispImage = cvCreateImage( cvSize(100 + size*w, 60 + size*h), 8, 3 );
	cvSet(DispImage,cvScalarAll(100),0);
	// Used to get the arguments passed
	va_list args;
	va_start(args, nArgs);

	// Loop for nArgs number of arguments
	for (i = 0, m = 20, n = 20; i < nArgs; i++, m += (20 + size)) {

		// Get the Pointer to the IplImage
		img = va_arg(args, IplImage*);

		// Check whether it is NULL or not
		// If it is NULL, release the image, and return
		if(img == 0) {
			printf("Invalid arguments");
			cvReleaseImage(&DispImage);
			return NULL;
		}

		// Find the width and height of the image
		x = img->width;
		y = img->height;

		// Find whether height or width is greater in order to resize the image
		max = (x > y)? x: y;

		// Find the scaling factor to resize the image
		scale = (float) ( (float) max / size );

		// Used to Align the images
		if( i % w == 0 && m!= 20) {
			m = 20;
			n+= 20 + size;
		}

		// Set the image ROI to display the current image
		cvSetImageROI(DispImage, cvRect(m, n, (int)( x/scale ), (int)( y/scale )));

		// Resize the input image and copy the it to the Single Big Image
		cvResize(img, DispImage);

		// Reset the ROI in order to display the next image
		cvResetImageROI(DispImage);
	}

	// Create a new window, and show the Single Big Image
	//cvNamedWindow( title, 1 );
	va_end(args);
	return DispImage;
	//cvShowImage( title, DispImage);

	//cvWaitKey();
	//cvDestroyWindow(title);

	// End the number of arguments


	// Release the Image Memory
	//cvReleaseImage(&DispImage);
}
void my_mouse_callback(int event, int x, int y, int flags, void* param)
{
	if(flags&CV_EVENT_LBUTTONDOWN)
	{
		clr_picker=1;
		IplImage* frame=(IplImage*)param;
		CvSize size=cvSize(frame->width,frame->height);
		uchar* ptr = (uchar*) (frame->imageData + y * frame->widthStep);
		color=cvScalar(ptr[3*x],ptr[3*x+1],ptr[3*x+2]);
		cvDrawCircle(param,cvPoint(x,y),5,color,4);
		std::cout<<"current:"<<color.val[0]<<","<<color.val[1]<<","<<color.val[2]<<","<<color.val[3]<<std::endl;
		if(max_color.val[0]<color.val[0])
			max_color.val[0]=color.val[0];
		if(max_color.val[1]<color.val[1])
			max_color.val[1]=color.val[1];
		if(max_color.val[2]<color.val[2])
			max_color.val[2]=color.val[2];

		if(min_color.val[0]>color.val[0])
			min_color.val[0]=color.val[0];
		if(min_color.val[1]>color.val[1])
			min_color.val[1]=color.val[1];
		if(min_color.val[2]>color.val[2])
			min_color.val[2]=color.val[2];


	}
}
CvScalar objDetection::utilities::colorPicker(IplImage* img)
{


	if(!img)
		return CvScalar();
	cvNamedWindow( "Color Picker", CV_WINDOW_AUTOSIZE );
	cvShowImage( "Color Picker", img ); // Original stream with detected ball overlay
	cvSetMouseCallback("Color Picker",my_mouse_callback,img);


	cvWaitKey(0);
	cvShowImage( "Color Picker", img ); // Original stream with detected ball overlay

	cvDestroyWindow("Color Picker");
	return color;

}
