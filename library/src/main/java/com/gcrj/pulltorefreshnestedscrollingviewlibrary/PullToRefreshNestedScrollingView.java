package com.gcrj.pulltorefreshnestedscrollingviewlibrary;

import android.content.Context;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ScrollerCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by zhangxin on 2016-8-15.
 */
public class PullToRefreshNestedScrollingView extends ViewGroup implements NestedScrollingParent {

    private int mHeaderHeight;
    private boolean isRefreshing;
    private RecyclerView mRecyclerView;
    private OnRefreshListener mOnRefreshListener;
    private OnStateChangedListener mOnStateChangedListener;
    private boolean nestedScrolled;

    private ScrollerCompat mScroller;

    public PullToRefreshNestedScrollingView(Context context) {
        super(context);
        init(context);
    }

    public PullToRefreshNestedScrollingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public void setOnRefreshListener(OnRefreshListener listener) {
        mOnRefreshListener = listener;
    }

    public void setOnStateChangedListener(OnStateChangedListener listener) {
        mOnStateChangedListener = listener;
    }

    public void onRefreshComplete() {
        if (isRefreshing) {
            isRefreshing = false;
            nestedScrolled = false;

            if (getScrollY() <= mHeaderHeight) {
                mScroller.startScroll(0, getScrollY(), 0, mHeaderHeight - getScrollY());
                invalidate();
            }
        }
    }

    private void init(Context context) {
        mScroller = ScrollerCompat.create(context);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() != 2) {
            throw new IllegalStateException("need 2 children");
        }
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureChildren(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        View headerView = getChildAt(0);
        mHeaderHeight = headerView.getMeasuredHeight();
        headerView.layout(l, t, r, t + mHeaderHeight);

        mRecyclerView = (RecyclerView) getChildAt(1);
        mRecyclerView.layout(l, t + mHeaderHeight, r, b + mHeaderHeight);
        scrollTo(0, mHeaderHeight);
    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        return (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        if (dy > 0 && getScrollY() < mHeaderHeight) {
            int diff = mHeaderHeight - getScrollY();
            if (dy < diff) {
                consumed[1] = dy;
                scrollBy(0, dy);
            } else {
                consumed[1] = dy - diff;
                scrollBy(0, diff);
            }

            if (mOnStateChangedListener != null) {
                float scaleOfLayout = 1 - getScrollY() * 1f / mHeaderHeight;
                if (scaleOfLayout == 1) {
                    mOnStateChangedListener.onStateChanged(2, scaleOfLayout);
                } else {
                    mOnStateChangedListener.onStateChanged(1, scaleOfLayout);
                }
            }
        }
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        if (dyUnconsumed < 0 && getScrollY() > 0 && getScrollY() <= mHeaderHeight) {
            int diff = getScrollY();
            if (-dyUnconsumed < diff) {
                scrollBy(0, dyUnconsumed);
            } else {
                scrollBy(0, -diff);
            }
            nestedScrolled = true;

            if (mOnStateChangedListener != null) {
                float scaleOfLayout = 1 - getScrollY() * 1f / mHeaderHeight;
                if (scaleOfLayout == 1) {
                    mOnStateChangedListener.onStateChanged(2, scaleOfLayout);
                } else {
                    mOnStateChangedListener.onStateChanged(1, scaleOfLayout);
                }
            }
        }
    }

    @Override
    public void onStopNestedScroll(View child) {
        super.onStopNestedScroll(child);
        if (isRefreshing || !nestedScrolled) {
            return;
        }

        if (getScrollY() > 0 && getScrollY() < mHeaderHeight) {
            nestedScrolled = false;
            mScroller.startScroll(0, getScrollY(), 0, mHeaderHeight - getScrollY());
            invalidate();
        } else if (((LinearLayoutManager) mRecyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition() == 0 && mOnRefreshListener != null) {
            isRefreshing = true;
            mOnRefreshListener.onRefresh();
        }
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(0, mScroller.getCurrY());
            invalidate();
        }
    }
}
