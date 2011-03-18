cmake_minimum_required(VERSION 2.8)
### assume FindOpenCV.cmake is in one of the CMAKE_MODULE_PATH folders.
FIND_PACKAGE( OpenCV REQUIRED )
#SET(OPENCV_PATH ~/OpenCV-2.2.0/)
#include_directories(${OPENCV_PATH}/include/opencv)    
#SET(OPENCV_CORE ${OPENCV_PATH}/lib/libopencv_core.so)
#SET(OPENCV_HIGHGUI ${OPENCV_PATH}/lib/libopencv_highgui.so)
#SET(OPENCV_IMGPROC ~${OPENCV_PATH}/lib/libopencv_imgproc.so)
#SET(OPENCV_ML ${OPENCV_PATH}/lib/libopencv_ml.so)
# searches for all .cpp and .h files in the current directory and add them 
# to the current project
FILE(GLOB folder_source *.cpp)
FILE(GLOB folder_header *.h)
SOURCE_GROUP("Source Files" FILES ${folder_source})
SOURCE_GROUP("Header Files" FILES ${folder_header})
  
# create the project
ADD_EXECUTABLE(vision ${folder_source} ../../../ObjectDetection/src/objdetection.cpp ${folder_header})
         
# add include directories
INCLUDE_DIRECTORIES(${CMAKE_SOURCE_DIR})
INCLUDE_DIRECTORIES(${OPENCV_INCLUDE_DIR})
			          
# link opencv libraries 
TARGET_LINK_LIBRARIES(vision ${OpenCV_LIBS})
