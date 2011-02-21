#include <cvaux.h>
#include <highgui.h>
#include <cxcore.h>
#include <stdio.h>
#include <iostream>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <cstring>
#include <assert.h>
#include <math.h>
#include <float.h>
#include <limits.h>
#include <time.h>
#include <ctype.h>
#include <iostream>
#include "objdetection.h"
#include <sstream>

// MIN values for black dot
int d_min_1	= 26;
int d_min_2	= 81;
int d_min_3	= 218;
// MAX values for black dot
int d_max_1	= 85;
int d_max_2	= 251;
int d_max_3	= 255;

// MIN values for red ball
int b_min_1	= 0;
int b_min_2	= 161;
int b_min_3	= 114;
// MAX values for red ball
int b_max_1	= 10;
int b_max_2	= 255;
int b_max_3	= 255;

// MIN values for yellow robot
int ty_min_1	= 23;
int ty_min_2	= 131;
int ty_min_3	= 172;
// MAX values for yellow robot
int ty_max_1	= 57;
int ty_max_2	= 255;
int ty_max_3	= 255;

// MIN values for blue robot
int tb_min_1	= 84;
int tb_min_2	= 103;
int tb_min_3	= 173;
// MAX values for blue robot
int tb_max_1	= 110;
int tb_max_2	= 255;
int tb_max_3	= 255;


// Color threshold for Black Dot
CvScalar hsv_min_D = cvScalar(d_min_1, d_min_2, d_min_3);
CvScalar hsv_max_D = cvScalar(d_max_1, d_max_2, d_max_3);

// Color threshold for Red Ball
CvScalar hsv_min_B = cvScalar(b_min_1, b_min_2, b_min_3);
CvScalar hsv_max_B = cvScalar(b_max_1, b_max_2, b_max_3);

// Color threshold for Yellow T 
CvScalar hsv_min_TY = cvScalar(ty_min_1, ty_min_2, ty_min_3);
CvScalar hsv_max_TY = cvScalar(ty_max_1, ty_max_2, ty_max_3);

// Color threshold for Blue T
CvScalar hsv_min_TB = cvScalar(tb_min_1, tb_min_2, tb_min_3);
CvScalar hsv_max_TB = cvScalar(tb_max_1, tb_max_2, tb_max_3);



