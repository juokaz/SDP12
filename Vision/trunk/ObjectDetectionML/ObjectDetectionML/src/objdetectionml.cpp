#include "objdetectionml.h"
void objDetection::machineLearning::setDataSetClasses2 (int selected,int rejected,CvMat* classData)
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
	objDetection::machineLearning::setDataSetClasses(selectedDataSet,rejectedDataSet,trainClass);

	CvSVM svm;
	CvSVMParams param;
	param.kernel_type=CvSVM::POLY;
	param.degree=2;
	svm.train(trainData,trainClass,cv::Mat(),cv::Mat(),param);
	svm.save(filename);

	cvReleaseMat(&trainClass);
	cvReleaseMat(&trainData);
}
void objDetection::machineLearning::setDataSetClasses (std::vector<CvContour*> selected,std::vector<CvContour*> rejected,CvMat* classData)
{
	CvMat trainClassesAccepted;
	CvMat trainClassesRejected;
	cvGetRows( classData, &trainClassesAccepted, 0, selected.size());

	for(unsigned int i=0;i<(selected.size());i++)
	{
		*( (float*)CV_MAT_ELEM_PTR( trainClassesAccepted, i, 0 ) ) = 1000.0;





	}
	if(rejected.size()==0)
		return;
	cvGetRows( classData, &trainClassesRejected, selected.size(),selected.size()+ rejected.size());
	for(unsigned int i=0;i<(rejected.size());i++)
	{
		*( (float*)CV_MAT_ELEM_PTR( trainClassesRejected, i, 0 ) ) =0;





	}



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

void objDetection::machineLearning::trainDataSet_minor(const char* filename,std::vector<CvContour*> selectedcon_DataSet,::vector<CvContour*> rejectedcon_DataSet,std::vector<ContourTuple> selectedtuples_DataSet,::vector<ContourTuple> rejectedtuples_DataSet)
{
	if(selectedtuples_DataSet.size()==0||rejectedtuples_DataSet.size()==0)
	{
		std::cout<<"Either SelectedDataSet or RejectedDataSet is 0"<<std::endl;
		return;
	}

	int dataSetsize=(selectedtuples_DataSet.size()+rejectedtuples_DataSet.size());
	CvMat* trainClass=cvCreateMat(dataSetsize,1,CV_32FC1);
	CvMat* trainData=cvCreateMat(dataSetsize,3,CV_32FC1);
	objDetection::machineLearning::setDataSetFeatures_DDistance2(selectedtuples_DataSet,rejectedtuples_DataSet,trainData,0);
	objDetection::machineLearning::setDataSetFeatures_Compactness(selectedcon_DataSet,rejectedcon_DataSet,trainData,1);
	objDetection::machineLearning::setDataSetFeatures_Area(selectedcon_DataSet,rejectedcon_DataSet,trainData,2);
	//objDetection::machineLearning::setDataSetFeatures_HuMoments(selectedcon_DataSet,rejectedcon_DataSet,trainData,3,10);
	objDetection::machineLearning::setDataSetClasses2(selectedtuples_DataSet.size(),rejectedtuples_DataSet.size(),trainClass);

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
	std::vector<objDetection::machineLearning::ContourTuple>& selectedtup_DataSet,std::vector<objDetection::machineLearning::ContourTuple>& rejectedtup_DataSet)
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

		results.push_back(objDetection::getorientation_with_dot(sel,cnt_dot));
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
		CvBox2D d=objDetection::getorientation_with_dot(selected_tuples.at(selI).a,dots);
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
void objDetection::machineLearning::setDataSetFeatures_DDistance2 (std::vector<objDetection::machineLearning::ContourTuple> selected,std::vector<objDetection::machineLearning::ContourTuple> rejected,CvMat* TrainData,int start_col)
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
void objDetection::machineLearning::setDataSetFeatures_DDistance (std::vector<CvContour*> selected,std::vector<CvContour*> rejected,std::vector<CvContour*> selected_dots,std::vector<CvContour*> rejected_dots,CvMat* TrainData,int start_col)
{
	CvMat trainDataAccepted;
	CvMat trainDataRejected;

	cvGetRows( TrainData, &trainDataAccepted, 0, selected.size());
	for(unsigned int i=0;i<(selected.size());i++)
	{
		*( (float*)CV_MAT_ELEM_PTR( trainDataAccepted, i, start_col ) ) = (float)objDetection::distance(selected.at(i),selected_dots.at(i));





	}
	if(rejected.size()==0)
		return;
	cvGetRows( TrainData, &trainDataRejected, selected.size(),selected.size()+ rejected.size());
	for(unsigned int i=0;i<(rejected.size());i++)
	{


		*( (float*)CV_MAT_ELEM_PTR( trainDataRejected, i, start_col ) ) =(float)objDetection::distance(rejected.at(i),rejected_dots.at(i));






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
void objDetection::machineLearning::getPredictDataSets (std::vector<CvContour*> contours,CvMat* predictData)
{



	//cvGetRows( trainData, &trainDataAccepted, selectedIndex, selected.size());
	for(unsigned int i=0;i<(contours.size());i++)
	{
		CvMat curRow;
		cvGetRows(predictData,&curRow,i,i+1);
		CvMoments moments;
		cvMoments(contours.at(i),&moments);
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
		cvSet(&curCell,cvScalar(cvContourArea(contours.at(i))));
		//End of setting HuMomenets as Training Data


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
