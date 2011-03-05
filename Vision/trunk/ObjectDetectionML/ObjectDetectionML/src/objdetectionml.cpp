#include "objdetectionml.h"
void objDetection::machineLearning::train_complete(config& conf,predictions& predicts)
{
	if(conf.train_major)
	{
		objDetection::machineLearning::trainDataSet_major(MODEL_MAJOR_NAME_TY,predicts.selectedDataSet_TY,predicts.rejectedDataSet_TY);
		objDetection::machineLearning::trainDataSet_major(MODEL_MAJOR_NAME_B,predicts.selectedDataSet_Ball,predicts.rejectedDataSet_Ball);
		objDetection::machineLearning::trainDataSet_major(MODEL_MAJOR_NAME_D,predicts.selectedDataSet_D,predicts.rejectedDataSet_D);
		objDetection::machineLearning::trainDataSet_major(MODEL_MAJOR_NAME_TB,predicts.selectedDataSet_TB,predicts.rejectedDataSet_TB);

	}
	train_minor_complete(conf,predicts);
}
void objDetection::machineLearning::train_minor_complete(config& conf,predictions& predict)
{
	if(conf.train_minor)
	{
		objDetection::machineLearning::trainDataSet_minor(MODEL_MINOR_NAME_TY,predict.selectedDataSet_D_TY,predict.rejectedDataSet_D_TY, predict.selectedtuples_DataSet_TY,predict.rejectedtuples_DataSet_TY);
		objDetection::machineLearning::trainDataSet_minor(MODEL_MINOR_NAME_TB,predict.selectedDataSet_D_TB,predict.rejectedDataSet_D_TB,predict.selectedtuples_DataSet_TB,predict.rejectedtuples_DataSet_TB);
	}
}
void objDetection::machineLearning::predict_minor(config& conf,CvContour* cnt_TB,CvContour* cnt_TY,IplImage* current_frame_pro_D)
{
	if(cnt_TB!=NULL)
	{
		std::vector<CvBox2D> TB= objDetection::machineLearning::tester_image_minor(current_frame_pro_D,MODEL_MINOR_NAME_TB,cnt_TB,conf.storage,conf.current_frame);
		if(TB.size()>0)
		{
			conf.sel_TB=TB.at(0);
			objDetection::utilities::cb_push_back(&conf.TB_Buffer,(void*)&conf.sel_TB.angle);
			conf.sel_TB.angle=objDetection::utilities::average_cb_buffer(&conf.TB_Buffer);
		}
	}
	if(cnt_TY!=NULL)
	{
		std::vector<CvBox2D> TY= objDetection::machineLearning::tester_image_minor(current_frame_pro_D,MODEL_MINOR_NAME_TY,cnt_TY,conf.storage,conf.current_frame);
		if(TY.size()>0)
		{
			conf.sel_TY=TY.at(0);
			objDetection::utilities::cb_push_back(&conf.TY_Buffer,(void*)&conf.sel_TY.angle);
			conf.sel_TY.angle=objDetection::utilities::average_cb_buffer(&conf.TY_Buffer);
		}
	}
}
void objDetection::machineLearning::predict(config& conf,IplImage* current_frame_pro_B,IplImage* current_frame_pro_TB,IplImage* current_frame_pro_TY,IplImage* current_frame_pro_D)
{
	CvContour* cnt_TB=NULL;
	CvContour* cnt_TY=NULL;
	CvContour* cnt_B=NULL;
	if(conf.predict_major||conf.predict_minor)
	{

		std::vector<CvContour*> cnt_D;
		std::vector<CvContour*> cnt;
		cnt= objDetection::machineLearning::tester_image_major(current_frame_pro_B,MODEL_MAJOR_NAME_B,conf.storage);
		if(cnt.size()!=0)
			cnt_B=cnt.at(0);
		cnt.clear();
		cnt= objDetection::machineLearning::tester_image_major(current_frame_pro_TB,MODEL_MAJOR_NAME_TB,conf.storage);
		if(cnt.size()!=0)
			cnt_TB=cnt.at(0);
		cnt.clear();
		cnt= objDetection::machineLearning::tester_image_major(current_frame_pro_TY,MODEL_MAJOR_NAME_TY,conf.storage);
		if(cnt.size()!=0)
			cnt_TY=cnt.at(0);
		if(conf.predict_major)
		{
			if(cnt_TB!=NULL)
			{
				conf.sel_TB=objDetection::orientation_secondOrderMoment(cnt_TB);//,cnt_D,current_frame);
				objDetection::utilities::cb_push_back(&conf.TB_Buffer,(void*)&conf.sel_TB.angle);
				conf.sel_TB.angle=objDetection::utilities::average_cb_buffer(&conf.TB_Buffer);
			}
			if(cnt_TY!=NULL)
			{
				conf.sel_TY=objDetection::orientation_secondOrderMoment(cnt_TY);//,cnt_D,current_frame);
				objDetection::utilities::cb_push_back(&conf.TY_Buffer,(void*)&conf.sel_TY.angle);
				conf.sel_TY.angle=objDetection::utilities::average_cb_buffer(&conf.TY_Buffer);
			}
			if(cnt_B!=NULL)
			{
				conf.rect_B=cvBoundingRect(cnt_B);
			}
		}
		predict_minor(conf,cnt_TB,cnt_TY,current_frame_pro_D);
	}
}
void objDetection::machineLearning::train(config& conf,predictions& predicts,IplImage* current_frame_pro_B,IplImage* current_frame_pro_TB,IplImage* current_frame_pro_TY,IplImage* current_frame_pro_D)
{
	if(conf.train_major||conf.train_minor)
	{
		std::vector<CvContour*> cnt= objDetection::getContours(current_frame_pro_B,conf.storage);
		objDetection::machineLearning::trainDataSet("Find Ball",conf.current_frame,cnt,predicts.selectedDataSet_Ball,predicts.rejectedDataSet_Ball);

		cnt= objDetection::getContours(current_frame_pro_TY,conf.storage);
		objDetection::machineLearning::trainDataSet("Find TY",conf.current_frame,cnt,predicts.selectedDataSet_TY,predicts.rejectedDataSet_TY);

		cnt= objDetection::getContours(current_frame_pro_TB,conf.storage);
		objDetection::machineLearning::trainDataSet("Find TB",conf.current_frame,cnt,predicts.selectedDataSet_TB,predicts.rejectedDataSet_TB);

		cnt= objDetection::getContours(current_frame_pro_D,conf.storage);
		objDetection::machineLearning::trainDataSet("Find D",conf.current_frame,cnt,predicts.selectedDataSet_D,predicts.rejectedDataSet_D);
		train_minor(conf,predicts,current_frame_pro_D);

	}
}
void objDetection::machineLearning::train_minor(config& conf,predictions& predicts,IplImage* current_frame_pro_D)
{
	if(conf.train_minor)
	{

		std::vector<CvContour*> cnt_D= objDetection::getContours(current_frame_pro_D,conf.storage);
		objDetection::machineLearning::trainDataSet("Find D Near TY",conf.current_frame,cnt_D,predicts.selectedDataSet_D_TY,predicts.rejectedDataSet_D_TY);
		objDetection::machineLearning::train_bind(predicts.selectedDataSet_D_TY,predicts.rejectedDataSet_D_TY,predicts.selectedDataSet_TY,predicts.rejectedDataSet_TY,predicts.selectedtuples_DataSet_TY,predicts.rejectedtuples_DataSet_TY);
		objDetection::machineLearning::trainDataSet("Find D Near TB",conf.current_frame,cnt_D,predicts.selectedDataSet_D_TB,predicts.rejectedDataSet_D_TB);
		objDetection::machineLearning::train_bind(predicts.selectedDataSet_D_TB,predicts.rejectedDataSet_D_TB,predicts.selectedDataSet_TB,predicts.rejectedDataSet_TB,predicts.selectedtuples_DataSet_TB,predicts.rejectedtuples_DataSet_TB);


	}
}
void objDetection::machineLearning::setDataSetClasses (int selected,int rejected,CvMat* classData)
{
	CvMat trainClassesAccepted;
	CvMat trainClassesRejected;
	cvGetRows( classData, &trainClassesAccepted, 0,selected);

	for(unsigned int i=0;i<(selected);i++)
	{
		*( (float*)CV_MAT_ELEM_PTR( trainClassesAccepted, i, 0 ) ) = 1000.0;
	}

	cvGetRows( classData, &trainClassesRejected, selected,selected+ rejected);
	for(unsigned int i=0;i<(rejected);i++)
	{
		*( (float*)CV_MAT_ELEM_PTR( trainClassesRejected, i, 0 ) ) =-1000.0;





	}
}
void objDetection::machineLearning::trainDataSet_major(const char* filename,std::vector<CvContour*> selectedDataSet,std::vector<CvContour*> rejectedDataSet)
{
	if(selectedDataSet.size()==0||rejectedDataSet.size()==0)
	{
		std::cout<<"Either SelectedDataSet or RejectedDataSet is 0"<<std::endl;
		return;
	}
	CvMat* trainClass=cvCreateMat(selectedDataSet.size()+rejectedDataSet.size(),1,CV_32FC1);
	CvMat* trainData=cvCreateMat(selectedDataSet.size()+rejectedDataSet.size(),1,CV_32FC1);
	objDetection::machineLearning::setDataSetFeatures_Area(selectedDataSet,rejectedDataSet,trainData,0);
	//objDetection::machineLearning::setDataSetFeatures_Compactness(selectedDataSet,rejectedDataSet,trainData,1);
	objDetection::machineLearning::setDataSetClasses(selectedDataSet.size(),rejectedDataSet.size(),trainClass);

	CvSVM svm;
	CvSVMParams param;
	param.kernel_type=CvSVM::POLY;
	param.degree=2;
	svm.train(trainData,trainClass,cv::Mat(),cv::Mat(),param);
	svm.save(filename);

	cvReleaseMat(&trainClass);
	cvReleaseMat(&trainData);
}
void objDetection::machineLearning::setDataSetFeatures_Area (std::vector<CvContour*> selected,std::vector<CvContour*> rejected,CvMat* trainData,int start_col)
{
	int selectedIndex=0;
	int rejectedIndex=selected.size();
	//Assign Class Labels

	//Assign Data values
	CvMat trainDataAccepted;
	CvMat trainDataRejected;

	cvGetRows( trainData, &trainDataAccepted, selectedIndex, selected.size());
	for(unsigned int i=0;i<(selected.size());i++)
	{
		*( (float*)CV_MAT_ELEM_PTR( trainDataAccepted, i, start_col ) ) = (float)abs(cvContourArea(selected.at(i)));





	}
	if(rejected.size()==0)
		return;
	cvGetRows( trainData, &trainDataRejected, rejectedIndex,rejectedIndex+ rejected.size());
	for(unsigned int i=0;i<(rejected.size());i++)
	{


		*( (float*)CV_MAT_ELEM_PTR( trainDataRejected, i, start_col ) ) =(float)abs(cvContourArea(rejected.at(i)));






	}

}

