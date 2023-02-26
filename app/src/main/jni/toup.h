#ifndef _TOUP_H_
#define _TOUP_H_

#include <jni.h>
#include <android/log.h>
extern "C" {
JNIEXPORT void JNICALL
Java_com_example_astroclient_TpLib_init(JNIEnv *env, jobject obj);
JNIEXPORT void JNICALL Java_com_example_astroclient_TpLib_updateFrame(JNIEnv *env, jobject obj,jobject directBuffer);
JNIEXPORT jintArray JNICALL Java_com_example_astroclient_TpLib_getLiveSize(JNIEnv *env, jobject obj);
JNIEXPORT void JNICALL Java_com_example_astroclient_TpLib_releaseCamera(JNIEnv *env, jobject obj);
JNIEXPORT jboolean JNICALL Java_com_example_astroclient_TpLib_isAlive(JNIEnv *env, jobject obj);
JNIEXPORT jboolean JNICALL Java_com_example_astroclient_TpLib_restartCamera(JNIEnv *env, jobject instance);
JNIEXPORT jboolean JNICALL Java_com_example_astroclient_TpLib_startCameraStream(JNIEnv *env, jobject instance);
JNIEXPORT jint JNICALL
Java_com_example_astroclient_TpLib_openDevice(JNIEnv *env, jobject obj, jstring url);
JNIEXPORT jboolean JNICALL
Java_com_example_astroclient_TpLib_haveData(JNIEnv *env, jobject obj);

}
#endif //_TOUP_H_