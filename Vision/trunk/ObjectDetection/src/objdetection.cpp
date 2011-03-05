// ObjectDetection.cpp : Defines the entry point for the console application.
//
#include "objdetection.h"
//#include "objdetection1.cpp"
#define PI 3.14159265
#define ANGLE_TO_RAD(X) X* PI/180
#define RAD_TO_ANGLE(X) X* 180/PI

IplImage* objDetection::preprocess_to_single_channel(IplImage* frame,IplImage* frame_back,CvScalar hsv_min,CvScalar hsv_max,bool back,bool bgr)
{
	
	// Convert the image from BGR to HSV colour space
	CvSize size = cvSize(frame->width,frame->height);
	IplImage* hsv_frame;
	IplImage* hsv_back_frame;
	if(!bgr)
	{
		//if bgr (meaning BGR colourspace) is unset then we convert images to HSV.
		hsv_frame = cvCreateImage(size, frame->depth,3);
		
		hsv_back_frame = cvCreateImage(size, frame->depth,3);
		cvCvtColor(frame, hsv_frame, CV_BGR2HSV);
		cvCvtColor(frame_back, hsv_back_frame, CV_BGR2HSV);
	}
	else
	{
		//if bgr is set we won't touch images.
		hsv_frame=frame;
		hsv_back_frame=frame_back;
	}
	IplImage* sub_frame = cvCreateImage(size, frame->depth,3);
	//subtract images according to back variable.
	if(!back)
		cvSub(hsv_back_frame,hsv_frame,sub_frame);
	else
		cvSub(hsv_frame,hsv_back_frame,sub_frame);

	//show subtracted image for debugging
	//cvShowImage("Subtracted",sub_frame);
	//return sub_frame;

	IplImage* thresholded = cvCreateImage(size, frame->depth,1);
	
	//Remove irrelavant pixels
	cvInRangeS(sub_frame, hsv_min, hsv_max, thresholded);
//	cvSmooth(thresholded, thresholded, CV_GAUSSIAN, 5, 5);
	//uncomment for debugging...
	
	


	if(!bgr)
	{
		cvReleaseImage(&hsv_frame);
		cvReleaseImage(&hsv_back_frame);
	}

	cvReleaseImage(&sub_frame);
	
	//cvSmooth(edged_frame, edged_frame, CV_GAUSSIAN, 5, 5);
	//cvShowImage("Ranges",thresholded);
	return thresholded;
}
IplImage* objDetection::preprocess_to_single_channel(IplImage* frame,CvScalar hsv_min,CvScalar hsv_max)
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

	//std::cout<<"Finding edges"<<std::endl;
	cvSmooth(edged_frame, edged_frame, CV_GAUSSIAN, 5, 5);

	return edged_frame;
}


CvBox2D objDetection::DotCloseObjectDetection(IplImage* robot_frame,IplImage* dot_frame,CvMemStorage* storage,config conf)
{
	// Mearsure time before processing of frame
#ifdef MEASURE_TIME
	boost::posix_time::ptime stime = boost::posix_time::microsec_clock::local_time();
#endif

	// Find a robot
	CvContour* robot_contour = rankedArea(robot_frame,storage);

	// Find the black dot on the plate
	std::vector<CvContour*> dot_contour = objDetection::getContours(dot_frame,storage);
	//CvContour* dot_contour = rankedArea(dot_frame,storage);

	// Measure time after processing of frame
#ifdef MEASURE_TIME
	boost::posix_time::ptime etime = boost::posix_time::microsec_clock::local_time();
	// See how long it took to process
	long long diff = (etime-stime).total_milliseconds();
	time_total+ = diff;
	//std::cout<<"time: "<<diff<<std::endl;
#endif

	CvBox2D res;
	res.angle = 400;

	if(!robot_contour ) //dot_contour.size()>0
	{
		return res;
	}

	//std::cout<<"calculating orientation"<<std::endl;
	res=orientation_plate2(robot_contour,dot_contour);// objDetection::getorientation_with_dot(robot_contour,dot_contour);

	return res;
}

bool UDgreater (CvContour* elem1, CvContour* elem2 )
{
	return cvContourArea(elem1) < cvContourArea(elem2);
}

