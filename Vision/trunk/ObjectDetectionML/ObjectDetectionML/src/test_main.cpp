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
#include "../../../ObjectDetectionUtil/ObjectDetectionUtil/src/objdetectionutil.h"
#include "objdetectionml.h"
#include <sstream>


// Color threshold for Black Dot
CvScalar hsv_min_D = cvScalar(0,0,0);
CvScalar hsv_max_D = cvScalar(255,255,255);

// Color threshold for Red Ball
CvScalar hsv_min_B = cvScalar(0,131,104);
CvScalar hsv_max_B = cvScalar(10,255,255);

// Color threshold for Yellow T 
CvScalar hsv_min_TY = cvScalar(0,8,75);
CvScalar hsv_max_TY = cvScalar(20,255,255);

// Color threshold for Blue T
CvScalar hsv_min_TB = cvScalar(29,14,0);
CvScalar hsv_max_TB = cvScalar(78,255,255);



int64 totalTime;
int Opcount;
void showResuts(IplImage*& frame,IplImage*& thresh1,IplImage*& thresh2,IplImage*& thresh3,int64 diffTime,CvBox2D TB,CvBox2D TY,CvRect B,bool display)
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
	totalTime+=diffTime;
	Opcount++;
	float meanfps=1/( ( ( (float)totalTime )/Opcount) /cv::getTickFrequency());
	float fps=1/(((float)diffTime)/cv::getTickFrequency());
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
		ss<<"Ball pos:"<<B.x<<","<<B.y;
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
void launch(config conf)
{

	CvMemStorage* storage=cvCreateMemStorage(0);
	IplImage* frame;
	IplImage* back_img;
	bool back=false;
	CvCapture* capture;
	CvRect rect_B;
	rect_B.x=-1;
	rect_B.y=-1;
	CvBox2D sel_TB;
	int64 endTick=0;
	int64 startTick=0;
	CvBox2D sel_TY;
	totalTime=0;
	Opcount=0;
	objDetection::circular_buffer TY_Buffer;
	objDetection::circular_buffer TB_Buffer;
	
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
	if(conf.image_file)
	{
	objDetection::utilities::cb_init(&TY_Buffer,1,sizeof(float));
	objDetection::utilities::cb_init(&TB_Buffer,1,sizeof(float));
	}
	if(conf.camera)
	{
	objDetection::utilities::cb_init(&TY_Buffer,5,sizeof(float));
	objDetection::utilities::cb_init(&TB_Buffer,5,sizeof(float));
	}
	while(true)
	{
		startTick= cv::getTickCount();
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
						//std::getchar();

						//std::cout<<"grabbing frame"<<std::endl;
						if( !cvGrabFrame( capture ))
						{
							std::cout<<"Camera Stopped working, Closing"<<std::endl;
							break;
						}
						frame = cvRetrieveFrame( capture );
						//std::cout<<"frame grabbed"<<std::endl;
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
		
		
		//std::cout<<"reformat"<<std::endl;
		int w=conf.windowOfInterest.Width;
		int h=conf.windowOfInterest.Height; 
		CvSize curSize=cvSize(frame->width,frame->height);
		IplImage* buffer_frame  = cvCreateImage(cvSize(frame->width,frame->height),frame->depth,3);
		IplImage* current_frame = cvCreateImage(cvSize(w,h), frame->depth,3);
		
		IplImage* current_frame_pro_TB;
		IplImage* current_frame_pro_TY;
		IplImage* current_frame_pro_B;
		IplImage* current_frame_pro_D;
		//std::cout<<curSize.width<<" "<<curSize.height<<std::endl;
		cvCopy(frame,buffer_frame);
		cvSetImageROI(buffer_frame,cvRect(conf.windowOfInterest.X,conf.windowOfInterest.Y,w,h));
		cvCopy(buffer_frame,current_frame);
		cvResetImageROI(buffer_frame);

		cvReleaseImage(&buffer_frame);
		
		//std::cout<<"reformat completed"<<std::endl;
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


		current_frame_pro_D=objDetection::preprocess_to_single_channel(current_frame,back_img,conf.hsv_min_D,conf.hsv_max_D);
		if(!current_frame_pro_D)
		{
			std::cout<<"Error in transforming image"<<std::endl;
		}
		
		
		//std::cout<<"Color transformation completed"<<std::endl;
		


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


			//std::cout<<"Prediction completed"<<std::endl;

			if(conf.predict_major)
			{

				CvBox2D sel_D;
				if(cnt_TB!=NULL)
				{
					sel_TB=objDetection::orientation_centerMoment(cnt_TB,current_frame);//,cnt_D,current_frame);
					objDetection::utilities::cb_push_back(&TB_Buffer,(void*)&sel_TB.angle);
					sel_TB.angle=objDetection::utilities::average_cb_buffer(&TB_Buffer);
				}
				else
				{
					sel_TB.center.x=-1;
					sel_TB.center.y=-1;
					sel_TB.angle=-1;
				}
				
				if(cnt_TY!=NULL)
				{
					sel_TY=objDetection::orientation_centerMoment(cnt_TY,current_frame);//,cnt_D,current_frame);
					objDetection::utilities::cb_push_back(&TY_Buffer,(void*)&sel_TY.angle);
					sel_TY.angle=objDetection::utilities::average_cb_buffer(&TY_Buffer);
				}
				else
				{
					sel_TY.center.x=-1;
					sel_TY.center.y=-1;
					sel_TY.angle=-1;
				}
				//std::cout<<"Orientation Calc completed"<<std::endl;
				if(conf.show)
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
						//std::cout<<"outputToConsole"<<std::endl;
						stringstream ss;
						if(cnt_TB)
							ss<<sel_TB.center.x<<","<<sel_TB.center.y<<","<<sel_TB.angle<<",";
						else
							ss<<"-1,-1,-1,";
						if(cnt_B)
						{	
							rect_B= cvBoundingRect(cnt_B);	
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

						rect_B= cvBoundingRect(cnt_B);
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
		endTick= cv::getTickCount();
		cvClearMemStorage(storage);
		if(conf.show)
		{
			
			showResuts(current_frame,current_frame_pro_TB,current_frame_pro_TY,current_frame_pro_B,endTick-startTick,sel_TB,sel_TY,rect_B,true);


			if( (cvWaitKey(10) & 255) == 27 ) break;
		}
release:

		cvReleaseImage(&current_frame_pro_TB);
		cvReleaseImage(&current_frame_pro_TY);
		cvReleaseImage(&current_frame_pro_B);
		cvReleaseImage(&current_frame_pro_D);


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
