#include <jni.h>
#include <string>
#include <stdio.h>
#include <opencv2/opencv.hpp>

using namespace cv;
using namespace std;

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



