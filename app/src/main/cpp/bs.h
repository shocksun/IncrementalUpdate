//
// Created by Administrator on 2019/1/4.
//

#ifndef INCREMENTALUPDATE_TEST_H
#define INCREMENTALUPDATE_TEST_H

#endif //INCREMENTALUPDATE_TEST_H

#include <malloc.h>
#include <jni.h>

int mydiff(int argc,char *argv[]);
int mypatch(int argc,char *argv[]);
JNIEXPORT jint JNICALL
Java_org_shock_incrementalupdate_MainActivity_patch
        (JNIEnv *env, jobject instance, jstring oldpath_, jstring newpath_,jstring patch_);
JNIEXPORT jint JNICALL
Java_org_shock_incrementalupdate_MainActivity_diff
        (JNIEnv *env, jobject instance, jstring oldpath_, jstring newpath_, jstring patch_);