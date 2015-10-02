#include <jni.h>
#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include "filters.h"

using namespace std;
using namespace cv;

extern "C" {

JNIEXPORT void JNICALL Java_com_ajscape_pixatoon_Native_applyCartoonFilter(JNIEnv* env, jobject, jlong addrRgba)
{
    Mat& mRgba = *(Mat*)addrRgba;
	cartoonFilter(mRgba);
}

JNIEXPORT void JNICALL Java_com_ajscape_pixatoon_Native_applyCartoon2Filter(JNIEnv* env, jobject, jlong addrRgba)
{
    Mat& mRgba = *(Mat*)addrRgba;
	cartoon2Filter(mRgba);
}

}
