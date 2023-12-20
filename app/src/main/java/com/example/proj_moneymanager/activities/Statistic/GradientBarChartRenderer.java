package com.example.proj_moneymanager.activities.Statistic;

import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.buffer.BarBuffer;
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.renderer.BarChartRenderer;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;

public class GradientBarChartRenderer extends BarChartRenderer {

    private Path mBarShadowRectBuffer = new Path();

    public GradientBarChartRenderer(BarDataProvider chart, ChartAnimator animator, ViewPortHandler viewPortHandler) {
        super(chart, animator, viewPortHandler);
    }

    @Override
    public void drawDataSet(Canvas c, IBarDataSet dataSet, int index) {
        super.drawDataSet(c, dataSet, index);

        BarBuffer buffer = mBarBuffers[index];
        int color = ColorTemplate.MATERIAL_COLORS[0]; // Use the first color for the border

        // Apply gradient color to bars
        for (int j = 0; j < buffer.size() * mAnimator.getPhaseX(); j += 4) {
            float left = buffer.buffer[j];
            float right = buffer.buffer[j + 2];

            mRenderPaint.setShader(new LinearGradient(left, 0, right, 0,
                    ColorTemplate.MATERIAL_COLORS, null, Shader.TileMode.CLAMP));

            c.drawRect(left, buffer.buffer[j + 1], right, buffer.buffer[j + 3], mRenderPaint);

            // Draw rounded corners
            drawRoundedBar(c, left, right, buffer.buffer[j + 1], buffer.buffer[j + 3], color);
        }
    }

    private void drawRoundedBar(Canvas canvas, float left, float right, float top, float bottom, int color) {
        mBarShadowRectBuffer.reset();
        mBarShadowRectBuffer.addRoundRect(left, top, right, bottom, 8f, 8f, Path.Direction.CW);
        mRenderPaint.setColor(color);
        mRenderPaint.setStyle(Paint.Style.STROKE);
        mRenderPaint.setStrokeWidth(2f);

        canvas.drawPath(mBarShadowRectBuffer, mRenderPaint);
    }
}
