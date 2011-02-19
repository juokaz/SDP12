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
#include "../../../ObjectDetection/src/objdetection.h"
#include "objdetectionml.h"
#include <sstream>

// Color threshold for Black Dot

CvScalar hsv_min_D = cvScalar(0, 25, 0, 0);//CvScalar hsv_min_D = cvScalar(11, 30, 60, 0);
CvScalar hsv_max_D = cvScalar(146, 200, 200, 0);//CvScalar hsv_max_D = cvScalar(80, 70, 120, 0);

// Color threshold for Red Ball

CvScalar hsv_min_B = cvScalar(0, 161, 114, 0);//CvScalar hsv_min_B = cvScalar(0, 120, 100, 0);
CvScalar hsv_max_B = cvScalar(10, 255, 255, 255);//CvScalar hsv_max_B = cvScalar(10, 255, 200, 255)

// Color threshold for Yellow T 
	
CvScalar hsv_min_TY = cvScalar(0, 39, 79, 0);//cvScalar(0, 39, 79, 0);//CvScalar hsv_min_D = cvScalar(11, 30, 60, 0);
CvScalar hsv_max_TY = cvScalar(4, 140, 220, 0);//CvScalar hsv_max_TY = cvScalar(40, 255, 255, 255);

// Color threshold for Blue T

CvScalar hsv_min_TB = cvScalar(30, 30, 50, 0);//CvScalar hsv_min_D = cvScalar(11, 30, 60, 0);
CvScalar hsv_max_TB = cvScalar(90, 105, 140, 0);//CvScalar hsv_max_TB = cvScalar(110, 255, 255, 255);


