/*
 * Copyright 2017 GuDong
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package name.gudong.trendchart;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

public class HorizontalScrollChartParentView extends HorizontalScrollView {
    private static final String TAG = "HorizontalScrollChartPa";
    private OnScrollListener mListener;

    public HorizontalScrollChartParentView(Context context) {
        super(context);
    }

    public HorizontalScrollChartParentView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HorizontalScrollChartParentView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setOnScrollListener(OnScrollListener listener) {
        mListener = listener;
    }

    /**
     * @param l    Current horizontal scroll origin.
     * @param t    Current vertical scroll origin.
     * @param oldl Previous horizontal scroll origin.
     * @param oldt Previous vertical scroll origin.
     */
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (mListener != null) {
            mListener.onScrollChanged(l, t, oldl, oldt);
        }
    }
    int x = -1;
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x = (int) ev.getX();
                break;
            case MotionEvent.ACTION_UP:
                if (mListener != null) {
                    mListener.onScrollEnd(ev.getX() < x);
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    public interface OnScrollListener {
        void onScrollChanged(int x, int y, int oldX, int oldY);

        /**
         * 滑动结束
         * @param isToLeft 是不是向左滑动
         */
        void onScrollEnd(boolean isToLeft);
    }
}
