#ifndef __JniUtils
#define __JniUtils __JniUtils
#include <jni.h>
#include <stdlib.h>
#include <stddef.h>

char* jstringToChars(JNIEnv* env, jstring jstr);

#endif