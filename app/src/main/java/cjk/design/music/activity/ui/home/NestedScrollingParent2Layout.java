package cjk.design.music.activity.ui.home;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.NestedScrollingParent2;
import androidx.core.view.NestedScrollingParentHelper;
import androidx.core.view.ViewCompat;

import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import cjk.design.music.R;

public class NestedScrollingParent2Layout extends LinearLayout implements NestedScrollingParent2 {

    //疑问1：是不是实现了NestedScrollingParent2就可以滑动了
    private NestedScrollingParentHelper mNestedScrollingParentHelper = new NestedScrollingParentHelper(this);
    private View mTopView;//头部view
    private View mNavView;//导航view
    private View mViewPager;//Viewpager
    private ScrollChangeListener mScrollChangeListener;
    /**
     * 父控件可以滚动的距离
     */
    private float mCanScrollDistance = 0f;

    public NestedScrollingParent2Layout(Context context) {
        this(context, null);
    }

    public NestedScrollingParent2Layout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NestedScrollingParent2Layout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(LinearLayout.VERTICAL);
    }

    /**
     * 父控件接受嵌套滑动，不管是手势滑动还是fling 父控件都接受
     */
    @Override
    public boolean onStartNestedScroll(@NonNull View child, @NonNull View target, int axes, int type) {
        return (axes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    @Override
    public void onNestedScrollAccepted(@NonNull View child, @NonNull View target, int axes, int type) {
        mNestedScrollingParentHelper.onNestedScrollAccepted(child, target, axes, type);
    }


    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        //如果子view欲向上滑动，则先交给父view滑动
        boolean hideTop = dy > 0 && getScrollY() < mCanScrollDistance;
        //如果子view欲向下滑动，必须要子view不能向下滑动后，才能交给父view滑动
        //canScrollVertically(-1)返回为true表示能够向下滑动
        boolean showTop = dy < 0 && getScrollY() >= 0 && !target.canScrollVertically(-1);
        if (hideTop || showTop) {
            scrollBy(0, dy);
            consumed[1] = dy;// consumed[0] 水平消耗的距离，consumed[1] 垂直消耗的距离
        }
    }


    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        if (dyUnconsumed < 0) {//表示已经向下滑动到头，这里不用区分手势还是fling
            scrollBy(0, dyUnconsumed);
        }
    }

    @Override
    public void onStopNestedScroll(@NonNull View target, int type) {
        mNestedScrollingParentHelper.onStopNestedScroll(target, type);
    }

    /**
     * 嵌套滑动时，如果父View处理了fling,那子view就没有办法处理fling了，所以这里要返回为false
     */
    @Override
    public boolean onNestedPreFling(@NonNull View target, float velocityX, float velocityY) {
        return false;
    }

    @Override
    public boolean onNestedFling(@NonNull View target, float velocityX, float velocityY, boolean consumed) {
        return false;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() > 0) {
            mTopView = getChildAt(0);
        }
        if (getChildCount() > 1) {
            mNavView = getChildAt(1);
        }
        if (getChildCount() > 2) {
            mViewPager =getChildAt(2);
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //先测量一次
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //ViewPager修改后的高度= 总高度-TabLayout高度
        ViewGroup.LayoutParams lp = mViewPager.getLayoutParams();
        lp.height = getMeasuredHeight() - mNavView.getMeasuredHeight();
        mViewPager.setLayoutParams(lp);
        //因为ViewPager修改了高度，所以需要重新测量
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        int result = 0;
        //获取状态栏高度的资源id
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }

        mCanScrollDistance = mTopView.getMeasuredHeight()+ getResources().getDimension(R.dimen.normal_title_height)-result;
        //getMeasuredHeight 获取到的是组件的高度，并没有包含margin等参数
        //这里实现了是否滑到顶部的效果
        //final float scale = getContext().getResources().getDisplayMetrics().density;
    }

    @Override
    public void scrollTo(int x, int y) {
        if (y < 0) {
            y = 0;
        }
        if (y > mCanScrollDistance) {
            y = (int) mCanScrollDistance;
        }
        if (mScrollChangeListener != null) {
            mScrollChangeListener.onScroll(y / mCanScrollDistance);
        }//这个是调用渐变效果的

//        if (getScrollY() != y)
        super.scrollTo(x, y);
    }


    @Override
    public int getNestedScrollAxes() {
        return mNestedScrollingParentHelper.getNestedScrollAxes();
    }


    public interface ScrollChangeListener {
        /**
         * 移动监听
         *
         * @param moveRatio 移动比例
         */
        void onScroll(float moveRatio);
    }

    public void setScrollChangeListener(ScrollChangeListener scrollChangeListener) {
        mScrollChangeListener = scrollChangeListener;
    }
}