void objDetection::machineLearning::trainDataSet(const char* title,IplImage* img ,std::vector<CvContour*> contours,std::vector<CvContour*>& accepted,std::vector<CvContour*>& rejected)
{
	std::vector<CvContour*> slc_list;
	cvNamedWindow( title, CV_WINDOW_AUTOSIZE );

	for(unsigned int i=0;i<contours.size();i++)
	{

		cvDrawContours(img,(CvSeq*)(contours.at(i)),cvScalarAll(255),cvScalarAll(255),0,2,8);// Try different values of max_level, and see what happens
		cvShowImage( title, img ); // Original stream with detected ball overlay
		int key=0;

		key=cvWaitKey(0) ;
		if( key=='c' ) 
		{
			rejected.push_back(contours.at(i));
			cvDrawContours(img,(CvSeq*)(contours.at(i)),cvScalar(0,0,0),cvScalar(0,0,0),0,2,8);

		}
		if( key=='a') 
		{
			accepted.push_back(contours.at(i));
			cvDrawContours(img,(CvSeq*)(contours.at(i)),cvScalar(255,0,0),cvScalar(255,0,0),0,2,8);

		}




	}
	cvDestroyWindow(title);

}
void objDetection::machineLearning::trainDataSet_minor(const char* filename,std::vector<CvContour*> selecteddot_DataSet,::vector<CvContour*> rejecteddot_DataSet,std::vector<ContourTuple> selectedDataSet,	std::vector<ContourTuple> rejectedDataSet)
{
	if(selectedDataSet.size()==0||rejectedDataSet.size()==0)
	{
		std::cout<<"Either SelectedDataSet or RejectedDataSet is 0"<<std::endl;
		return;
	}

	int dataSetsize=(selectedDataSet.size()+rejectedDataSet.size());
	CvMat* trainClass=cvCreateMat(dataSetsize,1,CV_32FC1);
	CvMat* trainData=cvCreateMat(dataSetsize,3,CV_32FC1);
	objDetection::machineLearning::setDataSetFeatures_DDistance2(selectedDataSet,rejectedDataSet,trainData,0);
	objDetection::machineLearning::setDataSetFeatures_Compactness(selecteddot_DataSet,rejecteddot_DataSet,trainData,1);
	objDetection::machineLearning::setDataSetFeatures_Area(selecteddot_DataSet,rejecteddot_DataSet,trainData,2);
	//objDetection::machineLearning::setDataSetFeatures_HuMoments(selectedcon_DataSet,rejectedcon_DataSet,trainData,3,10);
	objDetection::machineLearning::setDataSetClasses(selectedDataSet.size(),rejectedDataSet.size(),trainClass);

	CvSVM svm;
	CvSVMParams param;
	param.kernel_type=CvSVM::POLY;
	param.degree=2;

	svm.train(trainData,trainClass,cv::Mat(),cv::Mat(),param);
	svm.save(filename);
	cvReleaseMat(&trainClass);
	cvReleaseMat(&trainData);
}
void objDetection::machineLearning::train_bind(std::vector<CvContour*> selecteddot_DataSet,std::vector<CvContour*> rejecteddot_DataSet,
	std::vector<CvContour*> selectedcon_DataSet,std::vector<CvContour*> rejectedcon_DataSet,
	std::vector<ContourTuple>& selectedtup_DataSet,std::vector<ContourTuple>& rejectedtup_DataSet)
{

	for(int j=0;j<selecteddot_DataSet.size();j++)
	{
		selectedcon_DataSet.push_back(selecteddot_DataSet.at(j));
		for(int i=0;i<1&selectedcon_DataSet.size()>0;i++)
		{
			objDetection::machineLearning::ContourTuple tuple;
			tuple.a=selectedcon_DataSet.at(i);
			tuple.b=selecteddot_DataSet.at(j);
			selectedtup_DataSet.push_back(tuple);
		}
	}

	for(int j=0;j<rejecteddot_DataSet.size();j++)
	{
		rejectedcon_DataSet.push_back(rejecteddot_DataSet.at(j));
		for(int i=0;(i<1)&selectedcon_DataSet.size()>0;i++)
		{
			objDetection::machineLearning::ContourTuple tuple;
			tuple.a=selectedcon_DataSet.at(i);
			tuple.b=rejecteddot_DataSet.at(j);
			rejectedtup_DataSet.push_back(tuple);
		}


	}

}
std::vector<CvBox2D> objDetection::machineLearning::tester_image_minor(IplImage* image,const char* filename,CvContour* sel,CvMemStorage* storage,IplImage* orig)
{


	std::vector<ContourTuple> selected_tuples;
	std::vector<ContourTuple> rejected_tuples;
	std::vector<CvContour*> cnt_dot= objDetection::getContours(image,storage);
	std::vector<CvBox2D> results;
	bool noSVM=false;
	ifstream test(filename);
	if(!test)
		noSVM=true;


	if(noSVM)
	{

		results.push_back(objDetection::orientation_plate2(sel,cnt_dot));
		return results;
	}
	std::vector<CvContour*> cnt_sel;
	cnt_sel.push_back(sel);
	if(cnt_dot.size()==0)
		return results;
	CvMat* predictClass=cvCreateMat(cnt_dot.size(),1,CV_32FC1);
	CvMat* predictData=cvCreateMat(cnt_dot.size(),3,CV_32FC1);
	train_bind(cnt_dot,std::vector<CvContour*>(),cnt_sel,std::vector<CvContour*>(),selected_tuples,rejected_tuples);
	objDetection::machineLearning::setDataSetFeatures_DDistance2(selected_tuples,rejected_tuples,predictData,0);
	objDetection::machineLearning::setDataSetFeatures_Compactness(cnt_dot,std::vector<CvContour*>(),predictData,1);
	objDetection::machineLearning::setDataSetFeatures_Area(cnt_dot,std::vector<CvContour*>(),predictData,2);
	//objDetection::machineLearning::setDataSetFeatures_HuMoments(cnt_dot,std::vector<CvContour*>(),predictData,3,10);
	CvSVM svm;

	svm.load(filename);
	int selI=-1;
	float min_dist=10000000;
	for(unsigned int i=0;i<selected_tuples.size();i++)
	{
		CvMat curRow;
		cvGetRows(predictData,&curRow,i,i+1);
		float res=svm.predict(&curRow,false);
		float val=svm.predict(&curRow,true);
		if(res==1000)
		{
			if(val<=min_dist)
			{
				min_dist=val;
				selI=i;
			}
			cvDrawContours(orig,(CvSeq*)selected_tuples.at(i).b,cvScalarAll(0),cvScalarAll(0),0);	
		}



	}
	if(selI!=-1)
	{

		std::vector<CvContour*> dots;
		dots.push_back(selected_tuples.at(selI).b);
		cvDrawLine(orig,cvPointFrom32f(cvMinAreaRect2(selected_tuples.at(selI).b).center),cvPointFrom32f(cvMinAreaRect2(selected_tuples.at(selI).a).center),cvScalar(0,155,255),4);
		cvDrawContours(orig,(CvSeq*)selected_tuples.at(selI).b,cvScalarAll(255),cvScalarAll(255),0);
		CvBox2D d=objDetection::orientation_plate2(selected_tuples.at(selI).a,dots);
		results.push_back(d);


	}

	return results;

}
std::vector<CvContour*>  objDetection::machineLearning::tester_image_major(IplImage* image,const char* filename,CvMemStorage* storage)
{
	std::vector<CvContour*> results;


	bool noSVM=false;
	ifstream test(filename);

	if(!test)
		noSVM=true;


	if(noSVM)
	{
		results.push_back(objDetection::rankedArea(image,storage));
		return results;
	}

	std::vector<CvContour*> cnt= objDetection::getContours(image,storage);
	if(cnt.size()>0)
	{
		CvMat* predictClass=cvCreateMat(cnt.size(),1,CV_32FC1);
		CvMat* predictData=cvCreateMat(cnt.size(),1,CV_32FC1);
		objDetection::machineLearning::setDataSetFeatures_Area(cnt,std::vector<CvContour*>(),predictData,0);
		//objDetection::machineLearning::setDataSetFeatures_Compactness(cnt,std::vector<CvContour*>(),predictData,1);
		//objDetection::machineLearning::setDataSetFeatures_HuMoments(cnt,std::vector<CvContour*>(),predictData,0,7);
		CvSVM svm;

		svm.load(filename);
		//int selI=-1;
		//float min_dist=-10000000;
		for(unsigned int i=0;i<cnt.size();i++)
		{
			CvMat curRow;
			cvGetRows(predictData,&curRow,i,i+1);
			float res=svm.predict(&curRow);
			float val=svm.predict(&curRow,true);
			//int res = CV_MAT_ELEM( *predictClass, int,i, 0 );

			if (res)
			{
				results.push_back(cnt.at(i));


			}
			else
			{


			}

		}

	}	
	return results;
}
void objDetection::machineLearning::setDataSetFeatures_Compactness (std::vector<CvContour*> selected,std::vector<CvContour*> rejected,CvMat* TrainData,int start_col)
{
	CvMat trainDataAccepted;
	CvMat trainDataRejected;

	cvGetRows( TrainData, &trainDataAccepted, 0, selected.size());
	for(unsigned int i=0;i<(selected.size());i++)
	{
		CvPoint2D32f p;
		float r=0;
		cvMinEnclosingCircle(selected.at(i),&p,&r);
		float area=(float)abs(cvContourArea(selected.at(i)));
		*( (float*)CV_MAT_ELEM_PTR( trainDataAccepted, i, start_col ) ) = 1/(4*PI)*(2*r)*(2*r)/area;
	}
	if(rejected.size()==0)
		return;

	cvGetRows( TrainData, &trainDataRejected, selected.size(),selected.size()+rejected.size());
	for(unsigned int i=0;i<rejected.size();i++)
	{
		CvPoint2D32f p;
		float r=0;
		cvMinEnclosingCircle(rejected.at(i),&p,&r);
		float area=(float)abs(cvContourArea(rejected.at(i)));


		*( (float*)CV_MAT_ELEM_PTR( trainDataRejected, i, start_col ) ) =1/(4*PI)*(2*r)*(2*r)/area;;

	}
}
void objDetection::machineLearning::setDataSetFeatures_DDistance2 (std::vector<ContourTuple> selected,std::vector<ContourTuple> rejected,CvMat* TrainData,int start_col)
{
	CvMat trainDataAccepted;
	CvMat trainDataRejected;

	cvGetRows( TrainData, &trainDataAccepted, 0, selected.size());
	for(unsigned int i=0;i<(selected.size());i++)
	{
		float dist=(float)objDetection::distance(selected.at(i).a,selected.at(i).b);

		*( (float*)CV_MAT_ELEM_PTR( trainDataAccepted, i, start_col ) ) = dist;
	}
	if(rejected.size()==0)
		return;

	cvGetRows( TrainData, &trainDataRejected, selected.size(),selected.size()+rejected.size());
	for(unsigned int i=0;i<rejected.size();i++)
	{
		float dist=(float)objDetection::distance(rejected.at(i).a,rejected.at(i).b);

		*( (float*)CV_MAT_ELEM_PTR( trainDataRejected, i, start_col ) ) =dist;

	}

}
void objDetection::machineLearning::setDataSetFeatures_HuMoments (std::vector<CvContour*> selected,std::vector<CvContour*> rejected,CvMat* trainData,int start_col,int end_col)
{

	int selectedIndex=0;
	int rejectedIndex=selected.size();
	//Assign Class Labels

	//Assign Data values
	CvMat trainDataAccepted;
	CvMat trainDataRejected;

	cvGetRows( trainData, &trainDataAccepted, selectedIndex, selected.size());
	for(unsigned int i=0;i<(selected.size());i++)
	{
		int col=start_col;

		CvMat curRow;
		cvGetRows(&trainDataAccepted,&curRow,i,i+1);
		CvMoments moments;
		cvMoments(selected.at(i),&moments);
		CvHuMoments huMoments;
		cvGetHuMoments(&moments,&huMoments);

		//Setting HuMomenets as Training Data
		CvMat curCell;
		cvGetCol(&curRow,&curCell,col);

		*( (float*)CV_MAT_ELEM_PTR( trainDataAccepted, i, col ) ) = (float)huMoments.hu1;
		std::cout<<"Accepted HuMemont1-"<<(float)huMoments.hu1<<std::endl;
		col++;
		if(col>=end_col)
			continue;

		cvGetCol(&curRow,&curCell,col);
		*( (float*)CV_MAT_ELEM_PTR( trainDataAccepted, i, col ) ) = (float)huMoments.hu2;
		std::cout<<"Accepted HuMemont2-"<<(float)huMoments.hu2<<std::endl;
		col++;
		if(col>=end_col)
			continue;

		cvGetCol(&curRow,&curCell,col);
		*( (float*)CV_MAT_ELEM_PTR( trainDataAccepted, i, col ) ) = (float)huMoments.hu3;
		std::cout<<"Accepted HuMemont3-"<<(float)huMoments.hu3<<std::endl;
		col++;
		if(col>=end_col)
			continue;
		cvGetCol(&curRow,&curCell,col);
		*( (float*)CV_MAT_ELEM_PTR( trainDataAccepted, i, col ) ) = (float)huMoments.hu4;
		std::cout<<"Accepted HuMemont4-"<<(float)huMoments.hu4<<std::endl;
		col++;
		if(col>=end_col)
			continue;
		cvGetCol(&curRow,&curCell,col);
		*( (float*)CV_MAT_ELEM_PTR( trainDataAccepted, i, col ) ) = (float)huMoments.hu5;
		std::cout<<"Accepted HuMemont5-"<<(float)huMoments.hu5<<std::endl;
		col++;
		if(col>=end_col)
			continue;
		cvGetCol(&curRow,&curCell,col);
		*( (float*)CV_MAT_ELEM_PTR( trainDataAccepted, i, col ) ) = (float)huMoments.hu6;
		std::cout<<"Accepted HuMemont6-"<<(float)huMoments.hu6<<std::endl;
		col++;
		if(col>=end_col)
			continue;
		cvGetCol(&curRow,&curCell,col);
		*( (float*)CV_MAT_ELEM_PTR( trainDataAccepted, i, col ) ) = (float)huMoments.hu7;
		std::cout<<"Accepted HuMemont7-"<<(float)huMoments.hu7<<std::endl;
		col++;


	}
	if(rejected.size()==0)
	{
		return;
	}
	cvGetRows( trainData, &trainDataRejected, rejectedIndex,rejectedIndex+ rejected.size());
	for(unsigned int i=0;i<(rejected.size());i++)
	{
		int col=start_col;

		CvMat curRow;
		cvGetRows(&trainDataRejected,&curRow,i,i+1);
		CvMoments moments;
		cvMoments(rejected.at(i),&moments);
		CvHuMoments huMoments;
		cvGetHuMoments(&moments,&huMoments);

		//Setting HuMomenets as Training Data
		CvMat curCell;
		cvGetCol(&curRow,&curCell,col);
		*( (float*)CV_MAT_ELEM_PTR( trainDataRejected, i, col ) ) = (float)huMoments.hu1;
		std::cout<<"Rejected HuMemont1-"<<(float)huMoments.hu1<<std::endl;
		col++;
		if(col>=end_col)
			continue;

		cvGetCol(&curRow,&curCell,col);
		*( (float*)CV_MAT_ELEM_PTR( trainDataRejected, i, col ) ) = (float)huMoments.hu2;
		std::cout<<"Rejected HuMemont2-"<<(float)huMoments.hu2<<std::endl;
		col++;
		if(col>=end_col)
			continue;

		cvGetCol(&curRow,&curCell,col);
		*( (float*)CV_MAT_ELEM_PTR( trainDataRejected, i, col ) ) = (float)huMoments.hu3;
		std::cout<<"Rejected HuMemont3-"<<(float)huMoments.hu3<<std::endl;
		col++;
		if(col>=end_col)
			continue;
		cvGetCol(&curRow,&curCell,++col);
		*( (float*)CV_MAT_ELEM_PTR( trainDataRejected, i, col ) ) = (float)huMoments.hu4;
		std::cout<<"Rejected HuMemont4-"<<(float)huMoments.hu4<<std::endl;
		col++;
		if(col>=end_col)
			continue;
		cvGetCol(&curRow,&curCell,++col);
		*( (float*)CV_MAT_ELEM_PTR( trainDataRejected, i, col ) ) = (float)huMoments.hu5;
		std::cout<<"Rejected HuMemont5-"<<(float)huMoments.hu5<<std::endl;
		col++;
		if(col>=end_col)
			continue;
		cvGetCol(&curRow,&curCell,++col);
		*( (float*)CV_MAT_ELEM_PTR( trainDataRejected, i, col ) ) = (float)huMoments.hu6;
		std::cout<<"Rejected HuMemont6-"<<(float)huMoments.hu6<<std::endl;
		col++;
		if(col>=end_col)
			continue;
		cvGetCol(&curRow,&curCell,col);
		*( (float*)CV_MAT_ELEM_PTR( trainDataRejected, i, col ) ) = (float)huMoments.hu7;
		std::cout<<"Rejected HuMemont7-"<<(float)huMoments.hu7<<std::endl;
		col++;

	}
}

