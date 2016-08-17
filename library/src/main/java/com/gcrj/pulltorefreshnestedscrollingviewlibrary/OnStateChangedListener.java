package com.gcrj.pulltorefreshnestedscrollingviewlibrary;

/**
 * Created by zhangxin on 2016-8-17.
 */
public interface OnStateChangedListener {

    /**
     *
     * @param state  1：下拉中；2：此处松开刷新：
     * @param scaleOfLayout 下拉出的高度与header的比
     */
    void onStateChanged(int state, float scaleOfLayout);

}