void launch(config conf)
{
	CvMemStorage* storage=cvCreateMemStorage(0);
	IplImage* frame;
	CvCapture* capture;
	
	ofstream* writer;
	if(conf.outputToText) writer=new ofstream(conf.outputfile);
	if(conf.camera)
	{
		std::cout<<"Going for Camera"<<std::endl;
		capture=cvCaptureFromCAM(0);
	}
	if(conf.image_file) conf.i_base.current=conf.i_base.image_start;
	
	while(true)
	{
		if(conf.camera)
		{
			if(!cvGrabFrame(capture))
			{
				std::cout<<"Camera Stopped working, Closing"<<std::endl;
				break;
			}
			frame=cvRetrieveFrame(capture);
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
		int w=550;
		int h=300; 
		IplImage* buffer_frame  = cvCreateImage(cvSize(frame->width,frame->height),frame->depth,3);
		IplImage* current_frame = cvCreateImage(cvSize(w,h), frame->depth,3);
		IplImage* current_frame_pro_TB;
		IplImage* current_frame_pro_TY;
		IplImage* current_frame_pro_B;
		IplImage* current_frame_pro_D;
	
		cvCopy(frame,buffer_frame);
		cvSetImageROI(buffer_frame,cvRect(70,100,w,h));
		cvCopy(buffer_frame,current_frame);
		cvResetImageROI(buffer_frame);
		cvReleaseImage(&buffer_frame);
		
		IplImage* bg=cvLoadImage("bg.jpg");
		cvSetImageROI(bg,cvRect(70,100,w,h));

		current_frame_pro_TB=objDetection::preprocess_to_single_channel(current_frame,bg,hsv_min_TB,hsv_max_TB,true,false);
		if(!current_frame_pro_TB) std::cout<<"Error in transforming image"<<std::endl;
		cvShowImage("Blue",current_frame_pro_TB);

		current_frame_pro_TY=objDetection::preprocess_to_single_channel(current_frame,bg,hsv_min_TY,hsv_max_TY,true,false);
		if(!current_frame_pro_TY) std::cout<<"Error in transforming image"<<std::endl;
		cvShowImage("Yellow",current_frame_pro_TY);

		current_frame_pro_B=objDetection::preprocess_to_single_channel(current_frame,bg,hsv_min_B,hsv_max_B,true,false);
		if(!current_frame_pro_B) std::cout<<"Error in transforming image"<<std::endl;
		cvShowImage("Ball",current_frame_pro_B);

		current_frame_pro_D=objDetection::preprocess_to_single_channel(current_frame,bg,hsv_min_D,hsv_max_D,true,false);
		if(!current_frame_pro_D) std::cout<<"Error in transforming image"<<std::endl;
		cvShowImage("Dot",current_frame_pro_D);
		
		cvResetImageROI(bg);

		// Find objects based on the biggest contours in the image producing only positions
		if(conf.rankedArea)
		{
			
			CvContour* cnt_TB;
			CvContour* cnt_TY;
			CvContour* cnt_B;
			CvContour* cnt_D;
		
			cnt_TB	=objDetection::rankedArea(current_frame_pro_TB,storage);
			cnt_TY	=objDetection::rankedArea(current_frame_pro_TY,storage);
			cnt_B	=objDetection::rankedArea(current_frame_pro_B,storage);
			cnt_D	=objDetection::rankedArea(current_frame_pro_D,storage);
		
			// Draw contours on screen
			if(conf.show)
			{
				try
				{
					cvDrawContours(current_frame,(CvSeq*)(cnt_TB),cvScalar(255,0,0),cvScalar(0,0,255),0,2,8);
					cvDrawContours(current_frame,(CvSeq*)(cnt_TY),cvScalar(0,255,255),cvScalar(0,0,0),0,2,8);
					cvDrawContours(current_frame,(CvSeq*)(cnt_B),cvScalar(0,0,255),cvScalar(0,0,0),0,2,8);
					cvDrawContours(current_frame,(CvSeq*)(cnt_D),cvScalar(0,255,0),cvScalar(0,0,0),0,2,8);
				}
				catch(std::exception ex)
				{
					continue;
				}
			}
			// outputToConsole: write results on stderr
			if(conf.outputToText||conf.outputToConsole)
			{
				try
				{
					if(!cnt_TB||!cnt_TY||!cnt_B) continue;
					CvRect rect_TB= cvBoundingRect(cnt_TB);
					CvRect rect_TY= cvBoundingRect(cnt_TY);
					CvRect rect_B= cvBoundingRect(cnt_B);
					stringstream ss;
					ss<<rect_TB.x<<","<<rect_TB.y<<","<<"NaN,"<<rect_B.x<<","<<rect_B.y<<","<<rect_TY.x<<","<<rect_TY.y<<",NaN,"<<std::endl;
					if(conf.outputToText) *writer<<ss.str();
					if(conf.outputToConsole) std::cerr<<ss.str();
				}
				catch(std::exception ex)
				{
					continue;
				}
			
			}
		}
	
		// Compute orientation using blackspot at the end of the plate
		if(conf.closeObjects)
		{
			//std::cout<<"Starting Cycle"<<std::endl;
			CvBox2D bbox_TY;
			CvBox2D bbox_TB;
			CvContour* cnt_B;
			bbox_TY=objDetection::DotCloseObjectDetection(current_frame_pro_TY,current_frame_pro_D,storage,conf);
			//std::cout<<"yellow"<<std::endl;
			bbox_TB=objDetection::DotCloseObjectDetection(current_frame_pro_TB,current_frame_pro_D,storage,conf);
			//std::cout<<"blue"<<std::endl;
			cnt_B= objDetection::rankedArea(current_frame_pro_B,storage);
			
			if(conf.show)
			{
				//std::cout<<"Drawing"<<std::endl;
				objDetection::drawOrientation(current_frame,bbox_TB,cvScalar(255,0,0));
				objDetection::drawOrientation(current_frame,bbox_TY,cvScalar(0,255,255));
				cvDrawContours(current_frame,(CvSeq*)(cnt_B),cvScalar(0,0,255),cvScalar(0,0,0),0,2,8);
				//std::cout<<"End of Drawing"<<std::endl;
			}
			if(conf.outputToText || conf.outputToConsole)
			{
				try
				{
					//sleep(1);
					if(!cnt_B) continue;
					CvRect rect_B= cvBoundingRect(cnt_B);
					stringstream ss;
					ss<<bbox_TB.center.x<<","<<bbox_TB.center.y<<","<<bbox_TB.angle<<","<<rect_B.x<<","<<rect_B.y<<","<<bbox_TY.center.x<<","<<bbox_TY.center.y<<","<<bbox_TY.angle<<std::endl;
					if(conf.outputToText) *writer<<ss.str();
					if(conf.outputToConsole) std::cerr<<ss.str();
				}
				catch(std::exception ex)
				{
					continue;
				}
			}
			/*
			if(conf.outputToConsole)
			{
				try
				{
					if(!cnt_B) continue;
					CvRect rect_B= cvBoundingRect(cnt_B);
					
					/*
					std::cout<<" "<<std::endl;
					
					std::cout<<"BLUE X: "<<bbox_TB.center.x<<std::endl;
					std::cout<<"BLUE Y: "<<bbox_TB.center.y<<std::endl;
					std::cout<<"BLUE A: "<<bbox_TB.angle<<std::endl;
					
					std::cout<<" "<<std::endl;
					
					std::cout<<"RED X: "<<rect_B.x<<std::endl;
					std::cout<<"RED Y: "<<rect_B.y<<std::endl;
					
					std::cout<<" "<<std::endl;
					
					std::cout<<"YELLOW X: "<<bbox_TY.center.x<<std::endl;
					std::cout<<"YELLOW Y: "<<bbox_TY.center.y<<std::endl;
					std::cout<<"YELLOW A: "<<bbox_TY.angle<<std::endl;
					
					
					std::cout<<bbox_TB.center.x<<","<<bbox_TB.center.y<<","<<bbox_TB.angle<<","<<rect_B.x<<","<<rect_B.y<<","<<bbox_TY.center.x<<","<<bbox_TY.center.y<<","<<bbox_TY.angle<<std::endl;
				}
				catch(std::exception ex)
				{
					continue;
				}
			}
			*/
			
			//std::cout<<"Ending Cycle"<<std::endl;
		}

		cvReleaseImage(&current_frame_pro_TB);
		cvReleaseImage(&current_frame_pro_TY);
		cvReleaseImage(&current_frame_pro_B);
		cvReleaseImage(&current_frame_pro_D);
	
		// Show result on screen
		if(conf.show) cvShowImage("Camera",current_frame);
		
		cvReleaseImage(&current_frame);
		
		if((cvWaitKey(50)&255)==27) break;
		if(conf.image_file)
		{
			cvReleaseImage(&current_frame);
			if(conf.i_base.current==conf.i_base.image_end) break;
			conf.i_base.current++;
		}

		cvClearMemStorage(storage);
	}
	
	if(conf.outputfile) writer->close();
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
	res.hsv_max_B=hsv_max_B;
	res.hsv_max_D=hsv_max_D;
	res.hsv_max_TB=hsv_max_TB;
	res.hsv_max_TY=hsv_max_TY;
	res.hsv_min_B=hsv_min_B;
	res.hsv_min_TB=hsv_min_TB;
	res.hsv_min_TY=hsv_min_TY;
	res.hsv_min_D=hsv_min_D;


	int currentIndex=1;
	if(argv[currentIndex][0]=='c') res.camera=true;
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
		res.i_base.image_end=atoi(argv[currentIndex]);
		currentIndex++;
	}
	
	
	if(!strcmp(argv[currentIndex],"rankedArea")) res.rankedArea=true;
	if(!strcmp(argv[currentIndex],"closeObjects")) res.closeObjects=true;
	currentIndex++;
	if(currentIndex==argc) return res;
	if(!strcmp(argv[currentIndex],"show")) res.show=true;
	if(!strcmp(argv[currentIndex],"outputToText"))
	{
		res.outputToText=true;
		currentIndex++;
		res.outputfile=argv[currentIndex];
	}
	if(!strcmp(argv[currentIndex],"outputToConsole")) res.outputToConsole=true;
	currentIndex++;
	if(currentIndex==argc) return res;
	if(!strcmp(argv[currentIndex],"show")) res.show=true;
	if(!strcmp(argv[currentIndex],"outputToText"))
	{
		res.outputToText=true;
		currentIndex++;
		res.outputfile=argv[currentIndex];
	}
	if(!strcmp(argv[currentIndex],"outputToConsole")) res.outputToConsole=true;
	currentIndex++;
	if(currentIndex==argc) return res;
	if(!strcmp(argv[currentIndex],"show")) res.show=true;
	if(!strcmp(argv[currentIndex],"outputToText"))
	{
		res.outputToText=true;
		currentIndex++;
		res.outputfile=argv[currentIndex];
	}
	if(!strcmp(argv[currentIndex],"outputToConsole")) res.outputToConsole=true;
	return res;
}

