#include <cvaux.h>
#include <highgui.h>
#include <cxcore.h>
#include <stdio.h>
#include <iostream>
#include <stdio.h>
#include <stdlib.h>
#include <cstring>
#include <string.h>
#include <assert.h>
#include <math.h>
#include <float.h>
#include <limits.h>
#include <time.h>
#include <ctype.h>
#include <iostream>
#include "objdetection.h"
#include "../../ObjectDetectionUtil/ObjectDetectionUtil/src/objdetectionutil.h"
#include <sstream>
void launch(config conf)
{


    ofstream* writer=NULL;

    objDetection::utilities::initFile(conf,writer);
    objDetection::utilities::initImageStack(conf);

    while(true)
    {
        int64 endTick=0;
        int64 startTick=0;
	
        startTick= cv::getTickCount();

        objDetection::utilities::setupBackgroundImage(conf);

        if(!objDetection::utilities::getNextFrame(conf))
        {
            std::cout<<"frame not loaded"<<std::endl;
            break;
        }

        if(!conf.current_frame)
        {
            std::cout<<"Error: No Image, Closing"<<std::endl;
            break;
        }
        IplImage* current_frame=conf.current_frame;
        IplImage* back_img=conf.background;

        IplImage* current_frame_pro_TB;
        IplImage* current_frame_pro_TY;
        IplImage* current_frame_pro_B;
        IplImage* current_frame_pro_D;



        current_frame_pro_TB=objDetection::preprocess_to_single_channel(current_frame,back_img,conf.hsv_min_TB,conf.hsv_max_TB);
        if(!current_frame_pro_TB)
        {
            std::cout<<"Error in transforming image"<<std::endl;
        }
        current_frame_pro_TY=objDetection::preprocess_to_single_channel(current_frame,back_img,conf.hsv_min_TY,conf.hsv_max_TY);
        if(!current_frame_pro_TY)
        {
            std::cout<<"Error in transforming image"<<std::endl;
        }
        current_frame_pro_B=objDetection::preprocess_to_single_channel(current_frame,conf.hsv_min_B,conf.hsv_max_B);
        if(!current_frame_pro_B)
        {
            std::cout<<"Error in transforming image"<<std::endl;
        }
        current_frame_pro_D=objDetection::preprocess_to_single_channel(current_frame,back_img,conf.hsv_min_D,conf.hsv_max_D);
        if(!current_frame_pro_D)
        {
            std::cout<<"Error in transforming image"<<std::endl;
        }
        CvContour* cnt_TB=NULL;
        CvContour* cnt_TY=NULL;
        CvContour* cnt_B=NULL;



        cnt_B= objDetection::rankedArea(current_frame_pro_B,conf.storage);

        cnt_TB= objDetection::rankedArea(current_frame_pro_TB,conf.storage);

        cnt_TY= objDetection::rankedArea(current_frame_pro_TY,conf.storage);
        if(cnt_TB!=NULL)
        {
            cvDrawContours(conf.current_frame,(CvSeq*)cnt_TB,cvScalar(0,0,255),cvScalar(0,0,255),0,3);
            conf.sel_TB=objDetection::orientation_secondOrderMoment(cnt_TB);
        }
        if(cnt_TY!=NULL)
        {
            cvDrawContours(conf.current_frame,(CvSeq*)cnt_TY,cvScalar(0,0,255),cvScalar(0,0,255),0,3);
            conf.sel_TY=objDetection::orientation_secondOrderMoment(cnt_TY);

        }
        if(cnt_B!=NULL)
        {
            conf.rect_B=cvBoundingRect(cnt_B);
        }




        objDetection::utilities::show(conf);
        objDetection::utilities::output(conf,writer);
        endTick= cv::getTickCount();
        if(conf.show)
        {

            objDetection::utilities::showResults(conf.current_frame,conf,current_frame_pro_TB,current_frame_pro_TY,current_frame_pro_B,endTick-startTick);
            if( (cvWaitKey(10) & 255) == 27 ) break;
        }



        cvClearMemStorage(conf.storage);
        cvReleaseImage(&current_frame_pro_TB);
        cvReleaseImage(&current_frame_pro_TY);
        cvReleaseImage(&current_frame_pro_B);
        cvReleaseImage(&current_frame_pro_D);
        objDetection::utilities::releaseCurrentFrame(conf);

    }

    if(conf.outputfile)
        writer->close();
    cvReleaseMemStorage(&conf.storage);


}
int main(int argc, char* argv[])
{

    cvNamedWindow( "Camera", CV_WINDOW_AUTOSIZE );

    config conf=objDetection::utilities::get_Config(argc,argv);
    launch(conf);
    cvWaitKey(0);
    return 0;
}
