#include "objdetectionutil.h"
bool clr_picker=0;
CvScalar color;
CvScalar max_color;
CvScalar min_color;
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
