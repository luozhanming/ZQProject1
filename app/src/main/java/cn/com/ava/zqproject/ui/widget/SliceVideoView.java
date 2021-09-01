package cn.com.ava.zqproject.ui.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.com.ava.common.util.LoggerUtilKt;
import cn.com.ava.lubosdk.entity.LinkedUser;
import cn.com.ava.lubosdk.util.URLHexEncodeDecodeUtil;
import cn.com.ava.zqproject.R;


public class SliceVideoView extends ViewGroup implements View.OnDragListener, GestureDetector.OnGestureListener {

    private static final String TAG = SliceVideoView.class.getSimpleName();
    public static final String DRAG_EVENT_LABEL = "slice_video";
    public static final String DRAG_VIDEO_LONG_PRESS = "slice_video_long_press";

    public static final String DRAG_VIDEO = "drag_video";

    //分片数量
    private int sliceCount = 0;
    //分片矩形，用于记录位置
    private List<RectF> sliceRectFs;

    private OnVideoCallback mListener;
    private int dragState = DragEvent.ACTION_DRAG_ENDED;
    private GestureDetector detector;
    private List<LinkedUser> mUsersOnVideo;
    private boolean isDragExit = false;
    //用于记录长按的位置序号
    private int longClickIndex = -1;

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        final float x = e.getX();
        final float y = e.getY();
        int index = findRectIndex(x, y);
        LoggerUtilKt.logd(this,"drag index "+index);
        if (mListener != null && index >= 0) {
            if (mUsersOnVideo == null || mUsersOnVideo.get(index).getNumber() == -1) return;
            longClickIndex = index;
            mListener.onVideoLongPress(mUsersOnVideo.get(index), index, sliceRectFs.get(index));
        }
    }

    private int findRectIndex(float x, float y) {
        for (int i = 0, size = sliceRectFs.size(); i < size; i++) {
            final RectF rectF = sliceRectFs.get(i);
            if (rectF.contains(x, y)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//        final float x = e1.getX();
//        final float y = e1.getY();
//        int index = findRectIndex(x, y);
//        if (mListener != null && (Math.abs(velocityX) > 10000 || Math.abs(velocityY) > 10000)) {
//            mListener.onReplaceVideo("-1", index);
//            return true;
//        } else {
        return false;
        //       }
    }


    public interface OnVideoCallback {
        /**
         * 替换画面
         *
         * @param userNum     替换窗口的当前用户名
         * @param locateIndex 位于互动画面的位置
         */
        void onReplaceVideo(String userNum, int locateIndex);

        /**
         * 窗口长按
         *
         * @param user     长按窗口的当前用户名
         * @param location 位于互动画面的位置
         * @param rect     长按窗口的轮廓
         */
        void onVideoLongPress(LinkedUser user, int location, RectF rect);

        /**
         * 交互窗口
         * @param srcIndex 长按下去的index
         * @param dstIndex 目标index
         * */
        void onExchangeVideo(int srcIndex, int dstIndex);
    }


    public SliceVideoView(Context context) {
        this(context, null);
    }

    public SliceVideoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SliceVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        setOnDragListener(this);
        detector = new GestureDetector(getContext(), this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //       super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = width * 1080 / 1920;
        setMeasuredDimension(width, height);
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            childAt.measure(widthMeasureSpec, heightMeasureSpec);
        }

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            RectF rectF = sliceRectFs.get(i);
            child.layout((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom);
        }
    }


    /**
     * 设置分片数目
     *
     * @param count 分片数量
     */
    public void changeSliceCount(int count) {
        if (count == sliceCount)
            return;
        sliceCount = count;
        sliceRectFs.clear();
        sliceRectFs = generatorRects(count);
        removeAllViews();
        for (int i = 0; i < count; i++) {
            RectF rectF = sliceRectFs.get(i);
            int width = (int) (rectF.right - rectF.left);
            int height = (int) (rectF.bottom - rectF.top);
            FrameLayout frameLayout = new FrameLayout(getContext());
            frameLayout.setLayoutParams(new FrameLayout.LayoutParams(width, height));
            addView(frameLayout);
            frameLayout.setVisibility(GONE);
        }
        requestLayout();
    }

    private List<RectF> generatorRects(int count) {
        List<RectF> rects = new ArrayList<>(count);
        int height = getMeasuredHeight();
        int width = getMeasuredWidth();
        if (count == 1) {
            RectF rectf = new RectF(0, 0, width, height);
            rects.add(rectf);
        } else if (count == 2) {
            int eachWidth = width / 2;
            int eachHeight = eachWidth * 1080 / 1920;
            int top = height / 2 - eachHeight / 2;
            for (int i = 0; i < 2; i++) {
                RectF rectF = new RectF(i * eachWidth, top, (i + 1) * eachWidth, top + eachHeight);
                rects.add(rectF);
            }
        } else if (count == 3) {
            int eachWidth = width / 2;
            int eachHeight = eachWidth * 1080 / 1920;
            for (int i = 0; i < 3; i++) {
                if (i == 0) {
                    int top = height / 2 - eachHeight / 2;
                    RectF rectF = new RectF(0, top, eachWidth, top + eachHeight);
                    rects.add(rectF);
                } else if (i == 1) {
                    int top = height / 2 - eachHeight;
                    RectF rectF = new RectF(eachWidth, top, eachWidth * 2, top + eachHeight);
                    rects.add(rectF);
                } else if (i == 2) {
                    int top = height / 2;
                    RectF rectF = new RectF(eachWidth, top, eachWidth * 2, top + eachHeight);
                    rects.add(rectF);
                }
            }
        } else if (count == 4) {
            int eachWidth = width / 2;
            int eachHeight = eachWidth * 1080 / 1920;
            for (int i = 0; i < 2; i++) {
                RectF rectF = new RectF(i * eachWidth, 0, (i + 1) * eachWidth, eachHeight);
                rects.add(rectF);
            }
            for (int i = 0; i < 2; i++) {
                RectF rectF = new RectF(i * eachWidth, eachHeight, (i + 1) * eachWidth, eachHeight * 2);
                rects.add(rectF);
            }
        } else if (count == 6) {
            int sEachHeight = height / 3;
            int sEachWidth = width / 3;
            int lEachWidth = width - sEachWidth;
            int lEachHeight = lEachWidth * 1080 / 1920;

            RectF rectF1 = new RectF(0, 0, lEachWidth, lEachHeight);
            rects.add(rectF1);

            RectF rectF2 = new RectF(lEachWidth, 0, lEachWidth + sEachWidth, sEachHeight);
            rects.add(rectF2);

            RectF rectF3 = new RectF(lEachWidth, sEachHeight, lEachWidth + sEachWidth, sEachHeight * 2);
            rects.add(rectF3);

            RectF rectF6 = new RectF(0, sEachHeight * 2, sEachWidth, sEachHeight * 3);
            rects.add(rectF6);

            RectF rectF5 = new RectF(sEachWidth, sEachHeight * 2, sEachWidth * 2, sEachHeight * 3);
            rects.add(rectF5);

            RectF rectF4 = new RectF(lEachWidth, sEachHeight * 2, lEachWidth + sEachWidth, sEachHeight * 3);
            rects.add(rectF4);
        } else if (count == 8) {
            int sEachHeight = height / 4;
            int sEachWidth = width / 4;
            int lEachWidth = sEachWidth * 3;
            int lEachHeight = sEachHeight * 3;
            RectF rectF1 = new RectF(0, 0, lEachWidth, lEachHeight);
            rects.add(rectF1);
            RectF rectF2 = new RectF(lEachWidth, 0, lEachWidth + sEachWidth, sEachHeight);
            rects.add(rectF2);
            RectF rectF3 = new RectF(lEachWidth, sEachHeight, lEachWidth + sEachWidth, sEachHeight * 2);
            rects.add(rectF3);
            RectF rectF4 = new RectF(lEachWidth, sEachHeight * 2, lEachWidth + sEachWidth, sEachHeight * 3);
            rects.add(rectF4);
            RectF rectF8 = new RectF(lEachWidth - sEachWidth * 3, sEachHeight * 3, lEachWidth - sEachWidth * 2, sEachHeight * 4);
            rects.add(rectF8);
            RectF rectF7 = new RectF(lEachWidth - sEachWidth * 2, sEachHeight * 3, lEachWidth - sEachWidth, sEachHeight * 4);
            rects.add(rectF7);
            RectF rectF6 = new RectF(lEachWidth - sEachWidth, sEachHeight * 3, lEachWidth, sEachHeight * 4);
            rects.add(rectF6);
            RectF rectF5 = new RectF(lEachWidth, sEachHeight * 3, lEachWidth + sEachWidth, sEachHeight * 4);
            rects.add(rectF5);

        } else {
            throw new IllegalArgumentException("No such support slice count.");
        }
        return rects;
    }

    public void setUsersOnVideo(List<LinkedUser> usersOnVideo) {
        mUsersOnVideo = usersOnVideo;
        final int size = usersOnVideo.size();
        if (usersOnVideo.size() != sliceCount) return;
        for (int i = 0; i < size; i++) {
            final LinkedUser user = usersOnVideo.get(i);
            final FrameLayout childAt = (FrameLayout) getChildAt(i);
            if (childAt != null && childAt.getChildCount() != 0) {
                childAt.removeAllViews();
            }
            if (dragState == DragEvent.ACTION_DRAG_ENDED || dragState == DragEvent.ACTION_DROP) {
                continue;
            }
            if (user.getNumber() == -1) {
                continue;
            } else {
                TextView textView = new TextView(getContext());
                childAt.addView(textView, 0, new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT
                        , Gravity.CENTER));
                textView.setText(URLHexEncodeDecodeUtil.hexToStringUrlDecode(user.getNickname()));
                textView.setTextColor(Color.parseColor("#f1f1f1"));
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            }
        }
    }

    private void init() {
        sliceRectFs = new ArrayList<>();
    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
        if (sliceRectFs == null && sliceRectFs.size() == 0) {
            return false;
        }
        final int action = event.getAction();
        dragState = action;
        switch (action) {
            case DragEvent.ACTION_DRAG_STARTED:
                final CharSequence label = event.getClipDescription().getLabel();
                if (!label.equals(DRAG_VIDEO))
                    return false;
                dragStartAndEnd();
                LoggerUtilKt.logd(this, "SliceView receive drag start.");

                return true;
            case DragEvent.ACTION_DRAG_ENTERED:
                isDragExit = false;
            case DragEvent.ACTION_DRAG_LOCATION:
                isDragExit = false;
                final CharSequence label2 = event.getClipDescription().getLabel();
                final float x = event.getX();
                final float y = event.getY();
                locateWhereRect(x, y);
                if (DRAG_VIDEO.equals(label2)) {
//                    final String srcLocation = event.getClipData().getItemAt(0).getText().toString();
                }
                isDragExit = false;
                LoggerUtilKt.logd(this, "SliceView receive drag location.");
                return true;
            case DragEvent.ACTION_DRAG_EXITED:
                final CharSequence label3 = event.getClipDescription().getLabel();
                dragStartAndEnd();
                isDragExit = true;
                LoggerUtilKt.logd(this, "SliceView receive drag exit.");
                return true;
            case DragEvent.ACTION_DROP:
                final CharSequence label1 = event.getClipDescription().getLabel();
                final float x1 = event.getX();
                final float y1 = event.getY();
                int result = locateWhereRect(x1, y1);
                final CharSequence text = event.getClipData().getItemAt(0).getText();
                if (DRAG_VIDEO.equals(label1)) {
                    if (result != -1 && mListener != null) {  //交换
                        mListener.onExchangeVideo(Integer.parseInt(text.toString()), result);
                    }
                }
                dragCancel();
                LoggerUtilKt.logd(this, "SliceView receive drag drag.");
                return true;
            case DragEvent.ACTION_DRAG_ENDED:
                if (isDragExit && longClickIndex != -1) {
                    dragCancel();
                    //移除用户
                    mListener.onReplaceVideo("-1", longClickIndex);
                    longClickIndex = -1;
                    isDragExit = false;
                } else {
                    dragCancel();
                }
                return true;
        }
        return true;
    }

    private void dragCancel() {
        final int size = sliceRectFs.size();
        for (int i = 0; i < size; i++) {
            final View childAt = getChildAt(i);
            childAt.setBackgroundDrawable(null);
            childAt.setVisibility(GONE);
        }
    }


    public void setOnLabelDropListener(OnVideoCallback listener) {
        this.mListener = listener;
    }

    private int locateWhereRect(float x, float y) {
        final int size = sliceRectFs.size();
        boolean hasFind = false;
        int result = -1;
        for (int i = 0; i < size; i++) {
            final RectF rectF = sliceRectFs.get(i);
            if (!hasFind && rectF.contains(x, y)) {
                hasFind = true;
                final View childAt = getChildAt(i);
                childAt.setBackgroundResource(R.drawable.meeting_frame_slice_view_drag_enter);
                result = i;
            } else {
                final View childAt = getChildAt(i);
                childAt.setBackgroundResource(R.drawable.meeting_frame_slice_view_drag_begin);
            }
        }
        LoggerUtilKt.logd(this, "locate on " + "(" + x + "," + y + ")" + "result=" + result);

        return result;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return detector.onTouchEvent(event);
    }

    private void dragStartAndEnd() {
        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View childAt = getChildAt(i);
            childAt.setBackgroundResource(R.drawable.meeting_frame_slice_view_drag_begin);
            childAt.setVisibility(VISIBLE);
        }
    }
}
