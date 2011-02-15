How to compile the code:
For this part of the program you need to compile test_main.cpp and objdetection.cpp together.
possible import parameters are:
exec [c|i <Path to images> startnumber endnumber] [rankedArea|closeObjects] [show] [outputToText <pathForOutput>] [outputToConsole]
c: use camera feedback
i: use preloaded images
rankedArea: will find objects based on the biggest contours in the image producing only positions
closeObjects: computes orientation using blackspot at the end of the plate
show: show result on screen
outputToConsole: write results on stderr