// --------------------------
// Black dot trackbar setting
void change_d_min_1(int position){
	hsv_min_D.val[0] = (double) position;
}
void change_d_max_1(int position){
	hsv_max_D.val[0] = (double) position;
}
void change_d_min_2(int position){
	hsv_min_D.val[1] = (double) position;
}
void change_d_max_2(int position){
	hsv_max_D.val[1] = (double) position;
}
void change_d_min_3(int position){
	hsv_min_D.val[2] = (double) position;
}
void change_d_max_3(int position){
	hsv_max_D.val[2] = (double) position;
}

// --------------------------
// Red ball trackbar setting
void change_b_min_1(int position){
	hsv_min_B.val[0] = position;
}
void change_b_max_1(int position){
	hsv_max_B.val[0] = position;
}
void change_b_min_2(int position){
	hsv_min_B.val[1] = position;
}
void change_b_max_2(int position){
	hsv_max_B.val[1] = position;
}
void change_b_min_3(int position){
	hsv_min_B.val[2] = position;
}
void change_b_max_3(int position){
	hsv_max_B.val[2] = position;
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

// ---------------------------
// Blue robot trackbar setting
void change_tb_min_1(int position){
	hsv_min_TB.val[0] = (double) position;
}
void change_tb_max_1(int position){
	hsv_max_TB.val[0] = (double) position;
}
void change_tb_min_2(int position){
	hsv_min_TB.val[1] = (double) position;
}
void change_tb_max_2(int position){
	hsv_max_TB.val[1] = (double) position;
}
void change_tb_min_3(int position){
	hsv_min_TB.val[2] = (double) position;
}
void change_tb_max_3(int position){
	hsv_max_TB.val[2] = (double) position;
}


int main(int argc, char* argv[])
{
	config conf=get_Config(argc,argv);
	
	//*
	cvNamedWindow("Camera",CV_WINDOW_AUTOSIZE);
	cvNamedWindow("DotThreshold",0);
	cvMoveWindow("DotThreshold", 800, 100);
	cvNamedWindow("BallThreshold",0);
	cvMoveWindow("BallThreshold", 0, 600);
	cvNamedWindow("YellowThreshold",0);
	cvMoveWindow("YellowThreshold", 400, 600);
	cvNamedWindow("BlueThreshold",0);
	cvMoveWindow("BlueThreshold", 800, 600);
	
	cvCreateTrackbar( "Dot_Min_Hue",   "DotThreshold", &d_min_1, 255, &change_d_min_1);
	cvCreateTrackbar( "Dot_Max_Hue",   "DotThreshold", &d_max_1, 255, &change_d_max_1);
	cvCreateTrackbar( "Dot_Min_Sat",   "DotThreshold", &d_min_2, 255, &change_d_min_2);
	cvCreateTrackbar( "Dot_Max_Sat",   "DotThreshold", &d_max_2, 255, &change_d_max_2);
	cvCreateTrackbar( "Dot_Min_Light", "DotThreshold", &d_min_3, 255, &change_d_min_3);
	cvCreateTrackbar( "Dot_Max_Light", "DotThreshold", &d_max_3, 255, &change_d_max_3);

	cvCreateTrackbar( "Ball_Min_Hue",   "BallThreshold", &b_min_1, 255, &change_b_min_1);
	cvCreateTrackbar( "Ball_Max_Hue",   "BallThreshold", &b_max_1, 255, &change_b_max_1);
	cvCreateTrackbar( "Ball_Min_Sat",   "BallThreshold", &b_min_2, 255, &change_b_min_2);
	cvCreateTrackbar( "Ball_Max_Sat",   "BallThreshold", &b_max_2, 255, &change_b_max_2);
	cvCreateTrackbar( "Ball_Min_Light", "BallThreshold", &b_min_3, 255, &change_b_min_3);
	cvCreateTrackbar( "Ball_Max_Light", "BallThreshold", &b_max_3, 255, &change_b_max_3);

	cvCreateTrackbar( "Yellow_Min_Hue",   "YellowThreshold", &ty_min_1, 255, &change_ty_min_1);
	cvCreateTrackbar( "Yellow_Max_Hue",   "YellowThreshold", &ty_max_1, 255, &change_ty_max_1);
	cvCreateTrackbar( "Yellow_Min_Sat",   "YellowThreshold", &ty_min_2, 255, &change_ty_min_2);
	cvCreateTrackbar( "Yellow_Max_Sat",   "YellowThreshold", &ty_max_2, 255, &change_ty_max_2);
	cvCreateTrackbar( "Yellow_Min_Light", "YellowThreshold", &ty_min_3, 255, &change_ty_min_3);
	cvCreateTrackbar( "Yellow_Max_Light", "YellowThreshold", &ty_max_3, 255, &change_ty_max_3);

	cvCreateTrackbar( "Blue_Min_Hue",   "BlueThreshold", &tb_min_1, 255, &change_tb_min_1);
	cvCreateTrackbar( "Blue_Max_Hue",   "BlueThreshold", &tb_max_1, 255, &change_tb_max_1);
	cvCreateTrackbar( "Blue_Min_Sat",   "BlueThreshold", &tb_min_2, 255, &change_tb_min_2);
	cvCreateTrackbar( "Blue_Max_Sat",   "BlueThreshold", &tb_max_2, 255, &change_tb_max_2);
	cvCreateTrackbar( "Blue_Min_Light", "BlueThreshold", &tb_min_3, 255, &change_tb_min_3);
	cvCreateTrackbar( "Blue_Max_Light", "BlueThreshold", &tb_max_3, 255, &change_tb_max_3);
	
	//*/
	
	launch(conf);
	
	//*
	cvDestroyWindow("Camera");
	cvDestroyWindow("DotThreshold");
	cvDestroyWindow("BallThreshold");
	cvDestroyWindow("YellowThreshold");
	cvDestroyWindow("BlueThreshold");
	//*/
	
	return 0;
}
