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
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

public class TrendYAxisView extends View {

    private Paint mGradeAxisPaint;
    private TextPaint mTextPaint;
    private static final boolean isDebug = false;
    private int marginRight = dp2px(9);
    private int marginBottom = dp2px(6);

    private TrendChartView mChartView;

    public TrendYAxisView(Context context) {
        this(context, null);
    }

    public TrendYAxisView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TrendYAxisView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        DashPathEffect mPathEffect = new DashPathEffect(new float[]{dp2px(2), dp2px(2)}, 1);
        mGradeAxisPaint = new Paint();
        mGradeAxisPaint.reset();
        mGradeAxisPaint.setStyle(Paint.Style.STROKE);
        mGradeAxisPaint.setStrokeWidth(1);
        mGradeAxisPaint.setColor(isDebug ? Color.parseColor("#000000") : Color.parseColor("#16ffffff"));
        mGradeAxisPaint.setAntiAlias(true);
        mGradeAxisPaint.setPathEffect(mPathEffect);

        mTextPaint = new TextPaint();
        mTextPaint.setColor(Color.parseColor("#ffffff"));
        mTextPaint.setTextSize(dp2px(11));
        mTextPaint.setAntiAlias(true);
    }

    public void setChartView(TrendChartView mChartView) {
        this.mChartView = mChartView;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(mChartView == null){
            return;
        }
        drawGradeAxis(canvas);

        drawText(canvas);
    }

    private void drawText(Canvas canvas) {
        int heightGrade = mChartView.getChartContentHeight();
        int averageGradleHeight = heightGrade / (mChartView.getGradeCount() - 1);
        int bottomLine = getHeight() - mChartView.getBottomBlankSize();
        for (int i = 0; i < mChartView.getGradeCount(); i++) {
            String text = getYText(i);
            float textWidth = mTextPaint.measureText(text);
            canvas.drawText(text,
                    getWidth() - marginRight - textWidth,
                    bottomLine - i * averageGradleHeight - marginBottom,
                    mTextPaint);
        }
    }

    private String getYText(int i) {
        if (mChartView.isBigModeChart()) {
            switch (i) {
                case 0:
                    return "0";
                case 1:
                    return "250";
                case 2:
                    return "500";
            }
        } else {
            switch (i) {
                case 0:
                    return "0";
                case 1:
                    return "150";
                case 2:
                    return "300";
            }
        }
        return "";
    }


    private void drawGradeAxis(Canvas canvas) {
        Path path = new Path();
        int heightGrade = mChartView.getChartContentHeight();
        int width = getWidth();
        int averageGradleHeight = heightGrade / (mChartView.getGradeCount() - 1);
        int bottomLine = getHeight() - mChartView.getBottomBlankSize();
        for (int i = 0; i < mChartView.getGradeCount(); i++) {
            path.reset();
            path.moveTo(0, bottomLine - averageGradleHeight * i);
            path.lineTo(width, bottomLine - averageGradleHeight * i);
            canvas.drawPath(path, mGradeAxisPaint);
        }
    }

    /**
     * dp转px
     */
    public int dp2px(float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal, getContext().getResources().getDisplayMetrics());
    }
}
