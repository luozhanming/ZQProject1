package cn.com.ava.pd.main;

public class JniHelper {
    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("rt-ex");
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native int init(Object surface);
    public native void destroy();
    public native int start(String rtsp_url, int num_streams,
                            int[] in_x, int[] in_y,
                            int[] in_width, int[] in_height);
    public native void stop();
}
