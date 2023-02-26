//
// Created by 86132 on 2023/2/25.
//

#include "key_man.h"
#include <jni.h>
#include <cstring>
#include <cstdlib>
#include <regex>


jstring charTojstring(JNIEnv *env, std::string pat) {
    //定义java String类 strClass
    jclass strClass = env->FindClass("java/lang/String");
    //获取String(byte[],String)的构造器,用于将本地byte[]数组转换为一个新String
    jmethodID ctorID = env->GetMethodID(strClass, "<init>", "([BLjava/lang/String;)V");
    //建立byte数组
    jbyteArray bytes = env->NewByteArray(strlen(pat.c_str()));
    //将char* 转换为byte数组
    env->SetByteArrayRegion(bytes, 0, strlen(pat.c_str()), (jbyte*) pat.c_str());
    // 设置String, 保存语言类型,用于byte数组转换至String时的参数
    jstring encoding = env->NewStringUTF("GB2312");
    //将byte数组转换为java String,并输出
    return (jstring) env->NewObject(strClass, ctorID, bytes, encoding);
}
extern "C"
JNIEXPORT jstring JNICALL
Java_com_glide_chujian_util_TpLib_getName(JNIEnv *env, jobject thiz) {
    // TODO: implement getName()
    std::string name = "3694";
    return charTojstring(env,name);
}
extern "C"
JNIEXPORT jstring JNICALL
Java_com_glide_chujian_util_TpLib_getPassword(JNIEnv *env, jobject thiz) {
    // TODO: implement getPassword()
    std::string password = "957362";
    return charTojstring(env,password);
}