CvContour* objDetection::rankedArea(IplImage* frame,CvMemStorage* storage)
{

	std::vector<CvContour*> contour=objDetection::getContours(frame,storage);
	std::sort(contour.begin(),contour.end(),UDgreater);
	if(contour.size()>0) return contour.at(0);
	
	return NULL;
}

CvBox2D objDetection::orientation_secondOrderMoment(CvContour* cntr)
{
	float r=0;
	CvBox2D result;
	
	CvBox2D res1= orientation_centerMoment(cntr);
	
	//Next we calculate Center of mass. 
	CvMoments moments;
	cvMoments(cntr,&moments);
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

	//uncomment for debugging...

	//cvDrawContours(img,(CvSeq*)cntr,cvScalar(150,150,150),cvScalar(150,150,150),0,5);
	//cvDrawCircle(img,cvPointFrom32f(center),14,cvScalar(100,0,100),5);
	//cvDrawCircle(img,cvPointFrom32f(cenMoment),14,cvScalar(0,200,200),3);
	//std::cout<<"Moment"<<cenMoment.x<<","<<cenMoment.y<<"- circle"<<center.x<<","<<center.y<<std::endl;
	//cvDrawLine(img,cvPoint(x,y),cvPointFrom32f(result.center),cvScalar(200,0,255),10);


	return result;
}
std::vector<CvContour*> objDetection::getContours(IplImage* frame,CvMemStorage* storage)
{
	std::vector<CvContour*> selectedContours;
	CvSeq* first_contour;

	// Try all four values and see what happens
	//It seems that this mode works better with different models of object detetcion.
	IplImage* temp=cvCreateImage(cvSize(frame->width,frame->height),frame->depth,frame->nChannels);
	cvCopy(frame,temp);

	int Nc = cvFindContours(temp,storage,&first_contour,sizeof(CvContour),CV_RETR_EXTERNAL);
	cvReleaseImage(&temp);


	for(CvSeq* c=first_contour;c!=NULL;c=c->h_next)
	{
		for(CvSeq* d=c;d!=NULL;d=d->v_next)
		{
			selectedContours.push_back((CvContour*)d);
		}
	}

	return selectedContours;
}
CvContour* objDetection::findClosest(CvContour* robot_contour,std::vector<CvContour*> dot_contour)
{
	CvContour* dot;
	CvBox2D dotm_bbox;
	double m_dist=-1;

	for(unsigned int i=0;i<dot_contour.size();i++)
	{
		CvBox2D dot_bbox = cvMinAreaRect2(dot_contour.at(i));
		int 	dist	 = distance(robot_contour,dot_contour.at(i));

		if((dist<m_dist)||(m_dist==-1))
		{
			dot=dot_contour.at(i);
			dotm_bbox=dot_bbox;
			m_dist=dist;
		}
	}
	return dot;
}

float objDetection::distance(CvContour* contour1,CvContour* contour2)
{
	CvBox2D contour_1	=cvMinAreaRect2(contour1);
	CvBox2D contour_2	=cvMinAreaRect2(contour2);
	return (int) (pow((double)(contour_1.center.x-contour_2.center.x),2) + pow((double)(contour_1.center.y-contour_2.center.y),2));
}

CvBox2D objDetection::orientation_minRect(CvContour* cntr)
{
	CvBox2D result=cvMinAreaRect2(cntr);
	//result.angle+=180;
	return result ;
}
CvBox2D objDetection::orientation_centerMoment(CvContour* cntr)
{
	//First get cntours CvBox2D this can be done with different models of calculation cvFitEllipse2 etc.
	float r=0;
	CvBox2D result;
	CvPoint2D32f center;
	cvMinEnclosingCircle(cntr,&center,&r);
	//Next we calculate Center of mass. 
	CvMoments moments;
	cvMoments(cntr,&moments);
	float x= (moments.m10/moments.m00);
	float y= (moments.m01/moments.m00);
	CvPoint2D32f cenMoment;
	cenMoment.x=x;
	cenMoment.y=y;
	//uncomment for debugging...

	//cvDrawContours(img,(CvSeq*)cntr,cvScalar(150,150,150),cvScalar(150,150,150),0,5);
	//cvDrawCircle(img,cvPointFrom32f(center),14,cvScalar(100,0,100),5);
	//cvDrawCircle(img,cvPointFrom32f(cenMoment),14,cvScalar(0,200,200),3);
	//std::cout<<"Moment"<<cenMoment.x<<","<<cenMoment.y<<"- circle"<<center.x<<","<<center.y<<std::endl;
	//cvDrawLine(img,cvPoint(x,y),cvPointFrom32f(result.center),cvScalar(200,0,255),10);
	result.center=center;
	result.angle = atan2(cenMoment.y-result.center.y, cenMoment.x-result.center.x);
	

	return result;

}

