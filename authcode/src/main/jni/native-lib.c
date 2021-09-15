#include <jni.h>

//
// Created by Administrator on 2021/9/6.
//
const char *key = "http://www.ava.com.cn";


JNIEXPORT jstring JNICALL
Java_cn_com_ava_authcode_AuthKeyUtil_getAuthKey(JNIEnv *env, jclass clazz) {
    return (*env)->NewStringUTF(env, key);
}

