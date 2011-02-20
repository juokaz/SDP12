#ifndef OBJECTDETECTION_H_
#define OBJECTDETECTION_H_
#include <cvaux.h>
#include <highgui.h>
#include <cxcore.h>
#include <stdio.h>
#include <iostream>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <assert.h>
#include <cmath>
#include <float.h>
#include <limits.h>
#include <time.h>
#include <ctype.h>
#include <iostream>
#include <ml.h>
#include <time.h>
#include <fstream>
//#define MEASURE_TIME
#ifdef MEASURE_TIME
#include <boost/date_time/posix_time/posix_time_types.hpp>
#endif
struct image_base
{
	int image_start;
	//step for iterating through images.
	int step;
	int image_end;
	char* basefile;
	int current;
};
struct ROI
{
	int X;
	int Y;
	int Width;
	int Height;
};

struct config
{
	bool image_file;
	image_base i_base;
	bool camera;
	bool rankedArea;
	bool closeObjects;
	bool show;
	bool outputToText;
	char* outputfile;
	bool outputToConsole;
	bool train_minor;
	bool predict_minor;
	bool train_major;
	bool predict_major;
	ROI windowOfInterest;
	CvScalar hsv_min_B;
	CvScalar hsv_max_B;
	CvScalar hsv_min_TB;
	CvScalar hsv_max_TB;
	CvScalar hsv_min_TY;
	CvScalar hsv_max_TY;
	CvScalar hsv_min_D;
	CvScalar hsv_max_D;
};

namespace objDetection
{


#define TEST_FILES_START 1
#define TEST_FILES_END 43
#define PI 3.14159265
#define TEXT_OUTPUT "Outputlocs.txt"
//Normalization macro
#define NORMALIZE(X) if (X>360) X=X-360; else if(X<0) X=X+360;
 std::vector<CvContour*> getContours( IplImage* img,CvMemStorage* storage);
CvContour* rankedArea(IplImage* frame,CvMemStorage* storage);

CvBox2D DotCloseObjectDetection(IplImage* obj_frame,IplImage* dot_frame,CvMemStorage* storage,config conf);

CvBox2D orientation(CvContour* cntr);
CvBox2D orientation2(CvContour* cntr);
//calculating orientation using center of Mass
CvBox2D orientation_centerMoment(CvContour* cntr,IplImage* img);
CvBox2D orientation3(CvContour* cntr, std::vector<CvContour*> plate_vector);
//calculating orientation using plate and a T contour.
CvBox2D orientation_plate(CvContour* cntr, std::vector<CvContour*> plate_vector,IplImage* img);
CvBox2D getorientation_with_dot(CvContour* cntr,std::vector<CvContour*> dot_contours);

void outputToText(CvBox2D blue,CvBox2D yellow,CvPoint ball);
IplImage* preprocess_to_single_channel(IplImage* img_src,CvScalar hsv_min,CvScalar hsv_max);
//another version of preprocess_to_single_channnel uses subtraction and bgr. if back is set, background image will be subtracted from new frame.
IplImage* preprocess_to_single_channel(IplImage* frame,IplImage* frame_orig,CvScalar hsv_min,CvScalar hsv_max,bool back=true,bool bgr=false);
void drawOrientation(IplImage* img, CvBox2D box,CvScalar=cvScalarAll(0));

CvContour* findClosest(CvContour* cntr,std::vector<CvContour*> dot_contours);

float distance(CvContour* cntr1,CvContour* cntr2);



}
#endif
