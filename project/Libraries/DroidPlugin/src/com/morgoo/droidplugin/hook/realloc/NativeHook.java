package com.morgoo.droidplugin.hook.realloc;

/**
 * @author Lody
 */
public class NativeHook {

    static {
        System.loadLibrary("realloc");
    }

    private static boolean OPENED = false;

    private static native int nativeOpen();

    private static native String nativeRedirect(String oldPath, String newPath);

    /**
     * @return open的结果
     */
    public static boolean open() {
        //NOTE: 不必考虑线程安全
        if (OPENED) {
            return true;
        }
        try {
            nativeOpen();
            OPENED = true;
        }catch (Throwable e) {
            e.printStackTrace();
        }
        return OPENED;
    }

    public static boolean redirect(String oldPath, String newPath) {
        if (OPENED) {
            try {
                nativeRedirect(oldPath, newPath);
                return true;
            }catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return false;
    }

}
