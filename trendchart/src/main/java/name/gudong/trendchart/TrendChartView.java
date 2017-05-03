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
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Region;
import android.support.annotation.ColorInt;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseIntArray;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

/**
 * Created by GuDong on 2017/4/27 17:46.
 * Contact with gudong.name@gmail.com.
 */

public class TrendChartView extends View {
    private static final String TAG = "TrendChartView";
    private static final boolean isDebug = false;

    /**
     * title info in y axis
     */
    private final int mBottomBlankSize = dp2px(32);
    /**
     * 气泡跟表格的间距
     */
    private final int mPaddingPopAndChart = dp2px(16);
    /**
     * top black size
     */
    private final int mTopBlankSize = dp2px(29) + mPaddingPopAndChart;
    /**
     * content of chart height
     */
    private final int mChartContentHeight = dp2px(60);

    /**
     * 左边距距离
     */
    private final int mLeftMarginSize = dp2px(40);

    /**
     * 指示器高度
     */
    private final int mIndicatorHeight = dp2px(25);
    /**
     * 指示器宽度
     */
    private final int mIndicatorWidth = dp2px(82);

    /**
     * 等级数量
     */
    private int mGradeCount = 3;
    private static final int OFFSET_SIZE = 0;

    private Paint mGradeAxisPaint;
    private Paint mChartPaint;
    private Paint mChartTestLinePaint;
    private Paint mChartIndicatorPaint;
    private Paint mWhitePaint;
    private Paint mWhiteTextPaint;
    private float mWhiteTextPaintHeight;
    private Paint mDarkTextPaint;
    private DashPathEffect mPathEffect;
    private RectF mRectIndicator;
    private int dp12;
    private int dp2;
    private float roundRadius;

    /**
     * 柱状图内容的绝对宽度
     */
    private int mChartTrendWidthAbs;

    /**
     * 每一天的间隔距离
     */
    private int mAverageDayWidth;

    /**
     * 柱状之间的间隔距离
     */
    private final int mChartSpace = dp2px(1);
    /**
     * 柱状的宽度值
     */
    private final int mChartItemWidth = dp2px(7);
    /**
     * 图表距离最右侧的距离
     */
    private int rightChartMarginSize;
    /**
     * 屏幕宽度
     */
    private int mScreenWidth;

    private List<ITrendData> mDataList = new ArrayList<>();
    /**
     * 图表绘制点集合
     */
    private List<ChartRect> mChartRectList = new ArrayList<>();
    private int mListSize = 0;
    private int mCurrentTimePosition = -1;
    /**
     * 指示标沿着贝塞尔曲线平滑滑动
     */
    private ArrayList<PointF> mControlPoints = new ArrayList<>();
    /**
     * 日期集合
     */
    private List<String> mDayListInfo = new ArrayList<>();
    private List<String> mHourListInfo = new ArrayList<>(3);
    /**
     * 记录日期间隔对应 item 数量
     */
    private SparseIntArray mDayRecord;
    /**
     * 底部文字的坐标点
     */
    private List<PointF> mBottomDayPositions = new ArrayList<>();
    private List<PointF> mBottomHourPositions = new ArrayList<>();
    private Path mPathGradLine;
    private Path mPathIndicatorLine;
    /**
     * 预报的天数
     */
    private int mDayCount = 5;
    /**
     * 最大值是不是超过 250 类型的图表
     */
    private boolean mBigModeChart = false;


    int mOffset = 0;
    int mCurrentIndex = 0;
    int mCurrentDay = 0;
    ITrendData mCurrentData = null;
    private float mMaxAqiValue = 500f;

    private OnMoveListener mMoveListener;

    int mViewHeight;
    int mViewWidth;
    int averageGradleHeight;
    int bottomLine;
    public TrendChartView(Context context) {
        this(context, null);
    }

