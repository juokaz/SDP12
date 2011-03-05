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

CvSeq* getContours(IplImage* image, CvScalar& min_thresh, CvScalar& max_thresh, CvMemStorage* storage, bool hsvEnabled);
CvSeq* getLargestContour(CvSeq* contours);
void drawAllContours(const char* window_name, CvArr* image, CvSeq* contours);
void drawMatchedTemplate(const char* windowName, IplImage* image, IplImage* tpl, IplImage* result);
IplImage* getSubImage(IplImage* img, CvRect roi);
IplImage* matchTemplate(IplImage* image, IplImage* tpl, IplImage* result, int method);