#ifndef __PLUGIN_HOOK_H_
#define __PLUGIN_HOOK_H_
#include <jni.h>


#ifdef __cplusplus
extern "C" {
#endif

jint Java_com_morgoo_droidplugin_hook_realloc_NativeHook_nativeOpen(JNIEnv* env,jobject thiz);

void Java_com_morgoo_droidplugin_hook_realloc_NativeHook_nativeRedirect(JNIEnv* env,jobject thiz, jstring originPath, jstring newPath);

#ifdef __cplusplus
}
#endif

#endif //__PLUGIN_HOOK_H_
