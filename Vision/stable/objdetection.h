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
#include <time.h>
#include <fstream>
//#define MEASURE_TIME
#ifdef MEASURE_TIME
#include <boost/date_time/posix_time/posix_time_types.hpp>
#endif

struct ROI
{
	int X;
	int Y;
	int Width;
	int Height;
};

struct config
{
	bool show;
	bool bg;
	ROI windowOfInterest;
};

namespace objDetection
{


#define TEST_FILES_START 1
#define TEST_FILES_END 43
#define PI 3.14159265
#define TEXT_OUTPUT "Outputlocs.txt"
//Normalization macro
#define NORMALIZE(X) if (X>360) X=X-360; else if(X<0) X=X+360;

//another version of preprocessFrame uses subtraction.
IplImage* preprocessFrame(IplImage* frame,CvScalar hsv_min,CvScalar hsv_max);

//IplImage* preprocessFrame(IplImage* frame,CvScalar hsv_min,CvScalar hsv_max);

CvContour* getContours( IplImage* frame,CvMemStorage* storage);

//calculating orientation using center of Mass
CvBox2D orientationFirstOrderMoments(CvContour* contour);

CvBox2D orientationSecondOrderMoments(CvContour* contour);

void drawOrientation(IplImage* frame, CvBox2D box,CvScalar=cvScalarAll(0));

}
#endif
