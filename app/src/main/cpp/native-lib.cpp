#include <jni.h>
#include <string>
#include <stdio.h>
#include <opencv2/opencv.hpp>
#include "opencv2/objdetect.hpp"
#include "opencv2/highgui.hpp"
#include "opencv2/imgproc.hpp"
#include <iostream>

using namespace std;
using namespace cv;

int toGray(Mat &img, Mat &gray);

extern "C" JNIEXPORT jstring JNICALL
Java_ceng_murathas_facerectest_MainActivity_stringFromJNI(JNIEnv *env, jobject) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

extern "C"
JNIEXPORT jint JNICALL
Java_ceng_murathas_facerectest_OpenCVNativeClass_convertGray(JNIEnv *env, jclass type, jlong matAddrRgba, jlong matAddrGray) {

    Mat& mRgba = *(Mat*)matAddrRgba;
    Mat& mGray = *(Mat*)matAddrGray;

    int conv;
    jint retval;
    conv = toGray(mRgba,mGray);

    retval = (jint)conv;

    return retval;

}

int toGray(Mat &img, Mat &gray) {
    cvtColor(img, gray, CV_RGBA2GRAY);
    if (gray.rows == img.rows && gray.cols == img.cols)
        return 1;
    return 0;
}


extern "C"
JNIEXPORT void JNICALL
Java_ceng_murathas_facerectest_OpenCVNativeClass_faceDetection(JNIEnv *env, jclass type, jlong matAddrRgba) {

    Mat& frame = *(Mat*)matAddrRgba;

    String face_cascade_name = "/storage/emulated/0/data/haarcascade_frontalface_alt.xml";
    String eyes_cascade_name = "/storage/emulated/0/data/haarcascade_eye_tree_eyeglasses.xml";
    CascadeClassifier face_cascade;
    CascadeClassifier eyes_cascade;

    if( !face_cascade.load(face_cascade_name) ){ printf("--(!)Error loading\n"); return ; };
    if( !eyes_cascade.load(eyes_cascade_name) ){ printf("--(!)Error loading\n"); return ; };

    Mat frame_gray;
    cvtColor(frame, frame_gray, CV_BGR2GRAY );
    equalizeHist( frame_gray, frame_gray );

    //-- Detect faces
    std::vector<Rect> faces;
    //face_cascade.detectMultiScale(frame_gray,faces);

    for ( size_t i = 0; i < faces.size(); i++ )
    {
        Point center( faces[i].x + faces[i].width/2, faces[i].y + faces[i].height/2 );
        ellipse( frame, center, Size( faces[i].width/2, faces[i].height/2 ), 0, 0, 360, Scalar( 255, 0, 255 ), 4 );

    }

}


