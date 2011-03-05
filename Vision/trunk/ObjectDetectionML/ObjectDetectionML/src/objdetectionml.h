#ifndef OBJDETECTIONML_H_
#define OBJDETECTIONML_H_

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
#include "../../../ObjectDetection/src/objdetection.h"
#include "../../../ObjectDetectionUtil/ObjectDetectionUtil/src/objdetectionutil.h"

#define MODEL_NAME "TrainerOutput.txt"
#define MODEL_MAJOR_NAME_B "TrainerMajorOutput_B.txt"
#define MODEL_MAJOR_NAME_TY "TrainerMajorOutput_TY.txt"
#define MODEL_MAJOR_NAME_TB "TrainerMajorOutput_TB.txt"
#define MODEL_MAJOR_NAME_D "TrainerMajorOutput_D.txt"
#define MODEL_MINOR_NAME_B "TrainerMinorOutput_B.txt"
#define MODEL_MINOR_NAME_TY "TrainerMinorOutput_TY.txt"
#define MODEL_MINOR_NAME_TB "TrainerMinorOutput_TB.txt"
#define TRAIN_FILES_START 2
#define TRAIN_FILES_END 5

namespace objDetection
{
	//! machinelearning namespace includes apis to use machine learning facilites.
	namespace machineLearning
	{
		/*  \struct ContourTuple
		*	\brief this struct contains pointers to two contours that are relateds.
		*/
		struct ContourTuple
		{
		CvContour* a; /**< contour A*/
		CvContour* b; /**< contour B*/
		};
		/*  \struct predictions
		 *	\brief this struct contains data used by ML library.
		 */
	typedef struct predictions
	{
	std::vector<CvContour*> selectedDataSet_Ball;				/**< correct contours classified as Ball*/
	std::vector<CvContour*> rejectedDataSet_Ball;				/**< incorrect contours classified as Ball*/
	std::vector<CvContour*> selectedDataSet_D;					/**< correct contours classified as second order object(darkspot/greenplate)*/
	std::vector<CvContour*> rejectedDataSet_D;					/**< incorrect contours classified as second order object(darkspot/greenplate)*/
	std::vector<CvContour*> selectedDataSet_TY;					/**< correct contours classified as Yellow plate*/
	std::vector<CvContour*> rejectedDataSet_TY;					/**< incorrect contours classified as Yellow plate*/
	std::vector<CvContour*> selectedDataSet_TB;					/**< correct contours classified as Blue plate*/
	std::vector<CvContour*> rejectedDataSet_TB;					/**< incorrect contours classified as Blue plate*/

	std::vector<CvContour*> selectedDataSet_D_TY;				/**< correct contours classified as second order object(darkspot/greenplate) related to a Yellow plate*/
	std::vector<CvContour*> rejectedDataSet_D_TY;				/**< incorrect contours classified as second order object(darkspot/greenplate) related to a Yellow plate*/
	std::vector<CvContour*> selectedDataSet_D_TB;				/**< correct contours classified as second order object(darkspot/greenplate) related to a Blue plate*/
	std::vector<CvContour*> rejectedDataSet_D_TB;				/**< incorrect contours classified as second order object(darkspot/greenplate) related to a Blue plate*/

	std::vector<ContourTuple> selectedtuples_DataSet_TY;		/**< correct tuple contours classified as second order object(darkspot/greenplate) which relates to a Yellow plate*/
	std::vector<ContourTuple> rejectedtuples_DataSet_TY;		/**< incorrect tuple contours classified as second order object(darkspot/greenplate) which relates to a Yellow plate*/
	std::vector<ContourTuple> selectedtuples_DataSet_TB;		/**< correct tuple contours classified as second order object(darkspot/greenplate) which relates to a Blue plate*/
	std::vector<ContourTuple> rejectedtuples_DataSet_TB;		/**< incorrect tuple contours classified as second order object(darkspot/greenplate) which relates to a Blue plate*/
}predictions;
//! \brief using a window, user classifies correct and incorrect contours in a image.
/*!
 \param title title of window to be shown on top and asks user to classify a certain object.
 \param img original 3 channel image to show on the window.
 \param contours list of contours to show to user.
 \param accepted list of accepted items returned by the function.
 \param rejected list of rejected items returned by the function.
*/
void trainDataSet(const char* title,IplImage* img ,std::vector<CvContour*> contours,std::vector<CvContour*>& accepted,std::vector<CvContour*>& rejected);
void getTrainDataSets (std::vector<CvContour*> selected,std::vector<CvContour*> rejected,CvMat* TrainData, CvMat* TrainClasses);
//! \brief using the input file name, a model is created to classify objects. 
//! this method uses following features: Area.
/*!
 \param filename name of file to save the model.
 \param selectedDataSet list of correct contours.
 \param rejectedDataSet list of incorrect contours.
*/
void trainDataSet_major(const char* filename,std::vector<CvContour*> selectedDataSet,	std::vector<CvContour*> rejectedDataSet);
//! \brief using the input file name, a model is created to classify objects with relation to second order objects. 
//! this method uses following features: DDistance2, Compactness, Area
/*!
 \param filename name of file to save the model.
 \param selecteddot_DataSet list of correct second order objects contours.
 \param rejecteddot_DataSet list of incorrect second order objects contours.
 \param selectedDataSet list of correct contours.
 \param rejectedDataSet list of incorrect contours.
*/
void trainDataSet_minor(const char* filename,std::vector<CvContour*> selecteddot_DataSet,::vector<CvContour*> rejecteddot_DataSet,std::vector<ContourTuple> selectedDataSet,	std::vector<ContourTuple> rejectedDataSet);
//! \brief given two sets of lists this method binds correct contours of major objects to second order objects.
/*!
 \param selecteddot_DataSet list of correct second order objects contours.
 \param rejecteddot_DataSet list of incorrect second order objects contours.
 \param selectedcon_DataSet list of correct contours.
 \param rejectedcon_DataSet list of incorrect contours.
 \param selectedtup_DataSet list of correct contour tuples that will be filled by the method.
 \param rejectedtup_DataSet list of incorrect contour tuples that will be filled by the method.
*/
void train_bind(std::vector<CvContour*> selecteddot_DataSet,std::vector<CvContour*> rejecteddot_DataSet,
	std::vector<CvContour*> selectedcon_DataSet,std::vector<CvContour*> rejectedcon_DataSet,
	std::vector<ContourTuple>& selectedtup_DataSet,std::vector<ContourTuple>& rejectedtup_DataSet);
//! \brief extracts a set of HueMoment features of both correct and incorrect input contours and map them to a CvMat*
/*!
 \param selected list of all correct contours.
 \param rejected list of all incorrect contours.
 \param TrainData CvMat that will be filled with inpt data.
 \param start_col number of column to start filling data.
 \param end_col number pf maximum column which method can use.
*/
void setDataSetFeatures_HuMoments (std::vector<CvContour*> selected,std::vector<CvContour*> rejected,CvMat* TrainData,int start_col,int end_col);
//! \brief extracts a set of Area features of both correct and incorrect input contours and map them to a CvMat*
/*!
 \param selected list of all correct contours.
 \param rejected list of all incorrect contours.
 \param TrainData CvMat that will be filled with inpt data.
 \param start_col number of column to start filling data.
*/
void setDataSetFeatures_Area (std::vector<CvContour*> selected,std::vector<CvContour*> rejected,CvMat* TrainData,int start_col);
//! \brief extracts a set of Compactness features of both correct and incorrect input contours and map them to a CvMat*
/*!
 \param selected list of all correct contours.
 \param rejected list of all incorrect contours.
 \param TrainData CvMat that will be filled with inpt data.
 \param start_col number of column to start filling data.
*/
void setDataSetFeatures_Compactness (std::vector<CvContour*> selected,std::vector<CvContour*> rejected,CvMat* TrainData,int start_col);
//! \brief extracts a set of distance features of both correct and incorrect input tuple contours and map them to a CvMat*
/*!
 \param selected list of all correct tuple contours.
 \param rejected list of all incorrect tuple contours.
 \param TrainData CvMat that will be filled with inpt data.
 \param start_col number of column to start filling data.
*/
void setDataSetFeatures_DDistance2 (std::vector<ContourTuple> selected,std::vector<ContourTuple> rejected,CvMat* TrainData,int start_col);
//! \brief sets class number of of both correct and incorrect input tuple contours and map them to a CvMat*
//! 
/*!
 \param selected number of correct instances.
 \param rejected number of incorrect instances.
 \param classData CvMat* which will be filled by this function.
*/
void setDataSetClasses (int selected,int rejected,CvMat* classData);
//! \brief predicts position and orientation of objects using second order objects(plate/darkspot) 
//! 
/*!
 \param image single channel image to use for finding objects.
 \param filename file name of model to load. 
 \param sel pointer to contour containing major object.
 \param storage storage to use for computations.
 \param orig original 3 channel image used to draw debugging results.
*/
std::vector<CvBox2D> tester_image_minor(IplImage* image,const char* filename,CvContour* sel,CvMemStorage* storage,IplImage* orig);
//! \brief predicts position and orientation of objects using major objects.
//! 
/*!
 \param image single channel image to use for finding objects.
 \param filename file name of model to load. 
 \param storage storage to use for computations.
*/
std::vector<CvContour*> tester_image_major(IplImage* image,const char* filename,CvMemStorage* storage);
//! \brief completes training procedure by using all predictions
//! 
/*!
 \param conf configuration instance.
 \param predicts instance of prediction data.
*/
void train_complete(config& conf,predictions& predicts);
//! \brief completes training procedure by using all predictions on second order objects(plate/darkspot).
//! 
/*!
 \param conf configuration instance.
 \param predicts instance of prediction data.
*/
void train_minor_complete(config& conf,predictions& predicts);
//! \brief performes a tarining procedure on all objects, ball, Yellow Plate,Blue plate and second order objects.
//! 
/*!
 \param conf configuration instance.
 \param predicts instance of prediction data.
 \param  current_frame_pro_B pre processed image for Ball.
 \param  current_frame_pro_TB pre processed image for Blue Plate.
 \param  current_frame_pro_TY pre processed image for Yellow Plate.
 \param  current_frame_pro_D pre processed image for D Plate.
*/
void train(config& conf,predictions& predicts,IplImage* current_frame_pro_B,IplImage* current_frame_pro_TB,IplImage* current_frame_pro_TY,IplImage* current_frame_pro_D);
//! \brief performes a tarining procedure on second order objects.
//! 
/*!
 \param conf configuration instance.
 \param predicts instance of prediction data.
 \param  current_frame_pro_D pre processed image for D Plate.
*/
void train_minor(config& conf,predictions& predicts,IplImage* current_frame_pro_D);
//! \brief performes a prediction procedure on all objects, ball, Yellow Plate,Blue plate and second order objects. and updates input configuration variables.
/*!
 \param conf configuration instance.
 \param  current_frame_pro_B pre processed image for Ball.
 \param  current_frame_pro_TB pre processed image for Blue Plate.
 \param  current_frame_pro_TY pre processed image for Yellow Plate.
 \param  current_frame_pro_D pre processed image for D Plate.
*/
void predict(config& conf,IplImage* current_frame_pro_B,IplImage* current_frame_pro_TB,IplImage* current_frame_pro_TY,IplImage* current_frame_pro_D);
//! \brief performes a prediction procedure on second order objects.
//! 
/*!
 \param conf configuration instance.
 \param cnt_TY contour predicted as Yellow Plate.
 \param cnt_TB contour predicted as Blue Plate.
 \param  current_frame_pro_D pre processed image for D Plate.
*/
void predict_minor(config& conf,CvContour* cnt_TB,CvContour* cnt_TY,IplImage* current_frame_pro_D);
	};
};
#endif
