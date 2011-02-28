#include <cvaux.h>
#include <highgui.h>
#include <cxcore.h>
#include <stdio.h>
#include <iostream>
#include <stdio.h>
#include <stdlib.h>
#include <cstring>
#include <string.h>
#include <assert.h>
#include <math.h>
#include <float.h>
#include <limits.h>
#include <time.h>
#include <ctype.h>
#include <iostream>
#include "objdetection.h"
#include "objdetectionutil.h"
#include <sstream>


// Color threshold for Red Ball
CvScalar hsvMinRedBall = cvScalar(0,131,104);
CvScalar hsvMaxRedBall = cvScalar(10,255,255);

// Color threshold for Yellow T 
CvScalar hsvMinYellowPlate = cvScalar(0,8,75);
CvScalar hsvMaxYellowPlate = cvScalar(20,255,255);

// Color threshold for Blue T
CvScalar hsvMinBluePlate = cvScalar(29,14,0);
CvScalar hsvMaxBluePlate = cvScalar(78,255,255);


int64 totalTime;
int Opcount;

void showResuts(IplImage*& frame,IplImage*& thresh1,IplImage*& thresh2,IplImage*& thresh3,int64 diffTime,CvBox2D TB,CvBox2D TY,CvBox2D B,bool display)
{

	IplImage* frame_c	= cvCreateImage(cvSize(frame->width,frame->height),frame->depth,frame->nChannels);
	IplImage* thresh1_c	= cvCreateImage(cvSize(thresh1->width,thresh1->height),frame->depth,frame->nChannels);
	IplImage* thresh2_c	= cvCreateImage(cvSize(thresh2->width,thresh2->height),frame->depth,frame->nChannels);
	IplImage* thresh3_c	= cvCreateImage(cvSize(thresh3->width,thresh3->height),frame->depth,frame->nChannels);

	cvCopy(frame,frame_c);
	cvConvertImage(thresh1,thresh1_c);
	cvConvertImage(thresh2,thresh2_c);

	cvConvertImage(thresh3,thresh3_c);

	IplImage* disp = objDetection::utilities::cvShowManyImages("Camera",4,frame_c,thresh1_c,thresh2_c,thresh3_c);
	totalTime+= diffTime;
	Opcount++;
	float meanfps = 1/((((float)totalTime)/Opcount)/cv::getTickFrequency());
	float fps = 1/(((float)diffTime)/cv::getTickFrequency());
	
	if(disp && display)
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
		ss<<"Blue T pos:"<<TB.center.x<<","<<TB.center.y<<","<<TB.angle*180/PI;
		cvPutText(disp,ss.str().c_str(),point, &font, cvScalar(0, 0, 0, 0));
		ss.str("");
		point.y+=20;
		ss<<"Yellow T pos:"<<TY.center.x<<","<<TY.center.y<<","<<TY.angle*180/PI;
		cvPutText(disp,ss.str().c_str(),point, &font, cvScalar(0, 0, 0, 0));
		ss.str("");
		point.y+=20;
		ss<<"Ball pos:"<<B.center.x<<","<<B.center.y;
		cvPutText(disp,ss.str().c_str(),point, &font, cvScalar(0, 0, 0, 0));
		cvShowImage("Camera",disp);
		
	}
	cvReleaseImage(&disp);
	cvReleaseImage(&frame_c);
	cvReleaseImage(&thresh1_c);
	cvReleaseImage(&thresh2_c);
	cvReleaseImage(&thresh3_c);
}

