package cn.com.ava.common.widget;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class GridItemDecoration extends RecyclerView.ItemDecoration {
    private int vWidth;
    private int hWidth;

    private Paint paint;

    public GridItemDecoration(int vWidth, int hWidth, int color) {
        this.vWidth = vWidth;
        this.hWidth = hWidth;
        paint = new Paint();
        paint.setColor(color);
    }


    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        int count = parent.getChildCount();
        GridLayoutManager glm = (GridLayoutManager) parent.getLayoutManager();
        int spanCount = glm.getSpanCount();
        for (int i = 0; i < count; i++) {
            View child = parent.getChildAt(i);
            if (i % spanCount != spanCount - 1) {
                Rect rect = new Rect();
                rect.set(child.getRight(), child.getTop(), child.getRight() + vWidth, child.getBottom());
                c.drawRect(rect, paint);
            }
            if (i < count - spanCount) {
                Rect rect = new Rect();
                rect.set(child.getLeft(), child.getBottom(), child.getRight(), child.getBottom() + hWidth);
                c.drawRect(rect, paint);
            }
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int pos = parent.getChildAdapterPosition(view);
        int count = parent.getAdapter().getItemCount();
        GridLayoutManager glm = (GridLayoutManager) parent.getLayoutManager();
        int spanCount = glm.getSpanCount();
        if (pos % spanCount != spanCount - 1) { //不是每行最后一个
            outRect.right = vWidth;
        }
        if (pos < count - spanCount) {  //不是最后一行
            outRect.bottom = hWidth;
        }

    }
}