void launch(config conf)	
{	

	CvMemStorage* storage=cvCreateMemStorage(0);
	IplImage* frame;
	IplImage* back_img;
	bool back=false;
	CvCapture* capture;
	
	std::vector<CvContour*> selectedDataSet_Ball;
	std::vector<CvContour*> rejectedDataSet_Ball;
	std::vector<CvContour*> selectedDataSet_D;
	std::vector<CvContour*> rejectedDataSet_D;
	std::vector<CvContour*> selectedDataSet_TY;
	std::vector<CvContour*> rejectedDataSet_TY;
	std::vector<CvContour*> selectedDataSet_TB;
	std::vector<CvContour*> rejectedDataSet_TB;
	std::vector<CvContour*> selectedDataSet_B;
	std::vector<CvContour*> rejectedDataSet_B;
	std::vector<CvContour*> selectedDataSet_D_TY;
	std::vector<CvContour*> rejectedDataSet_D_TY;
	std::vector<CvContour*> selectedDataSet_D_TB;
	std::vector<CvContour*> rejectedDataSet_D_TB;

	std::vector<objDetection::machineLearning::ContourTuple> selectedtuples_DataSet_TY;
	std::vector<objDetection::machineLearning::ContourTuple> rejectedtuples_DataSet_TY;
	std::vector<objDetection::machineLearning::ContourTuple> selectedtuples_DataSet_TB;
	std::vector<objDetection::machineLearning::ContourTuple> rejectedtuples_DataSet_TB;
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
		if(conf.i_base.current>=conf.i_base.image_end)
			break;
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
	std::cout<<"reformat"<<std::endl;
	int w=540;
	int h=290; 
	CvSize curSize=cvSize(frame->width,frame->height);
	IplImage* buffer_frame  = cvCreateImage(cvSize(frame->width,frame->height),frame->depth,3);
	IplImage* current_frame = cvCreateImage(cvSize(w,h), frame->depth,3);
	IplImage* current_frame_pro_TB;
	IplImage* current_frame_pro_TY;
	IplImage* current_frame_pro_B;
	IplImage* current_frame_pro_D;
	std::cout<<curSize.width<<" "<<curSize.height<<std::endl;
	cvCopy(frame,buffer_frame);
	cvSetImageROI(buffer_frame,cvRect(80,110,w,h));
	cvCopy(buffer_frame,current_frame);
	cvResetImageROI(buffer_frame);
	
	cvReleaseImage(&buffer_frame);
	
	std::cout<<"reformat completed"<<std::endl;
	if(!back)
		goto after_release;
	current_frame_pro_TB=objDetection::preprocess_to_single_channel(current_frame,back_img,conf.hsv_min_TB,conf.hsv_max_TB);
	if(!current_frame_pro_TB)
	{
		std::cout<<"Error in transforming image"<<std::endl;
	}
	current_frame_pro_TY=objDetection::preprocess_to_single_channel(current_frame,back_img,conf.hsv_min_TY,conf.hsv_max_TY);
	if(!current_frame_pro_TY)
	{
		std::cout<<"Error in transforming image"<<std::endl;
	}
	current_frame_pro_B=objDetection::preprocess_to_single_channel(current_frame,conf.hsv_min_B,conf.hsv_max_B);
	if(!current_frame_pro_B)
	{
		std::cout<<"Error in transforming image"<<std::endl;
	}
	current_frame_pro_D=objDetection::preprocess_to_single_channel(current_frame,back_img,conf.hsv_min_D,conf.hsv_max_D,true,true);
	if(!current_frame_pro_D)
	{
		std::cout<<"Error in transforming image"<<std::endl;
	}
	std::cout<<"Color transformation completed"<<std::endl;
			
	if(conf.train_major||conf.train_minor)
		{
			std::vector<CvContour*> cnt= objDetection::getContours(current_frame_pro_B,storage);
			objDetection::machineLearning::trainDataSet("Find Ball",current_frame,cnt,selectedDataSet_B,rejectedDataSet_B);
			
			cnt= objDetection::getContours(current_frame_pro_TY,storage);
			objDetection::machineLearning::trainDataSet("Find TY",current_frame,cnt,selectedDataSet_TY,rejectedDataSet_TY);
			
			cnt= objDetection::getContours(current_frame_pro_TB,storage);
			objDetection::machineLearning::trainDataSet("Find TB",current_frame,cnt,selectedDataSet_TB,rejectedDataSet_TB);

			cnt= objDetection::getContours(current_frame_pro_D,storage);
			objDetection::machineLearning::trainDataSet("Find D",current_frame,cnt,selectedDataSet_D,rejectedDataSet_D);
		
	if(conf.train_minor)
		{
			
			std::vector<CvContour*> cnt_D= objDetection::getContours(current_frame_pro_D,storage);
			objDetection::machineLearning::trainDataSet("Find D Near TY",current_frame,cnt_D,selectedDataSet_D_TY,rejectedDataSet_D_TY);
			objDetection::machineLearning::train_bind(selectedDataSet_D_TY,rejectedDataSet_D_TY,selectedDataSet_TY,rejectedDataSet_TY,selectedtuples_DataSet_TY,rejectedtuples_DataSet_TY);
			objDetection::machineLearning::trainDataSet("Find D Near TB",current_frame,cnt_D,selectedDataSet_D_TB,rejectedDataSet_D_TB);
			objDetection::machineLearning::train_bind(selectedDataSet_D_TB,rejectedDataSet_D_TB,selectedDataSet_TB,rejectedDataSet_TB,selectedtuples_DataSet_TB,rejectedtuples_DataSet_TB);
			
			
		}
	}
	if(conf.predict_major||conf.predict_minor)
		{
			CvContour* cnt_TB=NULL;
			CvContour* cnt_TY=NULL;
			CvContour* cnt_B=NULL;
			std::vector<CvContour*> cnt_D;
			std::vector<CvContour*> cnt;
			cnt= objDetection::machineLearning::tester_image_major(current_frame_pro_B,MODEL_MAJOR_NAME_B,storage);
			if(cnt.size()!=0)
			cnt_B=cnt.at(0);
			cnt.clear();
			cnt= objDetection::machineLearning::tester_image_major(current_frame_pro_TB,MODEL_MAJOR_NAME_TB,storage);
			if(cnt.size()!=0)
			cnt_TB=cnt.at(0);
			cnt.clear();
			cnt= objDetection::machineLearning::tester_image_major(current_frame_pro_TY,MODEL_MAJOR_NAME_TY,storage);
			if(cnt.size()!=0)
			cnt_TY=cnt.at(0);

			cnt= objDetection::machineLearning::tester_image_major(current_frame_pro_D,MODEL_MAJOR_NAME_D,storage);
			cnt_D=cnt;
			std::cout<<"Prediction completed"<<std::endl;

			if(conf.predict_major & cnt_D.size()>0)
			{
				
				CvBox2D sel_TB;
				CvBox2D sel_TY;
				CvBox2D sel_D;
				if(cnt_TB!=NULL)
					sel_TB=objDetection::orientation_centerMoment(cnt_TB,current_frame);//,cnt_D,current_frame);
				if(cnt_TY!=NULL)
					sel_TY=objDetection::orientation_centerMoment(cnt_TY,current_frame);//,cnt_D,current_frame);
				std::cout<<"Orientation Calc completed"<<std::endl;
			if(conf.show & cnt_D.size()>0)
			{
				
				try
				{
				//cvDrawContours(current_frame,(CvSeq*)(cnt_TB),cvScalar(0,0,0),cvScalar(0,0,0),0,2,8);
				//cvDrawContours(current_frame,(CvSeq*)(cnt_TY),cvScalar(0,0,0),cvScalar(0,0,0),0,2,8);
				cvDrawContours(current_frame,(CvSeq*)(cnt_B),cvScalar(0,0,0),cvScalar(0,0,0),0,2,8);
				//std::vector<CvContour*> cnt_dot=objDetection::getContours(current_frame_pro_D,storage);
				
				

				if(cnt_TB!=NULL)
					objDetection::drawOrientation(current_frame,sel_TB);
				if(cnt_TY!=NULL)
					objDetection::drawOrientation(current_frame,sel_TY);
				}
				catch(std::exception ex)
				{
					continue;
				}
			}
			if(conf.outputToText||conf.outputToConsole)
			{
				try
				{
				std::cout<<"outputToConsole"<<std::endl;
				stringstream ss;
				if(cnt_TB)
					ss<<sel_TB.center.x<<","<<sel_TB.center.y<<","<<sel_TB.angle<<",";
				else
					ss<<"-1,-1,-1,";
				if(cnt_B)
				{	
					CvRect rect_B= cvBoundingRect(cnt_B);	
					ss<<rect_B.x<<","<<rect_B.y<<",";
				}
				else
					ss<<"-1,-1,";
				if(cnt_TY)
					ss<<sel_TY.center.x<<","<<sel_TY.center.y<<","<<sel_TY.angle;
				else
					ss<<"-1,-1,-1";
				ss<<std::endl;
				
				if(conf.outputToText)
					*writer<<ss.str();
				if(conf.outputToConsole)
					std::cerr<<ss.str();
				}
				catch(std::exception ex)
				{
					continue;
				}
				
			}
			}
			if(conf.predict_minor)
			{
				
				CvBox2D sel_TB;
				CvBox2D sel_TY;	
				if(cnt_TB!=NULL)
				{
				std::vector<CvBox2D> TB= objDetection::machineLearning::tester_image_minor(current_frame_pro_D,MODEL_MINOR_NAME_TB,cnt_TB,storage,current_frame);
				if(TB.size()>0)
				sel_TB=TB.at(0);
				}
				if(cnt_TY!=NULL)
				{
				std::vector<CvBox2D> TY= objDetection::machineLearning::tester_image_minor(current_frame_pro_D,MODEL_MINOR_NAME_TY,cnt_TY,storage,current_frame);
				if(TY.size()>0)
				sel_TY=TY.at(0);
				}
				if(conf.show)
				{
					if(cnt_TB!=NULL)
						objDetection::drawOrientation(current_frame,sel_TB);
					if(cnt_TY!=NULL)
						objDetection::drawOrientation(current_frame,sel_TY);
						cvDrawContours(current_frame,(CvSeq*)(cnt_B),cvScalar(0,0,0),cvScalar(0,0,0),0,2,8);
				
				}
			if(conf.outputToText||conf.outputToConsole)
			{
				try
				{
				if(!cnt_B)
					continue;
				
				CvRect rect_B= cvBoundingRect(cnt_B);
				stringstream ss;
				ss<<sel_TB.center.x<<","<<sel_TB.center.y<<","<<sel_TB.angle<<","<<rect_B.x<<","<<rect_B.y<<","<<sel_TY.center.x<<","<<sel_TY.center.y<<","<<sel_TY.angle<<std::endl;
				if(conf.outputToText)
					*writer<<ss.str();
				if(conf.outputToConsole)
					std::cerr<<ss.str();
				}
				catch(std::exception ex)
				{
					continue;
				}
				
			}
			}
		}
		
release:
	
		cvReleaseImage(&current_frame_pro_TB);
		cvReleaseImage(&current_frame_pro_TY);
		cvReleaseImage(&current_frame_pro_B);
		cvReleaseImage(&current_frame_pro_D);

		if(conf.show)
		{
		cvShowImage( "Camera", current_frame );
after_release:
		if( (cvWaitKey(200) & 255) == 27 ) break;
		}
		if(!back)
		{	
		back=true;
		back_img=current_frame;
		}
		else
		{
			cvReleaseImage(&current_frame);
		}
		
		if(conf.image_file)
		{
				cvReleaseImage(&frame);
				if(conf.i_base.current==conf.i_base.image_end)
					break;
				conf.i_base.current+=conf.i_base.step;
		}
		
	}
	if(conf.train_major)
	{
		objDetection::machineLearning::trainDataSet_major(MODEL_MAJOR_NAME_TY,selectedDataSet_TY,rejectedDataSet_TY);
		objDetection::machineLearning::trainDataSet_major(MODEL_MAJOR_NAME_B,selectedDataSet_B,rejectedDataSet_B);
		objDetection::machineLearning::trainDataSet_major(MODEL_MAJOR_NAME_D,selectedDataSet_D,rejectedDataSet_D);
		objDetection::machineLearning::trainDataSet_major(MODEL_MAJOR_NAME_TB,selectedDataSet_TB,rejectedDataSet_TB);

	}
	if(conf.train_minor)
	{
		objDetection::machineLearning::trainDataSet_minor(MODEL_MINOR_NAME_TY,selectedDataSet_D_TY,rejectedDataSet_D_TY, selectedtuples_DataSet_TY,rejectedtuples_DataSet_TY);
		objDetection::machineLearning::trainDataSet_minor(MODEL_MINOR_NAME_TB,selectedDataSet_D_TB,rejectedDataSet_D_TB,selectedtuples_DataSet_TB,rejectedtuples_DataSet_TB);
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
	res.train_major=false;
	res.train_minor=false;
	res.show=false;
	res.outputfile=false;	res.outputToConsole=false;
	res.outputToText=false;
	res.predict_major=false;
	res.predict_minor=false;
	res.hsv_max_B=hsv_max_B;
	res.hsv_max_D=hsv_max_D;
	res.hsv_max_TB=hsv_max_TB;
	res.hsv_max_TY=hsv_max_TY;
	res.hsv_min_B=hsv_min_B;
	res.hsv_min_TB=hsv_min_TB;
	res.hsv_min_TY=hsv_min_TY;
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
int main(int argc, char* argv[])
{
	

	
    cvNamedWindow( "Camera", CV_WINDOW_AUTOSIZE );
    
	config conf=get_Config(argc,argv);
	launch(conf);
	cvWaitKey(0);
    return 0;
   }
