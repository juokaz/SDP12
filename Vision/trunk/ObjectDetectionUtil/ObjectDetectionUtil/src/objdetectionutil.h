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
#include <string>

#include "../../../ObjectDetection/src/objdetection.h"
namespace objDetection
{
	//! utilities namespace includes utility apis.
	namespace utilities
	{
		//! \brief a Color picking tool, shows a window which when clicked on value of the pixels is written on console.
		/*!
			\param img input image.
			\return color of last pixel selected.
		*/
		CvScalar colorPicker(IplImage* img);
		//! \brief a function which shows several images in a single image.
		/*!
			\param title title of window
			\param nArgs number of args.
			\return image created from input parameters
		*/
		IplImage* cvShowManyImages(const char* title, int nArgs, ...);
		//! \brief pops an item from a circular buffer
		/*!
			\param cb pointer to buffer.
			\param item pointer to position of item to write poped value.
		*/
		void cb_pop_front(circular_buffer *cb, void *item);
		//! \brief pushes an item to a buffer
		/*!
			\param cb pointer to buffer.
			\param item pointer to position of item to read.
		*/
		void cb_push_back(circular_buffer *cb, const void *item);
		//! \brief frees a circular buffer
		/*!
			\param cb pointer to buffer.
		*/
		void cb_free(circular_buffer *cb);
		//! \brief calculates average of all values in the buffer, this function assumes that values in the buffer are of type float.
		/*!
			\param cb pointer to buffer.
			\return average value of all items in buffer.
		*/
		float average_cb_buffer(circular_buffer *cb);
		//! \brief initializes a buffer.
		/*!
			\param cb pointer to new buffer.
			\param capacity number of items in buffer.
			\param sz size of a single item in buffer.
		*/
		void cb_init(circular_buffer *cb, size_t capacity, size_t sz);
		//! \brief initializes a config variable using input arguments of program.
		/*!
			\param argc number of arguments
			\param argv pointer to list of arguments.
		*/
		config get_Config(int argc, char* argv[]);
		//! \brief initializes an output file stream using a configurtion variable.
		/*!
			\param conf configuration variable.
			\param file pointer to a ofstream that will be initialized.
		*/
		void initFile(config& conf,ofstream*& file);
		//! \brief releases an output file stream using a configurtion variable.
		/*!
			\param conf configuration variable.
			\param file pointer to a ofstream that will be initialized.
		*/
		void releaseFile(config& conf,ofstream*& file);
		//! \brief initializes image stack of program.
		/*!
			\param conf configuration variable.
		*/
		void initImageStack(config& conf);
		//! \brief sets up background image based on configuration variable. updates background memebr of conf.
		/*!
			\param conf configuration variable.
		*/
		void setupBackgroundImage(config& conf);
		//! \brief retrieves an image from camera and update image pointer.
		/*!
			\param conf configuration variable.
			\param image image pointer which will point to the new image
		*/
		void getImageFromCamera(config& conf,IplImage*& image);
		//! \brief retrieves an image from file and update image pointer.
		/*!
			\param file file name to read image from.
			\param image image pointer which will point to the new image
		*/
		void getImageFromFile(const char* file,IplImage*& image);
		//! \brief retrieves address of next image.
		/*!
			\param conf configuration variable.
			\return name of next file to read.
		*/
		std::string getNextImageFileName(config& conf);
		//! \brief retrieves address of next image.
		/*!
			\param conf configuration variable.
			\return true if reading image was succesfull and false otherwise.
		*/
		bool getNextFrame(config& conf);
		//! \brief releases current frame of config variable based on configuration settings.
		/*!
			\param conf configuration variable.
		*/
		void releaseCurrentFrame(config& conf);
		//! \brief shows conf state.
		/*!
			\param conf configuration variable.
		*/
		void show(config& conf);
		//! \brief outputs state of configuration variable to standard error stream and/or input file
		/*!
			\param conf configuration variable.
			\param file file stream to write on.
		*/
		void output(config& conf,ofstream* file);
		//! \brief crops input frame based on input config ROI.
		/*!
			\param conf configuration variable.
			\param img image to crop.
		*/
		void cropFrame(config& conf,IplImage*& img);
		//! \brief shows resuts on a window.
		/*!
			\param frame origial frame with different data layers.
			\param conf configuration variable.
			\param thresh1 thresholded image number 1.
			\param thresh2 thresholded image number 2.
			\param thresh3 thresholded image number 3.
			\param diffTime that takes to complete a single cycle.
		*/
		void showResults(IplImage*& frame,config& conf,IplImage*& thresh1,IplImage*& thresh2,IplImage*& thresh3,int64 diffTime);

	}
}
#endif
