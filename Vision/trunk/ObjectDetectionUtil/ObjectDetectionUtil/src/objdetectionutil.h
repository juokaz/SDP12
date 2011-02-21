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
#include <stdarg.h>
#include "../../../ObjectDetection/src/objdetection.h"
namespace objDetection
{
	typedef struct circular_buffer
	{
    void *buffer;     // data buffer
    void *buffer_end; // end of data buffer
    size_t capacity;  // maximum number of items in the buffer
    size_t count;     // number of items in the buffer
    size_t sz;        // size of each item in the buffer
    void *head;       // pointer to head
    void *tail;       // pointer to tail
	} circular_buffer;


	namespace utilities
	{
		CvScalar colorPicker(IplImage* img);
		void thresholdFinder(const char* traininputbase,CvScalar& min,CvScalar& max,bool hsv_true=true);
		IplImage* cvShowManyImages(char* title, int nArgs, ...);
		void cb_pop_front(circular_buffer *cb, void *item);
		void cb_push_back(circular_buffer *cb, const void *item);
		void cb_free(circular_buffer *cb);
		float average_cb_buffer(circular_buffer *cb);
		void cb_init(circular_buffer *cb, size_t capacity, size_t sz);
	}
}
#endif