void objDetection::machineLearning::getTrainDataSets (std::vector<CvContour*> selected,std::vector<CvContour*> rejected,CvMat* trainData, CvMat* trainClasses)
{
	CvMat trainClassesAccepted;
	CvMat trainClassesRejected;
	int selectedIndex=0;
	int rejectedIndex=selected.size();
	//Assign Class Labels
	cvGetRows( trainClasses, &trainClassesAccepted, 0, selected.size());
	for(unsigned int i=0;i<selected.size();i++)
	{
		*( (int*)CV_MAT_ELEM_PTR( trainClassesAccepted, i, 0 ) )=1;

	}
	//cvSet( &trainClassesAccepted, cvScalar(1) );

	cvGetRows( trainClasses, &trainClassesRejected, rejectedIndex,rejectedIndex+ rejected.size());
	//cvSet( &trainClassesRejected, cvScalar(0) );
	for(unsigned int i=0;i<rejected.size();i++)
	{
		*( (int*)CV_MAT_ELEM_PTR( trainClassesRejected, i, 0 ) )=0;

	}
	//Assign Data values
	CvMat trainDataAccepted;
	CvMat trainDataRejected;

	cvGetRows( trainData, &trainDataAccepted, selectedIndex, selected.size());
	for(unsigned int i=0;i<(selected.size());i++)
	{
		CvMat curRow;
		cvGetRows(&trainDataAccepted,&curRow,i,i+1);
		CvMoments moments;
		cvMoments(selected.at(i),&moments);
		CvHuMoments huMoments;
		cvGetHuMoments(&moments,&huMoments);

		//Setting HuMomenets as Training Data
		CvMat curCell;
		cvGetCol(&curRow,&curCell,0);
		cvSet(&curCell,cvScalar(huMoments.hu1));
		cvGetCol(&curRow,&curCell,1);
		cvSet(&curCell,cvScalar(huMoments.hu2));
		cvGetCol(&curRow,&curCell,2);
		cvSet(&curCell,cvScalar(huMoments.hu3));
		cvGetCol(&curRow,&curCell,3);
		cvSet(&curCell,cvScalar(huMoments.hu4));
		cvGetCol(&curRow,&curCell,4);
		cvSet(&curCell,cvScalar(huMoments.hu5));
		cvGetCol(&curRow,&curCell,5);
		cvSet(&curCell,cvScalar(huMoments.hu6));
		cvGetCol(&curRow,&curCell,6);
		cvSet(&curCell,cvScalar(huMoments.hu7));
		cvGetCol(&curRow,&curCell,7);
		cvSet(&curCell,cvScalar(cvContourArea(selected.at(i))));

		//End of setting HuMomenets as Training Data


	}

	cvGetRows( trainData, &trainDataRejected, rejectedIndex,rejectedIndex+ rejected.size());
	for(unsigned int i=0;i<(rejected.size());i++)
	{
		CvMat curRow;
		cvGetRows(&trainDataRejected,&curRow,i,i+1);
		CvMoments moments;
		cvMoments(rejected.at(i),&moments);
		CvHuMoments huMoments;
		cvGetHuMoments(&moments,&huMoments);

		//Setting HuMomenets as Training Data
		CvMat curCell;
		cvGetCol(&curRow,&curCell,0);
		cvSet(&curCell,cvScalar(huMoments.hu1));
		cvGetCol(&curRow,&curCell,1);
		cvSet(&curCell,cvScalar(huMoments.hu2));
		cvGetCol(&curRow,&curCell,2);
		cvSet(&curCell,cvScalar(huMoments.hu3));
		cvGetCol(&curRow,&curCell,3);
		cvSet(&curCell,cvScalar(huMoments.hu4));
		cvGetCol(&curRow,&curCell,4);
		cvSet(&curCell,cvScalar(huMoments.hu5));
		cvGetCol(&curRow,&curCell,5);
		cvSet(&curCell,cvScalar(huMoments.hu6));
		cvGetCol(&curRow,&curCell,6);
		cvSet(&curCell,cvScalar(huMoments.hu7));
		cvGetCol(&curRow,&curCell,7);
		cvSet(&curCell,cvScalar(cvContourArea(rejected.at(i))));
		//End of setting HuMomenets as Training Data


	}

}
