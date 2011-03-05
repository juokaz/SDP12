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
	namespace machineLearning
	{
		struct ContourTuple
		{
		CvContour* a;
		CvContour* b;
		};
void trainDataSet(const char* title,IplImage* img ,std::vector<CvContour*> contours,std::vector<CvContour*>& accepted,std::vector<CvContour*>& rejected);

void getTrainDataSets (std::vector<CvContour*> selected,std::vector<CvContour*> rejected,CvMat* TrainData, CvMat* TrainClasses);
void trainDataSet_major(const char* filename,std::vector<CvContour*> selectedDataSet,	std::vector<CvContour*> rejectedDataSet);
void trainDataSet_minor(const char* filename,std::vector<CvContour*> selecteddot_DataSet,::vector<CvContour*> rejecteddot_DataSet,std::vector<ContourTuple> selectedDataSet,	std::vector<ContourTuple> rejectedDataSet);
void train_bind(std::vector<CvContour*> selecteddot_DataSet,std::vector<CvContour*> rejecteddot_DataSet,
	std::vector<CvContour*> selectedcon_DataSet,std::vector<CvContour*> rejectedcon_DataSet,
	std::vector<ContourTuple>& selectedtup_DataSet,std::vector<ContourTuple>& rejectedtup_DataSet);

void setDataSetFeatures_HuMoments (std::vector<CvContour*> selected,std::vector<CvContour*> rejected,CvMat* TrainData,int start_col,int end_col);
void setDataSetFeatures_Area (std::vector<CvContour*> selected,std::vector<CvContour*> rejected,CvMat* TrainData,int start_col);
void setDataSetFeatures_Compactness (std::vector<CvContour*> selected,std::vector<CvContour*> rejected,CvMat* TrainData,int start_col);
void setDataSetFeatures_DDistance (std::vector<CvContour*> selected,std::vector<CvContour*> rejected,std::vector<CvContour*> selected_dots,std::vector<CvContour*> rejected_dots,CvMat* TrainData,int start_col);
void setDataSetFeatures_DDistance2 (std::vector<ContourTuple> selected,std::vector<ContourTuple> rejected,CvMat* TrainData,int start_col);

void setDataSetClasses (std::vector<CvContour*> selected,std::vector<CvContour*> rejected,CvMat* classData);
void setDataSetClasses2 (int selected,int rejected,CvMat* classData);
void getPredictDataSets (std::vector<CvContour*> contours,CvMat* predictData);
std::vector<CvBox2D> tester_image_minor(IplImage* image,const char* filename,CvContour* sel,CvMemStorage* storage,IplImage* orig);
std::vector<CvContour*> tester_image_major(IplImage* image,const char* filename,CvMemStorage* storage);

	};
};
#endif
