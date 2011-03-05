#include "stdafx.h"
#include "objdetection1.h"

#define CVX_WHITE CV_RGB(0xff, 0xff, 0xff)

CvSeq* getContours(IplImage* image, CvScalar& minThresh, CvScalar& maxThresh, CvMemStorage* storage, bool hsvEnabled)
{
	// Convert to from RGB to HSV space if hsvEnabled
	if(hsvEnabled)
	{
		cvCvtColor(image, image, CV_BGR2HSV);
	}

	// Threshold and smooth image 
	IplImage* thresholded = cvCreateImage(cvGetSize(image), IPL_DEPTH_8U, 1);
	cvInRangeS(image, minThresh, maxThresh, thresholded);
	cvSmooth(thresholded, thresholded, CV_GAUSSIAN, 5, 5);

	// Declare pointer to first of the contours found
	CvSeq* contours = NULL;
	cvFindContours(thresholded, storage, &contours, sizeof(CvContour), CV_RETR_TREE);

	// Clean up
	cvReleaseImage(&thresholded);

	return contours;
}

CvSeq* getLargestContour(CvSeq* contours)
{
	// Set pointer to current largest contour to the first one
	CvSeq* largestContour = contours;
	
	// Loop through all contours and compare their areas
	CvSeq* c;
	for(c = contours; c != NULL; c = c->h_next)
	{
		if(cvContourArea(c) > cvContourArea(largestContour))
		{
			largestContour = c;
		}
	}

	return largestContour;
}

void drawAllContours(const char* windowName, CvArr* image, CvSeq* contours)
{
	// Loop through contours and draw them on image
	CvSeq* c;
	for(c = contours; c != NULL; c = c->h_next)
	{
		cvDrawContours(image, c, CVX_WHITE, CVX_WHITE, 0, 2, 8);
	}

	cvShowImage(windowName, image);
}

void drawMatchedTemplate(const char* windowName, IplImage* image, IplImage* tpl, IplImage* result)
{
	CvPoint		minloc, maxloc;
	double		minval, maxval;

	cvMinMaxLoc(result, &minval, &maxval, &minloc, &maxloc, 0);

	// Draw a rectangle around the matched segment
	cvRectangle( image, cvPoint( minloc.x, minloc.y ), 
		cvPoint( minloc.x + tpl->width, minloc.y + tpl->height ), cvScalar( 0, 0, 255, 0 ), 1, 0, 0 );

	cvShowImage(windowName, image);
}

IplImage* matchTemplate(IplImage* image, IplImage* tpl, IplImage* result, int method)
{
	// Get dimensions for result
	int res_width  = image->width - tpl->width + 1;
	int res_height = image->height - tpl->height + 1;

	result = cvCreateImage(cvSize(res_width, res_height), IPL_DEPTH_32F, 1);
	
	cvMatchTemplate(image, tpl, result, method);

	return result;
}

IplImage* getSubImage(IplImage* img, CvRect roi)
{
	IplImage* res;

	// Set image region of interest and use img characteristics to get subimage
	cvSetImageROI(img, roi);
	res = cvCreateImage(cvSize(roi.width,roi.height), img->depth, img->nChannels);
	cvCopy(img, res);
	cvResetImageROI(img);

	return res;
}