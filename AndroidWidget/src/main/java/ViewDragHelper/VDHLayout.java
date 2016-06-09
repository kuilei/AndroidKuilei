package ViewDragHelper;

import android.content.Context;
import android.graphics.Point;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by lenovog on 2016/6/9.
 */
public class VDHLayout extends LinearLayout {
    private ViewDragHelper mDragHelper;

    private View mDragView;
    private View mAutoBackView;
    private View mEdgeTrackerView;

    private Point mAutoBackOriginPos = new Point();

    public VDHLayout(Context context) {
        super(context);
    }

    public VDHLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mDragHelper = ViewDragHelper.create(this, 1.0f, new ViewDragHelper.Callback() {
            //返回ture则表示可以捕获该view
            @Override
            public boolean tryCaptureView(View child, int pointerId) {
                return child == mDragView || child == mAutoBackView;
            }
            //可以在该方法中对child移动的边界进行控制，left , top 分别为即将移动到的位置，
            //比如横向的情况下，我希望只在ViewGroup的内部移动,
            // 即：最小>=paddingleft，最大<=ViewGroup.getWidth()-paddingright-child.getWidth
            @Override
            public int clampViewPositionHorizontal(View child, int left, int dx) {
                return left;
            }

            @Override
            public int clampViewPositionVertical(View child, int top, int dy) {
                return top;
            }

            //手指释放的时候回调
            @Override
            public void onViewReleased(View releasedChild, float xvel, float yvel) {
                if (releasedChild == mAutoBackView)
                {
                    //当手指松开，返回view原来的位置
                    mDragHelper.settleCapturedViewAt(mAutoBackOriginPos.x,mAutoBackOriginPos.y);
                    invalidate();
                }
            }

            //在边界拖动时回调
            @Override
            public void onEdgeDragStarted(int edgeFlags, int pointerId) {
                mDragHelper.captureChildView(mEdgeTrackerView,pointerId);
            }
        });
        //边界检测
        mDragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_LEFT);
    }

    //是否应该拦截当前的事件
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //处理事件
        mDragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    public void computeScroll() {
        if (mDragHelper.continueSettling(true))
        {
            invalidate();
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        mAutoBackOriginPos.x = mAutoBackView.getLeft();
        mAutoBackOriginPos.y = mAutoBackView.getTop();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mDragView = getChildAt(0);
        mAutoBackView = getChildAt(1);
        mEdgeTrackerView = getChildAt(2);
    }
}