CvBox2D objDetection::orientation_minRect_Circle(CvContour* cntr)
{
	CvBox2D cen1= cvMinAreaRect2(cntr);
	CvPoint2D32f p_cen2;
	float r;
	int i= cvMinEnclosingCircle(cntr,&p_cen2,&r);
	cen1.angle=atan2(cen1.center.y-p_cen2.y,cen1.center.x-p_cen2.x);
	if(cen1.size.height<cen1.size.width)
	{
		cen1.angle-=PI/2;
		return cen1;
	}
}
CvBox2D objDetection::orientation_plate1(CvContour* cntr, std::vector<CvContour*> plate_vector)
{
	//First find the closest plate.
	CvContour* plate_contour= findClosest(cntr,plate_vector);
	CvBox2D robot_rect;
	//Get CvBox2D
	CvBox2D plate_rect= cvMinAreaRect2(plate_contour);
	plate_rect.angle=plate_rect.angle*PI/180;
	float r=0;
	//Get center of the contour of T by fitting a circle. other modes makes it very vulnurable. 
	cvMinEnclosingCircle(cntr,&robot_rect.center,&r);

	//Sometimes the returned angle needs to be rotated since width and height are swaped.
	if(plate_rect.size.width>plate_rect.size.height)
	{
		float temp=plate_rect.size.height;
		plate_rect.size.height=plate_rect.size.width;
		plate_rect.size.width=temp;
		plate_rect.angle-=PI/2;
	}
	//normalize angles to make sure angles are between 0-360
	NORMALIZE(plate_rect.angle);
	//bound angle between 0-180
	if(plate_rect.angle<0)
		plate_rect.angle-=PI;
	if(plate_rect.angle>PI)
		plate_rect.angle-=PI;
	//For different cases of T location angle is changed.
	
	//std::cout<<plate_rect.size.height<<","<<plate_rect.size.width<<","<<plate_rect.angle<<std::endl;


	if((robot_rect.center.x>plate_rect.center.x)&&(robot_rect.center.y>plate_rect.center.y)&&(plate_rect.angle>90))
	{
		plate_rect.angle-=PI;
		//std::cout<<"Modified2:"<<plate_rect.size.height<<","<<plate_rect.size.width<<","<<plate_rect.angle<<std::endl;
	}
	if((robot_rect.center.x<plate_rect.center.x)&&(robot_rect.center.y>plate_rect.center.y)&&(plate_rect.angle<90))
	{
		plate_rect.angle-=PI;
		//std::cout<<"Modified3:"<<plate_rect.size.height<<","<<plate_rect.size.width<<","<<plate_rect.angle<<std::endl;
	}
	//Normalize angles again!
	NORMALIZE(plate_rect.angle);
	return plate_rect;
}
CvBox2D objDetection::orientation_plate2(CvContour* robot_contour, std::vector<CvContour*> plate_vector)
{
	CvContour* plate_contour= findClosest(robot_contour,plate_vector);

	CvBox2D robot_rect= cvMinAreaRect2(robot_contour);
	CvBox2D plate_rect= cvMinAreaRect2(plate_contour);
	robot_rect.angle= atan2(robot_rect.center.y - plate_rect.center.y, robot_rect.center.x - plate_rect.center.x);

	//std::cout<<robot_rect.angle<<std::endl;

	return robot_rect;
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
	//uncomment for debugging
	//cvEllipseBox(frame,box,color,2);
	if(frame)
	{
		cvDrawLine(frame,point1,point2,cvScalar(0,0,0),4);
		cvCircle(frame,pointc,10,color,2);
	}
}

