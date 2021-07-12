package cn.com.ava.td.terminal.avartspviewer;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import cn.com.ava.pd.main.JniHelper;

/**
 * AVA RTSP viewer
 * @author pananfly
 */
public class RtspView extends SurfaceView{

    //surface holder
    private SurfaceHolder mHolder;
    //rtsp helper
    private JniHelper mRtspHelper;
    //living state
    private boolean isLiving = false;
    //live init state
    private boolean isLiveInit = false;

    public RtspView(Context context) {
        this(context , null);
    }

    public RtspView(Context context, AttributeSet attrs) {
        this(context, attrs , 0);
    }

    public RtspView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /** init */
    private void init(){
//        mRtspHelper = new JniHelper();
    }

    /**
     * init live tool
     * @return init result
     */
    public boolean initLiveTool(Surface surface){
        if(mRtspHelper == null){
            mRtspHelper = new JniHelper();
        }
        if(mRtspHelper != null && getHolder() != null){
            int ret =  mRtspHelper.init(surface);
            return isLiveInit = (ret == 0);
        }
        return false;
    }

    /**
     * start living
     * @param rtsp_url living url
     * @param num_streams num of streams
     * @param in_x
     * @param in_y
     * @param in_width
     * @param in_height
     * @return start result: if not init, return false . if is living, return true.
     */
    public boolean startLiving(String rtsp_url, int num_streams,
                                int[] in_x, int[] in_y,
                                int[] in_width, int[] in_height){
        if(!isLiveInit){
            return false;
        }
        if(isLiving){
            return true;
        }
        if(mRtspHelper != null){
            int ret = mRtspHelper.start(rtsp_url, num_streams, in_x, in_y, in_width, in_height);
            isLiving = (ret == 0);
        }
        return isLiving;
    }

    /**
     * stop living
     */
    public void stopLiving(){
        if(!isLiveInit || !isLiving)
            return;
        if(mRtspHelper != null){
            mRtspHelper.stop();
        }
        isLiving = false;
    }

    /**
     * destroy live tool
     */
    public void destroyLiveTool(){
        if(isLiveInit && mRtspHelper != null){
            mRtspHelper.destroy();
        }
        mRtspHelper = null;
        isLiving = false;
        isLiveInit = false;
    }
}
