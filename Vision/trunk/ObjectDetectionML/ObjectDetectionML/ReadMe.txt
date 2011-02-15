How to compile the code:
For this part of the program you need to compile test_main.cpp and objdetection.cpp together.
possible import parameters are:
exec [c|i <Path to images> startnumber endnumber] [train_major|train_minor|predict_major|predict_minor] [show] [outputToText <pathForOutput>] [outputToConsole]
c: use camera feedback
i: use preloaded images
train_major: Trains a model for major objects including ball, TYellow and TBlue
train_minor: Trains a model for orientation of objects TYellow and TBlue.
predict_major: Predicts objects by using the major model already trained.
predict_minor: Predicts objects by using the major and minor model already trained.
show: show result on screen
outputToConsole: write results on stderr