    public TrendChartView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TrendChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr();
        init(context, attrs);
    }

    private void initAttr() {
        dp12 = dp2px(12);
        dp2 = dp2px(2);
        roundRadius = dp2;
        averageGradleHeight = mChartContentHeight / (mGradeCount - 1);
        rightChartMarginSize = getItemWidthWithSpace() * 1;

        mHourListInfo.add("06:00");
        mHourListInfo.add("12:00");
        mHourListInfo.add("18:00");
        Collections.unmodifiableList(mHourListInfo);
    }

    private void init(Context context, AttributeSet attrs) {
        initPaint();
    }

    //这里面的属性不会随着外部参数变化而变化
    private void initPaint() {
        mPathEffect = new DashPathEffect(new float[]{dp2, dp2}, 1);
        mGradeAxisPaint = new Paint();
        mGradeAxisPaint.reset();
        mGradeAxisPaint.setStyle(Paint.Style.STROKE);
        mGradeAxisPaint.setStrokeWidth(1);
        mGradeAxisPaint.setColor(isDebug ? Color.parseColor("#f00000") : Color.parseColor("#16ffffff"));
        mGradeAxisPaint.setAntiAlias(true);
        mGradeAxisPaint.setPathEffect(mPathEffect);

        mChartPaint = new Paint();
        mChartPaint.setStyle(Paint.Style.FILL);
        mChartPaint.setStrokeWidth(4);
        mChartPaint.setAntiAlias(true);

        mChartTestLinePaint = new Paint();
        mChartTestLinePaint.setStyle(Paint.Style.STROKE);
        mChartTestLinePaint.setStrokeWidth(dp2px(1));
        mChartTestLinePaint.setColor(Color.parseColor("#DF6A56"));
        mChartTestLinePaint.setAntiAlias(true);
        mChartTestLinePaint.setAlpha(0);
        if (isDebug) {
            mChartTestLinePaint.setAlpha(255);
        }

        mChartIndicatorPaint = new Paint();
        mChartIndicatorPaint.setStyle(Paint.Style.STROKE);
        mChartIndicatorPaint.setStrokeWidth(dp2px(1));

        mWhitePaint = new Paint();
        mWhitePaint.setStyle(Paint.Style.FILL);
        mWhitePaint.setColor(Color.parseColor("#ffffff"));

        mWhiteTextPaint = new TextPaint();
        mWhiteTextPaint.setColor(Color.parseColor("#ffffff"));
        mWhiteTextPaint.setTextSize(dp2px(11));
        mWhiteTextPaint.setAntiAlias(true);

        mWhiteTextPaintHeight = mWhiteTextPaint.descent() + mWhiteTextPaint.ascent();

        mDarkTextPaint = new TextPaint(mWhiteTextPaint);
        mDarkTextPaint.setAlpha(127);

        mPathIndicatorLine = new Path();
        mPathIndicatorLine.setFillType(Path.FillType.WINDING);

        mPathGradLine = new Path();
        mRectIndicator = new RectF();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int computedWidth = resolveSize(getChartRealWidth() + rightChartMarginSize + mIndicatorWidth, widthMeasureSpec);
        int viewHeight = mTopBlankSize + mChartContentHeight + mBottomBlankSize;
        int computedHeight = resolveSize(viewHeight, heightMeasureSpec);
        setMeasuredDimension(computedWidth, computedHeight);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewHeight = h;
        mViewWidth = w;
        bottomLine = mViewHeight - mBottomBlankSize;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isDebug) {
            Paint paint = new Paint();
            paint.setColor(Color.parseColor("#80000000"));
            canvas.drawRect(canvas.getClipBounds(), paint);
        }
        //画等级虚线背景
        drawGradeAxis(canvas);
        //画柱状图
        drawCharts(canvas);
        //画指示器
        drawIndicatorLine(canvas, mOffset, mCurrentData);
        //画底部的时间
        drawBottomDateInfo(canvas);

    }

    private void drawIndicatorLine(Canvas canvas, int offset, ITrendData trendData) {
        String textIndicator = trendData == null ? "00:00 --/--" : trendData.popTextInfo();

        float offsetLineY = getBezierY(mControlPoints, offset);

        mChartIndicatorPaint.setColor(trendData == null ? Color.argb(32, 255, 255, 255) : trendData.levelColor());
        mChartIndicatorPaint.setStyle(Paint.Style.FILL);
        mChartIndicatorPaint.setAntiAlias(true);

        /**
         * 气泡区域
         */

        mRectIndicator.left = offset;
        mRectIndicator.right = mRectIndicator.left + mIndicatorWidth;
        mRectIndicator.bottom = offsetLineY;
        mRectIndicator.top = mRectIndicator.bottom - mIndicatorHeight;

        canvas.drawRoundRect(mRectIndicator, dp12, dp12, mChartIndicatorPaint);

        //遮住左下角的半圆区域
        mRectIndicator.top = mRectIndicator.top + mRectIndicator.height() / 2;
        mRectIndicator.right = mRectIndicator.left + mRectIndicator.height();

        canvas.drawRect(mRectIndicator, mChartIndicatorPaint);

        float textWidth = mWhiteTextPaint.measureText(textIndicator);
        //text position for Y axis
        float textX;
        float textY;

        textX = offset + (mIndicatorWidth / 2 - textWidth / 2);
        textY = mRectIndicator.bottom - mIndicatorHeight / 2 - mWhiteTextPaintHeight / 2;
        canvas.drawText(textIndicator,
                textX,
                textY,
                mWhiteTextPaint);
    }

    public void onTrendCharScrollChanged(int x, int y, int oldX, int oldY) {
        if (mDataList.isEmpty()) {
            return;
        }
        float scale = getScaleByX(x);
        mOffset = (int) (mChartTrendWidthAbs * scale);
        //留出一个空隙
        if (mOffset >= mChartTrendWidthAbs) {
            mOffset = mOffset - mChartSpace;
        }

        int itemWidth = getItemWidthWithSpace();
        mCurrentIndex = mOffset / itemWidth;
        if (mCurrentIndex < 0) {
            mCurrentIndex = 0;
        }
        if (mCurrentIndex > mDataList.size() - 1) {
            mCurrentIndex = mDataList.size() - 1;
        }
        mCurrentData = mDataList.get(mCurrentIndex);

        int currentOffsetAbs = mCurrentIndex * itemWidth;
        mCurrentDay = currentOffsetAbs / mAverageDayWidth;

        if (mMoveListener != null) {
            mMoveListener.moveTo(mOffset, mCurrentDay);
        }

        invalidate();
    }


    /**
     * 根据选中的星期 计算滑动的距离
     *
     * @param position
     * @return
     */
    public int getOffsetByPosition(int position) {
        if (mDayRecord == null) {
            return 0;
        }
        float scale = (float) mDayRecord.get(position) / mListSize;
        //如果是今天 则移动到当前时间
        if (mDayListInfo.get(position).equals(getContext().getString(R.string.today)) && mCurrentTimePosition >= 0) {
            scale = (float) mCurrentTimePosition / mListSize;
        }
        return getXByScale(scale);
    }


    private float getScaleByX(int x) {
        int visibleScrollWidth = getScreenWidth() - mLeftMarginSize;
        //可以看到的横向滚动的距离
        int scrollVisibleWidth = getChartRealWidth() + rightChartMarginSize + mIndicatorWidth;
        return (float) x / (float) (scrollVisibleWidth - visibleScrollWidth);
    }

    private int getXByScale(float scale) {
        int visibleScrollWidth = getScreenWidth() - mLeftMarginSize;
        //可以看到的横向滚动的距离
        int scrollVisibleWidth = getChartRealWidth() + rightChartMarginSize + mIndicatorWidth;
        return Math.round((scale * (scrollVisibleWidth - visibleScrollWidth)));
    }


    public interface OnMoveListener {
        /**
         * 曲线移动监听
         *
         * @param offset   偏移距离
         * @param position 当前滑动的区域
         */
        void moveTo(int offset, int position);
    }

    public void setMoveListener(OnMoveListener mMoveListener) {
        this.mMoveListener = mMoveListener;
    }

    public int getChartRealWidth() {
        return mChartTrendWidthAbs;
    }

    private void drawBottomDateInfo(Canvas canvas) {
        int daySize = mDayListInfo.size();
        for (int i = 0; i < daySize; i++) {
            String text = mDayListInfo.get(i);
            canvas.drawText(text,
                    mBottomDayPositions.get(i).x,
                    mBottomDayPositions.get(i).y,
                    i == mCurrentDay ? mWhiteTextPaint : mDarkTextPaint
            );
        }
        int hourSize = mBottomHourPositions.size();
        for (int i = 0; i < hourSize; i++) {
            String textHour = "";
            try {
                textHour = mHourListInfo.get(i % 3);
            } catch (Exception e) {

            }
            canvas.drawText(textHour,
                    mBottomHourPositions.get(i).x,
                    mBottomHourPositions.get(i).y,
                    mDarkTextPaint
            );
        }
    }

    /**
     * 绘制三根背景虚线
     * @param canvas
     */
    private void drawGradeAxis(Canvas canvas) {
        canvas.drawPath(mPathGradLine, mGradeAxisPaint);
    }

    private void drawCharts(Canvas canvas) {
        for (int i = 0; i < mListSize; i++) {
            drawChartRect(canvas, i);
        }
        canvas.drawPath(mPathIndicatorLine, mChartTestLinePaint);// draw all path
    }

    /**
     * 封装的用于画柱状图的属性对象
     */
    private static class ChartRect {
        /**
         * 柱状图矩区域
         */
        RectF rectChart;
        /**
         * 柱子颜色
         */
        @ColorInt int color;
    }

    private void drawChartRect(Canvas canvas, int position) {
        ChartRect chartRect = mChartRectList.get(position);
        canvas.save();
        mChartPaint.setColor(chartRect.color);
        RectF rectChart = chartRect.rectChart;
        canvas.clipRect(rectChart.left, rectChart.top, rectChart.right, rectChart.bottom - roundRadius, Region.Op.INTERSECT);
        if (position < mCurrentTimePosition) {
            mChartPaint.setAlpha(102);
        } else {
            mChartPaint.setAlpha(255);
        }
        canvas.drawRoundRect(rectChart, roundRadius, roundRadius, mChartPaint);
        canvas.restore();
    }


    /**
     * 计算点柱状图的点坐标集合
     */
    private void calculateCurveDot() {
        mControlPoints.clear();
        mPathIndicatorLine.reset();
        mChartRectList.clear();

        float currentPosX;
        float currentPosY;
        float lastX = 0;
        float lastY = 0;
        int roundRadius = dp2;
        int lastDay = 0;
        int position = 0;
        Calendar calendar = Calendar.getInstance();
        for (int i = 0; i < mListSize; i++) {
            ITrendData data = mDataList.get(i);
            int dataWrapValue = data.warpValue();
            ChartRect chartRect = new ChartRect();

            float left = (i + 1) * mChartSpace + i * mChartItemWidth;
            float bottom = mViewHeight - mBottomBlankSize + roundRadius;
            float top = bottom - (dataWrapValue / mMaxAqiValue) * (mChartContentHeight) - roundRadius;
            float right = left + mChartItemWidth;
            chartRect.rectChart = new RectF(left, top, right, bottom);
            chartRect.color = data.levelColor();
            mChartRectList.add(chartRect);

            currentPosX = (i + 1) * mChartSpace + i * mChartItemWidth;
            currentPosY = mViewHeight - mBottomBlankSize - (dataWrapValue / mMaxAqiValue) * (mChartContentHeight) - mPaddingPopAndChart;
            if (i == 0) {
                mPathIndicatorLine.moveTo(currentPosX, currentPosY);
                lastX = currentPosX;
                lastY = currentPosY;
            } else {
                float cX = (currentPosX + lastX) / 2;
                float cY = (currentPosY + lastY) / 2;

                mPathIndicatorLine.quadTo(lastX, lastY, cX, cY);

                lastX = currentPosX;
                lastY = currentPosY;
            }
            mControlPoints.add(new PointF(currentPosX, currentPosY));

            //
            calendar.setTimeInMillis(data.timestamp());
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            mDayRecord.put(position, i);
            if (day != lastDay) {
                position++;
                lastDay = day;
            }
        }
    }

    /**
     * 一个 item 的宽度 带空隙距离
     *
     * @return
     */
    private int getItemWidthWithSpace() {
        return mChartItemWidth + mChartSpace;
    }

    public void fillData(List<ITrendData> dataList, List<String> dayListInfo, int dayCount) {
        mListSize = dataList.size();
        mControlPoints = new ArrayList<>(mListSize);
        mChartTrendWidthAbs = mListSize * getItemWidthWithSpace();
        mDataList = dataList;
        mDayCount = dayCount;
        mDayRecord = new SparseIntArray(mDayCount);
        mDayListInfo = dayListInfo;
        mCurrentTimePosition = getCurrentTimePosition(mDayListInfo);

        for (ITrendData data : dataList) {
            if (data.value() >= 300) {
                mBigModeChart = true;
                break;
            }
        }
        if (mBigModeChart) {
            mMaxAqiValue = 500f;
        } else {
            mMaxAqiValue = 300f;
        }

        mAverageDayWidth = mChartTrendWidthAbs / mDayCount;

        //初始化当前 item
        mCurrentIndex = 0;
        mCurrentData = mDataList.get(mCurrentIndex);

        //计算柱状图的点集合
        calculateCurveDot();
        //计算背景虚线的 path
        calculatedLinePath();
        //计算日期的位置坐标集合
        calculateBottomTextPoint();
        //发起重绘请求
        requestLayout();
    }

    private int getCurrentTimePosition(List<String> dayListInfo) {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        Log.i(TAG, "hour " + hour);
        for (int position = 0; position < dayListInfo.size(); position++) {
            //如果是今天 则移动到当前时间
            if (dayListInfo.get(position).equals(getContext().getString(R.string.today))) {
                return position * 24 + hour;
            }
        }
        return -1;
    }

    private void calculateBottomTextPoint() {
        if (mDayRecord == null) {
            return;
        }
        float y = mViewHeight - mBottomBlankSize / 2 - mWhiteTextPaintHeight / 2;
        for (int i = 0; i < mDayCount; i++) {
            float xDayOffset = mDayRecord.get(i) * getItemWidthWithSpace();
            mBottomDayPositions.add(new PointF(xDayOffset, y));
            for (int j = 1; j <= 3; j++) {
                mBottomHourPositions.add(
                        new PointF(xDayOffset + (j * 6) * getItemWidthWithSpace(), y)
                );
            }
        }
    }

    private void calculatedLinePath() {
        mPathGradLine.reset();
        for (int i = 0; i < mGradeCount; i++) {
            mPathGradLine.moveTo(0, bottomLine - averageGradleHeight * i);
            mPathGradLine.lineTo(getChartRealWidth(), bottomLine - averageGradleHeight * i);
        }
    }

    public List<ITrendData> getDataList() {
        return mDataList;
    }


    /**
     * dp转px
     */
    public int dp2px(float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal, getContext().getResources().getDisplayMetrics());
    }

    /**
     * 获得屏幕高度（随屏幕旋转改变）
     * 如果获取失败，则返回720
     *
     * @return 屏幕高度，或720
     */
    public int getScreenWidth() {
        if (mScreenWidth != 0) {
            return mScreenWidth;
        }
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        if (wm != null && wm.getDefaultDisplay() != null) {
            DisplayMetrics outMetrics = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(outMetrics);
            mScreenWidth = outMetrics.widthPixels;
            return mScreenWidth;
        } else {
            return 720;
        }
    }


    private float getBezierY(ArrayList<PointF> points, float x) {
        float targetY = 0;
        float moveX;
        float mCurrentPosX;
        float mCurrentPosY;
        float mNextPosX;
        float mNextPosY;
        try {
            if (points != null && points.size() > 0) {
                if (x < points.get(0).x) {
                    targetY = points.get(0).y;
                } else if (x > points.get(points.size() - 1).x) {
                    targetY = points.get(points.size() - 1).y;
                } else {
                    for (int i = 0; i < points.size() - 1; i++) {
                        mCurrentPosX = points.get(i).x;
                        mCurrentPosY = points.get(i).y;
                        mNextPosX = points.get(i + 1).x;
                        mNextPosY = points.get(i + 1).y;
                        if (x <= mNextPosX && x >= mCurrentPosX) {
                            if (mNextPosY != mCurrentPosY) {
                                moveX = (x - mCurrentPosX) / (mNextPosX - mCurrentPosX);
                                targetY = (float) (mCurrentPosY * Math.pow((1f - moveX), 3) + 3 * mCurrentPosY * moveX * Math.pow(1f - moveX, 2)
                                        + 3 * mNextPosY * (1f - moveX) * Math.pow(moveX, 2) + mNextPosY * Math.pow(moveX, 3));
                                break;
                            } else {
                                targetY = mCurrentPosY;
                                break;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return targetY;
    }

    public int getChartContentHeight() {
        return mChartContentHeight;
    }

    public int getLeftMarginSize() {
        return mLeftMarginSize;
    }

    public int getChartHeight() {
        return mTopBlankSize + mChartContentHeight + mBottomBlankSize;
    }

    public int getGradeCount() {
        return mGradeCount;
    }

    public int getBottomBlankSize() {
        return mBottomBlankSize;
    }

    public boolean isBigModeChart() {
        return mBigModeChart;
    }
}
