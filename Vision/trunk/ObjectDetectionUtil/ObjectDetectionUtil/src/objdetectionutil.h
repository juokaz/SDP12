#ifndef OBJDETECTIONUTIL_H_
#define OBJDETECTIONUTIL_H_
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
#include "..\..\ObjectDetection\src\objdetection.h"
namespace objDetection
{
	namespace utilities
	{
		CvScalar colorPicker(IplImage* img);
		void thresholdFinder(const char* traininputbase,CvScalar& min,CvScalar& max,bool hsv_true=true);
	}
}
#endif