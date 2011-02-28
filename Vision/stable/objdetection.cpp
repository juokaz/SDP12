#include "objdetection.h"
#define PI 3.14159265
#define ANGLE_TO_RAD(X) X* PI/180
#define RAD_TO_ANGLE(X) X* 180/PI

IplImage* objDetection::preprocessFrame(IplImage* frame,CvScalar hsv_min,CvScalar hsv_max)
{

	CvSize size = cvSize(frame->width,frame->height);
	IplImage* hsv_frame = cvCreateImage(size, frame->depth,3);
	//IplImage* hsv_frame_bg = cvCreateImage(size, frame->depth,3);

	cvCvtColor(frame, hsv_frame, CV_BGR2HSV);
	//cvCvtColor(frame_bg, hsv_frame_bg, CV_BGR2HSV);

	//IplImage* substracted_frame = cvCreateImage(size, frame->depth,3);

	//cvSub(hsv_frame_bg,hsv_frame,substracted_frame);

	IplImage* thresholded = cvCreateImage(size, frame->depth,1);

	cvInRangeS(hsv_frame, hsv_min, hsv_max, thresholded);

	return thresholded;
}

/*
IplImage* objDetection::preprocessFrame(IplImage* frame,CvScalar hsv_min,CvScalar hsv_max)
{

	// Convert the image from BGR to HSV colour space
	CvSize size = cvSize(frame->width,frame->height);
	IplImage* hsv_frame = cvCreateImage(size, frame->depth,3);

	cvCvtColor(frame, hsv_frame, CV_BGR2HSV);

	double	lowThresh	= 70;
	double	highThresh	= lowThresh*3;
	int	N		= 7;

	CvPoint offset = cvPoint((N-1)/2,(N-1)/2);
	size = cvSize(frame->width+N-1,frame->height+N-1);

	// Creating a grayscale thresholded image
	IplImage* thresholded = cvCreateImage(size, frame->depth,1);
	IplImage* border_frame = cvCreateImage(size, frame->depth,3);
	IplImage* edged_frame = cvCreateImage(size, frame->depth,1);


	// Eliminate noise at the borders
	cvCopyMakeBorder( hsv_frame, border_frame, offset, IPL_BORDER_CONSTANT, cvScalarAll(0));

	// Look at a specific colour boundaries
	cvInRangeS(border_frame, hsv_min, hsv_max, thresholded);

	// Perform the Canny edge detection algorithm
	cvCanny( thresholded,edged_frame, lowThresh*N*N, highThresh*N*N, N);

	cvReleaseImage(&thresholded);
	cvReleaseImage(&hsv_frame);
	cvReleaseImage(&border_frame);

	cvSmooth(edged_frame, edged_frame, CV_GAUSSIAN, 5, 5);

	return edged_frame;
}

*/

CvContour* objDetection::getContours(IplImage* frame,CvMemStorage* storage)
{
	std::vector<CvContour*> selectedContours;
	CvSeq* contours;

	int contour_number = cvFindContours(frame,storage,&contours,sizeof(CvContour),CV_RETR_EXTERNAL);
	
	CvContour* contour = NULL;
	
	for(CvSeq* c=contours; c!=NULL ; c=c->h_next )
	{
		contour = (CvContour*) c;
	}

	if(contour == NULL)
	{
		std::cout<<"hui"<<std::endl;
	}

	return contour;
}

CvBox2D objDetection::orientationFirstOrderMoments(CvContour* contour)
{
	float r=0;
	CvBox2D result;
	CvPoint2D32f center;
	cvMinEnclosingCircle(contour,&center,&r);
	
	//Next we calculate Center of mass.
	CvMoments moments;
	cvMoments(contour,&moments);
	
	float x= (moments.m10/moments.m00);
	float y= (moments.m01/moments.m00);
	
	CvPoint2D32f cenMoment;
	cenMoment.x=x;
	cenMoment.y=y;

	result.center=center;
	result.angle = atan2(result.center.y-cenMoment.y, result.center.x-cenMoment.x);

	return result;

}

CvBox2D objDetection::orientationSecondOrderMoments(CvContour* contour)
{
	float r=0;
	CvBox2D result;
	
	CvBox2D res1= orientationFirstOrderMoments(contour);
	
	//Next we calculate Center of mass.
	CvMoments moments;
	cvMoments(contour,&moments);
	float x= (moments.m10/moments.m00);
	float y= (moments.m01/moments.m00);
	CvPoint2D32f cenMoment;

	result.center.x=x;
	result.center.y=y;

	float mu20=moments.m20/moments.m00 - x*x;
	float mu02=moments.m02/moments.m00 - y*y;
	float mu11=moments.m11/moments.m00 - x*y;
	//float angle=atan(2*mu11/(mu20-mu02))/2;
	float angle = atan2(2*mu11,mu20-mu02)/2;
	//float angle= atan2(mu11, mu02);
	//float angle= atan2(mu20, mu11);

	if (res1.angle > PI/2 || res1.angle < -PI/2)
	{
		angle = angle + PI;
	}

	result.angle=angle;

	return result;
}

void objDetection::drawOrientation(IplImage* frame, CvBox2D box,CvScalar color)
{
	CvPoint pointc	= cvPointFrom32f(box.center);
	float	angle	= box.angle;
	CvPoint point1;
	CvPoint point2;

	point1.x= box.center.x;
	point1.y= box.center.y;
	point2.x= box.center.x + cos(angle)*20;
	point2.y= box.center.y + sin(angle)*20;

	if(frame)
	{
		cvDrawLine(frame,point1,point2,cvScalar(0,0,0),4);
		cvCircle(frame,pointc,10,color,2);
	}
}