void launch(config conf)
{

	CvMemStorage* storage=cvCreateMemStorage(0);
	IplImage* frame;
	//CvCapture* capture;
	int key;
	
	int64 startTick=0;
	int64 endTick=0;

	
	totalTime=0;
	Opcount=0;
	
	objDetection::circular_buffer TY_Buffer;
	objDetection::circular_buffer TB_Buffer;
	
	//capture=cvCaptureFromCAM(0);
	
	objDetection::utilities::cb_init(&TY_Buffer,5,sizeof(float));
	objDetection::utilities::cb_init(&TB_Buffer,5,sizeof(float));
	
	
	while(true)
	{
		CvCapture* capture=cvCaptureFromCAM(0);
		
		cvGrabFrame(capture);
		frame = cvRetrieveFrame(capture);
		startTick= cv::getTickCount();
		
		int x = conf.windowOfInterest.X;
		int y = conf.windowOfInterest.Y;
		int w = conf.windowOfInterest.Width;
		int h = conf.windowOfInterest.Height;
		CvSize size = cvSize(w,h);
		CvRect rect = cvRect(x,y,w,h);
		
		
		IplImage* bufferFrame	= cvCreateImage(cvSize(frame->width,frame->height),frame->depth,3);
		IplImage* bufferFrame2	= cvCreateImage(cvSize(frame->width,frame->height),frame->depth,3);
		IplImage* bufferFrame3	= cvCreateImage(size,frame->depth,3);
		IplImage* background	= cvCreateImage(size,frame->depth,3);
		IplImage* currentFrame	= cvCreateImage(size, frame->depth,3);
		
		
		while(conf.bg)
		{
			cvShowImage("Background",frame);
			
			key = cvWaitKey(50);
			
			if (key == 'b')
			{
				cvCopy(frame,bufferFrame);
		
				cvSetImageROI(bufferFrame,rect);
				cvCopy(bufferFrame,background);

				cvResetImageROI(bufferFrame);
				cvReleaseImage(&bufferFrame);
		
				conf.bg = false;
			}
		}
		
		
		cvCopy(frame,bufferFrame2);

		cvSetImageROI(bufferFrame2,rect);
		cvCopy(bufferFrame2,bufferFrame3);

		
		cvSub(bufferFrame3,background,currentFrame);

		cvShowImage("Substracted Image",currentFrame);
		
		cvResetImageROI(bufferFrame2);
		cvReleaseImage(&bufferFrame2);
		cvReleaseImage(&bufferFrame3);
		cvReleaseImage(&background);

		
		/*
		IplImage* processedRedBall	= objDetection::preprocessFrame(currentFrame,hsvMinRedBall,hsvMaxRedBall);
		IplImage* processedBluePlate	= objDetection::preprocessFrame(currentFrame,hsvMinBluePlate,hsvMaxBluePlate);
		IplImage* processedYellowPlate	= objDetection::preprocessFrame(currentFrame,hsvMinYellowPlate,hsvMaxYellowPlate);
		
		
		CvContour* contourBall		= objDetection::getContours(processedRedBall,storage);
		CvContour* contourBlue		= objDetection::getContours(processedBluePlate,storage);
		CvContour* contourYellow	= objDetection::getContours(processedYellowPlate,storage);


		cvDrawContours(currentFrame,(CvSeq*) contourBall,cvScalar(0,0,255),cvScalar(0,0,0),0,2,8);
		
		
		CvBox2D boxBall;
		CvBox2D boxBlue;
		CvBox2D boxYellow;
		
		boxBall.center.x= -1;
		boxBall.center.y= -1;
		
		boxBlue.center.x= -1;
		boxBlue.center.y= -1;
		boxBlue.angle= -1;
		
		boxYellow.center.x= -1;
		boxYellow.center.y= -1;
		boxYellow.angle= -1;
		
				std::cout<<"before"<<std::endl;
		
		boxBall		= cvMinAreaRect2(contourBall);
		
				std::cout<<"before1"<<std::endl;
		
		boxBlue		= objDetection::orientationSecondOrderMoments(contourBlue);
		boxYellow	= objDetection::orientationSecondOrderMoments(contourYellow);
		
		
		stringstream ss;
		ss
		<<boxBlue.center.x<<","<<boxBlue.center.y<<","<<boxBlue.angle<<","
		<<boxBall.center.x<<","<<boxBall.center.y<<","
		<<boxYellow.center.x<<","<<boxYellow.center.y<<","<<boxYellow.angle
		<<std::endl;
		
		std::cerr<<ss.str();

		endTick = cv::getTickCount();
		cvClearMemStorage(storage);
		
		
		if(conf.show)
		{
			showResuts(currentFrame,processedBluePlate,processedYellowPlate,processedRedBall,endTick-startTick,boxBlue,boxYellow,boxBall,true);
			if((cvWaitKey(10) & 255) == 27) break;
		}
		*/
		
		cvReleaseImage(&currentFrame);
		//cvReleaseImage(&processedBluePlate);
		//cvReleaseImage(&processedYellowPlate);
		//cvReleaseImage(&processedRedBall);
		cvReleaseCapture(&capture);
	}
}

config getConfig(int argc, char* argv[])
{
	config conf;
	conf.bg = false;
	//conf.show=false;

	conf.windowOfInterest.X=80;
	conf.windowOfInterest.Y= 110;
	conf.windowOfInterest.Width=540;
	conf.windowOfInterest.Height= 290;
	
	
	int currentIndex=1;
	
	if(!strcmp(argv[currentIndex],"background"))
	{
		conf.bg=true;
	}
	
	/*
	if(!strcmp(argv[currentIndex],"show"))
	{
		conf.show=true;
	}
	*/

	return conf;
}

int main(int argc, char* argv[])
{
	cvNamedWindow("Background", CV_WINDOW_AUTOSIZE);
	
	config conf = getConfig(argc,argv);
	launch(conf);
	
	return 0;
}
