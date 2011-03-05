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
/*	\struct image_base
*	\brief this struct contains data for dealing image files.
*/
struct image_base
{
	int image_start; /**< start number for loading images*/ 
	int step; /**< step for iterating through images.*/
	int image_end; /**< end number for iterating through images.*/
	char* basefile; /**< base directory/address for images*/
	int current; /**< current number in image dataset.*/
};
/*  \struct Region of Interest
*	\brief this struct contains data for croping images.
*/
struct ROI
{
	int X; /**< x value relative to top left of image*/
	int Y; /**< y value relative to top left of image*/
	int Width; /**< width to crop*/
	int Height; /**< height to crop*/
};
	/* 
*	\struct circular buffer
*	\brief this struct contains data for a circular buffer.
*/
typedef struct circular_buffer
	{
    void *buffer;     /**< pointer to buffer */
    void *buffer_end; /**< end of data buffer */
    size_t capacity;  /**< maximum number of items in the buffer */
    size_t count;     /**< number of items in the buffer */
    size_t sz;        /**< size of each item in the buffer*/
    void *head;       /**< pointer to head*/
    void *tail;       /**< pointer to tail*/
	};
/*  \struct config
*	\brief this struct contains main configuration parameters.
*/
struct config
{
	bool image_file;					/**< indicates if images are loaded from file*/
	CvMemStorage* storage;				/**< storage for keeping contours durring run*/
	image_base i_base;					/**< struct that holds values for file based images*/
	bool camera;						/**< indicates if images are loaded from camera*/
	bool rankedArea;					/**< indicates if rankedArea method should be used for finding best match*/
	bool closeObjects;					/**< indicates if closeObjects method should be used to find best match and orientation*/
	bool show;							/**< indicates if results should be displayed*/
	bool outputToText;					/**< indicates if output should be saved in a file*/
	char* outputfile;					/**< address of file which results have to be saved*/
	CvCapture* capture;					/**< pointer to instance of CvCapture to get images from camera */
	bool back;							/**< indicates if background is loaded*/
	circular_buffer TY_Buffer;			/**< circular buffer for Yellow Plate */
	circular_buffer TB_Buffer;			/**< circular buffer for Blue Plate */
	IplImage* current_frame;			/**< pointer to current image */
	IplImage* background;				/**< pointer to backgound image */
	IplImage* background_transformed;	/**< pointer to processed backgound image */
	int64 totalTime;					/**< total time consumed for proccessing images */
	int Opcount;						/**< total number of cycles program has been working*/
	CvRect rect_B;						/**< position of position of Ball*/
	CvBox2D sel_TB;						/**< position and orientation of Blue Plate*/
	CvBox2D sel_TY;						/**< position and orientation of Yelow Plate*/
	bool outputToConsole;				/**< indicates if results should be sent to standard error*/
	bool train_minor;					/**< indicates if program should train using both larg obects and also second order objects(plate-dot)*/
	bool predict_minor;					/**< indicates if program should predict using both larg obects 
											 and also second order objects(plate-dot), if models don't exist one of deterministc methods is used*/
	bool train_major;					/**< indicates if program should train using larg obects*/
	bool predict_major;					/**< indicates if program should predict using larg obects 
											 ,if models don't exist one of deterministc methods is used*/
	ROI windowOfInterest;				/**<struct that holds values for Region of Interest*/
	CvScalar hsv_min_B;					/**<min values for thresholding Ball*/
	CvScalar hsv_max_B;					/**<max values for thresholding Ball*/
	CvScalar hsv_min_TB;				/**<min values for thresholding Blue plate*/
	CvScalar hsv_max_TB;				/**<max values for thresholding Blue plate*/
	CvScalar hsv_min_TY;				/**<min values for thresholding Yellow plate*/
	CvScalar hsv_max_TY;				/**<max values for thresholding Yellow plate*/
	CvScalar hsv_min_D;					/**<min values for thresholding Black Dot/Green Plate plate*/
	CvScalar hsv_max_D;					/**<max values for thresholding Black Dot/Green Plate plate*/
};
//! main namespace includes functions for calculating contours/orienation/matching contours and all sub namespaces
namespace objDetection
{


#define TEST_FILES_START 1
#define TEST_FILES_END 43
#define PI 3.14159265
#define TEXT_OUTPUT "Outputlocs.txt"
//Normalization macro
#define NORMALIZE(X) if (X>PI*2) X=X-PI*2; else if(X<0) X=X+PI*2;

//! \brief extracts contours in a image.
/*!
 \param img pointer to an instance of IplImage* single channel image to extract contours from.
 \param storage pointer to an instance of ScMemStorage to store found contours
*/
std::vector<CvContour*> getContours( IplImage* img,CvMemStorage* storage);
//! \brief Finds the biggest contour in an image
//! use this function in conjunction with thresholding methods to find the object in an image
/*!
 \param frame pointer to an instance of IplImage* single channel image to extract contours from.
 \param storage pointer to an instance of ScMemStorage to store found contours
*/
CvContour* rankedArea(IplImage* frame,CvMemStorage* storage);
//! \brief Calculates position and orientation of an object using rankedArea method and orientation3 method
//! use this function in conjunction with thresholding methods to find the object in an image
/*!
 \param obj_frame pointer to an instance of IplImage* single channel image to extract contours from.
 \param dot_frame pointer to an instance of IplImage* single channel image to extract contours of second order objects.
 \param storage pointer to an instance of ScMemStorage to store found contours
 \param conf configuration instance.
 \return instance CvBox2D that include position and orientation of object.
*/
CvBox2D DotCloseObjectDetection(IplImage* obj_frame,IplImage* dot_frame,CvMemStorage* storage,config conf);
//! \brief Calculates orientation and position of a contour using minimum bounding rectangle
/*!
 \param cntr pointer to a contour to calculate its position and orientation.
 \return instance CvBox2D that include position and orientation of object.
*/
CvBox2D orientation_minRect(CvContour* cntr);
//! \brief Calculates orientation and position of a contour using minimum bounding rectangle and enclosing circle.
/*!
 \param cntr pointer to a contour to calculate its position and orientation.
 \return instance CvBox2D that include position and orientation of object.
*/
CvBox2D orientation_minRect_Circle(CvContour* cntr);
//! \brief Calculates orientation and position of a contour using center of mass of the contour and enclosing center..
/*!
 \param cntr pointer to a contour to calculate its position and orientation.
 \return instance CvBox2D that include position and orientation of object.
*/
CvBox2D orientation_centerMoment(CvContour* cntr);
//! \brief Calculates orientation and position of a contour using center of mass of the contour and seconf order of centeral moments.
//!	uses orientation_minRect_Circle as a helper function to disambiguate orientation
/*!
 \param cntr pointer to a contour to calculate its position and orientation.
 \return instance CvBox2D that include position and orientation of object.
*/
CvBox2D orientation_secondOrderMoment(CvContour* cntr);
//! \brief Calculates orientation and position of a contour using a set of contours asscoiate to the plate(darkspot/plate)
//! This method uses minbounding rectangle for both objects
/*!
 \param cntr pointer to a contour to calculate its position and orientation.
 \param plate_vector list of contours associated to the plate(darkspot/plate)
 \return instance CvBox2D that include position and orientation of object.
*/
CvBox2D orientation_plate2(CvContour* cntr, std::vector<CvContour*> plate_vector);
//! \brief Calculates orientation and position of a contour using a set of contours asscoiate to the plate(darkspot/plate)
//! This method uses minbounding rectangle and bounding circle for both objects.
/*!
 \param cntr pointer to a contour to calculate its position and orientation.
 \param plate_vector list of contours associated to the plate(darkspot/plate)
 \return instance CvBox2D that include position and orientation of object.
*/
CvBox2D orientation_plate1(CvContour* cntr, std::vector<CvContour*> plate_vector);
//! \brief Preprocess an image
//! This method uses: canny Edge detector,applies thresholds, smooths result
/*!
 \param img_src source image.
 \param hsv_min minimum vector for thresholding.
 \param hsv_max maximum vector for thresholding.
 \return a new single channel image of applied thresholds and procedures.
*/
IplImage* preprocess_to_single_channel(IplImage* img_src,CvScalar hsv_min,CvScalar hsv_max);
//! \brief Preprocess an image
//! This method uses: canny Edge detector,applies thresholds, smooths result,subtracts background image.
/*!
 \param frame source image.
 \param frame_back background image.
 \param hsv_min minimum vector for thresholding.
 \param hsv_max maximum vector for thresholding.
 \param back if set background will be subtracted from new frame.
 \param bgr if set color space is set to be RGB.
 \return a new single channel image of applied thresholds and procedures.
*/
IplImage* preprocess_to_single_channel(IplImage* frame,IplImage* frame_back,CvScalar hsv_min,CvScalar hsv_max,bool back=true,bool bgr=false);
//! \brief draws orientation of input CvBox2D on input frame.
/*!
 \param img image that position and orientation is drawn on.
 \param box input location and orientation to draw
 \param color color vector to use for drawing
*/
void drawOrientation(IplImage* img, CvBox2D box,CvScalar color=cvScalarAll(0));
//! \brief find closes contour in a list of contours.
/*!
 \param cntr the contour to find closes contours to it.
 \param dot_contours list of contours to find closest contour from.
 \return closest contour to cntr.
*/
CvContour* findClosest(CvContour* cntr,std::vector<CvContour*> dot_contours);
//! \brief calculates distance of two input contours
//! this method uses center of contours calcuated by minimum bounding box.
/*!
 \param cntr1 first contour.
 \param cntr2 second contour.
 \return distance between two contours.
*/
float distance(CvContour* cntr1,CvContour* cntr2);
}
#endif
