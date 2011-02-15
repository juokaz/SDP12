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
#include <float.h>
#include <limits.h>
#include <time.h>
#include <ctype.h>
#include <iostream>
#include "..\..\ObjectDetection\src\objdetection.h"
#include "objdetectionutil.h"
#include <sstream>


// Color threshold for Black Dot

CvScalar hsv_min_D = cvScalar(0, 39, 79, 0);//cvScalar(0, 39, 79, 0);//CvScalar hsv_min_D = cvScalar(11, 30, 60, 0);
CvScalar hsv_max_D = cvScalar(4, 140, 220, 0);//CvScalar hsv_max_D = cvScalar(80, 70, 120, 0);

void launch(config conf)
{	

	CvMemStorage* storage=cvCreateMemStorage(0);
	IplImage* frame;
	IplImage* back_img;
	CvCapture* capture;
	bool back=false;
	
	ofstream writer;
	if(conf.outputToText)
		writer=ofstream(conf.outputfile);
	
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
			if(!back)
			{
				std::cout<<"Setup for Background image"<<std::endl;
			if( !cvGrabFrame( capture ))
			{
				std::cout<<"Camera Stopped working, Closing"<<std::endl;
               break;
			}
            frame = cvRetrieveFrame( capture );
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
	int w=720;
	int h=380; 
	IplImage* buffer_frame  = cvCreateImage(cvSize(frame->width,frame->height),frame->depth,3);
	IplImage* current_frame = cvCreateImage(cvSize(w,h), frame->depth,3);
	cvCopy(frame,buffer_frame);
	cvSetImageROI(buffer_frame,cvRect(20,100,w,h));
	cvCopy(buffer_frame,current_frame);
	cvResetImageROI(buffer_frame);
	
	cvReleaseImage(&buffer_frame);
	if(!back)
	{
		goto after_release;
	}
	
	
	IplImage* current_frame_pro_D= objDetection::preprocess_to_single_channel(current_frame,back_img,conf.hsv_min_D,conf.hsv_max_D);
	objDetection::utilities::colorPicker(current_frame_pro_D);
	
after_release:
		if(!back)
		{	
		back=true;
		back_img=current_frame;
		}
		else
		{
			cvReleaseImage(&current_frame);
		}
		if(conf.show)
		cvShowImage( "Camera", current_frame );
		
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
			writer.close();
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
	
	res.hsv_max_D=hsv_max_D;
	res.hsv_min_D=hsv_min_D;
	
	

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
	currentIndex++;
	if(currentIndex==argc)
	{
		return res;
	}
	if(!std::strcmp(argv[currentIndex],"show"))
		res.show=true;
	if(!std::strcmp(argv[currentIndex],"outputToText"))
	{
		res.outputToText=true;
		currentIndex++;
		res.outputfile=argv[currentIndex];
	}
	if(!std::strcmp(argv[currentIndex],"outputToConsole"))
		res.outputToConsole=true;
	currentIndex++;
	if(currentIndex==argc)
	{
		return res;
	}
	if(!std::strcmp(argv[currentIndex],"show"))
	{
		res.show=true;
	}
	if(!std::strcmp(argv[currentIndex],"outputToText"))
	{
		res.outputToText=true;
		currentIndex++;
		res.outputfile=argv[currentIndex];
	}
	if(!std::strcmp(argv[currentIndex],"outputToConsole"))
	{
		res.outputToConsole=true;
	}
	currentIndex++;
	if(currentIndex==argc)
	{
		return res;
	}
	if(!std::strcmp(argv[currentIndex],"show"))
	{
		res.show=true;
	}
	if(!std::strcmp(argv[currentIndex],"outputToText"))
	{
		res.outputToText=true;
		currentIndex++;
		res.outputfile=argv[currentIndex];
	}
	if(!std::strcmp(argv[currentIndex],"outputToConsole"))
	{	
		res.outputToConsole=true;

	}
	return res;
}
int main(int argc, char* argv[])
{
	

	
    cvNamedWindow( "Camera", CV_WINDOW_AUTOSIZE );
    
	config conf=get_Config(argc,argv);
	launch(conf);
	cvWaitKey(0);
    return 0;
   }
