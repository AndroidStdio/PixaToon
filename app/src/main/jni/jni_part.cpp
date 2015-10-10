#include <jni.h>
#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include "filters.h"

extern "C" {

JNIEXPORT void JNICALL Java_com_ajscape_pixatoon_filters_Native_colorCartoonFilter(JNIEnv* env, jobject, jlong addrSrc, jlong addrDst, jint thickness, jint threshold)
{ 
	colorCartoonFilter(*(Mat*)addrSrc, *(Mat*)addrDst, (int)thickness, (int)threshold);
}

JNIEXPORT void JNICALL Java_com_ajscape_pixatoon_filters_Native_grayCartoonFilter(JNIEnv* env, jobject, jlong addrSrc, jlong addrDst, jint thickness, jint threshold)
{ 
	grayCartoonFilter(*(Mat*)addrSrc, *(Mat*)addrDst, (int)thickness, (int)threshold);
}

}
