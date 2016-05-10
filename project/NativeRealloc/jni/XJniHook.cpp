#include "XJniHook.h"
#include "Logger.h"
#include "core/NativeIORedirect.h"
#include "JniUtils.h"


jint Java_com_morgoo_droidplugin_hook_realloc_NativeHook_nativeOpen(JNIEnv* env,jobject thiz) {
	NIR_open();
	return 0;
}


void Java_com_morgoo_droidplugin_hook_realloc_NativeHook_nativeRedirect(JNIEnv* env,jobject thiz, jstring originPath, jstring newPath) {
	char* originPathChar = jstringToChars(env, originPath);
	char* newPathChar = jstringToChars(env, newPath);
  	NIR_add(originPathChar, newPathChar);
}