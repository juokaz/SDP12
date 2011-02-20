#include "objdetectionutil.h"
bool clr_picker=0;
CvScalar color;
CvScalar max_color;
CvScalar min_color;

IplImage* objDetection::utilities::cvShowManyImages(char* title, int nArgs, ...) {

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
void objDetection::utilities::thresholdFinder(const char* traininputbase,CvScalar& min,CvScalar& max,bool hsv_true)
{
	max_color=cvScalar(0,0,0);
	min_color=cvScalar(255,255,255);
	for(int i=TEST_FILES_START;i<TEST_FILES_END;i++)
	{
		clr_picker=0;
		std::stringstream ss;
		ss<<traininputbase<<i<<".jpg";
		std::string currentFile=ss.str();
		std::cout<<"Loading "<<currentFile<<std::endl;
		IplImage* frame =cvLoadImage(currentFile.c_str(),1);
		if(!frame)
			continue;
		IplImage *  hsv_frame;
		if(hsv_true)
		{
		hsv_frame= cvCreateImage(cvSize(frame->width,frame->height), IPL_DEPTH_8U, 3);
		cvCvtColor(frame, hsv_frame, CV_BGR2HSV);
		}
		else
		{
			hsv_frame=frame;
		}
		CvScalar res=objDetection::utilities::colorPicker(hsv_frame);
		std::cout<<"min:"<<min_color.val[0]<<","<<min_color.val[1]<<","<<min_color.val[2]<<","<<min_color.val[3]<<std::endl;
		std::cout<<"max:"<<max_color.val[0]<<","<<max_color.val[1]<<","<<max_color.val[2]<<","<<max_color.val[3]<<std::endl;
		if(hsv_true)
		{
		cvReleaseImage(&hsv_frame);
		}
		cvReleaseImage(&frame);
	}
	min=min_color;
	max=max_color;
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
