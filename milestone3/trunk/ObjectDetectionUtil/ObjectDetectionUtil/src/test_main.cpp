#include <cvaux.h>
#include <highgui.h>
#include <cxcore.h>
#include <stdio.h>
#include <iostream>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <assert.h>
#include <math.h>
#include <cstring>
#include <float.h>
#include <limits.h>
#include <time.h>
#include <ctype.h>
#include <iostream>
#include "../../../ObjectDetection/src/objdetection.h"
#include "objdetectionutil.h"
#include <sstream>


// MIN values for yellow robot
int ty_min_1	= 0;
int ty_min_2	= 8;
int ty_min_3	= 75;
// MAX values for yellow robot
int ty_max_1	= 20;
int ty_max_2	= 255;
int ty_max_3	= 255;


CvScalar hsv_min_TY = cvScalar(ty_min_1, ty_min_2, ty_min_3);
CvScalar hsv_max_TY = cvScalar(ty_max_1, ty_max_2, ty_max_3);


void launch(config conf)
{	

	CvMemStorage* storage=cvCreateMemStorage(0);
	IplImage* frame;
	IplImage* back_img;
	CvCapture* capture;
	bool back=false;
	
	ofstream* writer;
	if(conf.outputToText)
		writer=new ofstream(conf.outputfile);
	
	if(conf.camera)
	{
		std::cout<<"Going for Camera"<<std::endl;
		capture=cvCaptureFromCAM(0);
	}
	if(conf.image_file)
	{
		conf.i_base.current=conf.i_base.image_start;
	}
	while(true)
	{
		if(conf.camera)
		{
			if(conf.camera)
			{

				if(!back)
				{
					frame=cvLoadImage("bg.jpg");
					if(frame)
					{
						std::cout<<"background loaded"<<std::endl;

					}
					else
					{
						std::cout<<"Setup for Background image"<<std::endl;
						std::getchar();

						std::cout<<"grabbing frame"<<std::endl;
						if( !cvGrabFrame( capture ))
						{
							std::cout<<"Camera Stopped working, Closing"<<std::endl;
							break;
						}
						frame = cvRetrieveFrame( capture );
						std::cout<<"frame grabbed"<<std::endl;
					}
				}
				else
				{
					if( !cvGrabFrame( capture ))
					{
						std::cout<<"Camera Stopped working, Closing"<<std::endl;
						break;
					}
					frame = cvRetrieveFrame( capture );
				}
			}
		}

	if(conf.image_file)
	{
		
		std::stringstream ss;
		ss<<conf.i_base.basefile<<conf.i_base.current<<".jpg";
		std::string currentFile=ss.str();
		
		frame =cvLoadImage(currentFile.c_str(),1);
		std::cout<<"image: "<<conf.i_base.current<<std::endl;
		
	}
	if(!frame)
	{
			std::cout<<"Error: No Image, Closing"<<std::endl;
			break;
	}
	int w=540;
	int h=290; 
	IplImage* buffer_frame  = cvCreateImage(cvSize(frame->width,frame->height),frame->depth,3);
	IplImage* current_frame = cvCreateImage(cvSize(w,h), frame->depth,3);
	cvCopy(frame,buffer_frame);
	cvSetImageROI(buffer_frame,cvRect(80,110,w,h));
	cvCopy(buffer_frame,current_frame);
	cvResetImageROI(buffer_frame);
	IplImage* current_frame_pro_TY=NULL;
	cvReleaseImage(&buffer_frame);
	if(!back)
	{
		goto after_release;
	}
	
	//std::cout<<"Show: "<<<<conf.show<<std::endl;
	current_frame_pro_TY= objDetection::preprocess_to_single_channel(current_frame,back_img,hsv_min_TY,hsv_max_TY);
	
	cvShowImage("Thresholds",current_frame_pro_TY);
	//objDetection::utilities::colorPicker(current_frame_pro_TY);
	
after_release:
		if(conf.show)
		cvShowImage( "Camera", current_frame );
		if(!back)
		{	
		back=true;
		back_img=current_frame;
		}
		else
		{
			cvReleaseImage(&current_frame);
		}
		
		
		
		if( (cvWaitKey(50) & 255) == 27 ) break;
		if(conf.image_file)
		{
				cvReleaseImage(&frame);
				if(conf.i_base.current==conf.i_base.image_end)
					break;
				conf.i_base.current++;
		}
		
		cvClearMemStorage(storage);
	}
	if(conf.outputfile)
			writer->close();
	cvReleaseMemStorage(&storage);
}
config get_Config(int argc, char* argv[])
{
	config res;
	res.camera=false;
	res.image_file=false;
	res.rankedArea=false;
	res.show=false;
	res.outputToConsole=false;
	res.outputToText=false;
	res.closeObjects=false;
	
	res.hsv_max_TY=hsv_max_TY;
	res.hsv_min_TY=hsv_min_TY;
	
	

	int currentIndex=1;
	if(argv[currentIndex][0]=='c')
	{
		res.camera=true;
	}
	if(argv[currentIndex][0]=='i')
	{
		res.image_file=true;
		res.i_base.current=-1;	
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
	//currentIndex++;
	if(currentIndex==argc)
	{
		return res;
	}
	if(!strcmp(argv[currentIndex],"show"))
		res.show=true;
	if(!strcmp(argv[currentIndex],"outputToText"))
	{
		res.outputToText=true;
		currentIndex++;
		res.outputfile=argv[currentIndex];
	}
	if(!strcmp(argv[currentIndex],"outputToConsole"))
		res.outputToConsole=true;
	currentIndex++;
	if(currentIndex==argc)
	{
		return res;
	}
	if(!strcmp(argv[currentIndex],"show"))
	{
		res.show=true;
	}
	if(!strcmp(argv[currentIndex],"outputToText"))
	{
		res.outputToText=true;
		currentIndex++;
		res.outputfile=argv[currentIndex];
	}
	if(!strcmp(argv[currentIndex],"outputToConsole"))
	{
		res.outputToConsole=true;
	}
	currentIndex++;
	if(currentIndex==argc)
	{
		return res;
	}
	if(!strcmp(argv[currentIndex],"show"))
	{
		res.show=true;
	}
	if(!strcmp(argv[currentIndex],"outputToText"))
	{
		res.outputToText=true;
		currentIndex++;
		res.outputfile=argv[currentIndex];
	}
	if(!strcmp(argv[currentIndex],"outputToConsole"))
	{	
		res.outputToConsole=true;

	}
	return res;
}
// Yellow robot trackbar setting
void change_ty_min_1(int position){
	hsv_min_TY.val[0] = (double) position;
}
void change_ty_max_1(int position){
	hsv_max_TY.val[0] = (double) position;
}
void change_ty_min_2(int position){
	hsv_min_TY.val[1] = (double) position;
}
void change_ty_max_2(int position){
	hsv_max_TY.val[1] = (double) position;
}
void change_ty_min_3(int position){
	hsv_min_TY.val[2] = (double) position;
}
void change_ty_max_3(int position){
	hsv_max_TY.val[2] = (double) position;
}

int main(int argc, char* argv[])
{

	cvNamedWindow( "Camera", CV_WINDOW_AUTOSIZE );
	cvNamedWindow("YellowThreshold",0);
	cvMoveWindow("YellowThreshold", 400, 600);
	
	cvCreateTrackbar( "Yellow_Min_Hue",   "YellowThreshold", &ty_min_1, 255, &change_ty_min_1);
	cvCreateTrackbar( "Yellow_Max_Hue",   "YellowThreshold", &ty_max_1, 255, &change_ty_max_1);
	cvCreateTrackbar( "Yellow_Min_Sat",   "YellowThreshold", &ty_min_2, 255, &change_ty_min_2);
	cvCreateTrackbar( "Yellow_Max_Sat",   "YellowThreshold", &ty_max_2, 255, &change_ty_max_2);
	cvCreateTrackbar( "Yellow_Min_Light", "YellowThreshold", &ty_min_3, 255, &change_ty_min_3);
	cvCreateTrackbar( "Yellow_Max_Light", "YellowThreshold", &ty_max_3, 255, &change_ty_max_3);

	config conf=get_Config(argc,argv);
	launch(conf);
	cvWaitKey(0);
	return 0;
}
