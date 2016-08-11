package com.shirunjie.graphing;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shirunjie on 2016-07-21.
 */
public class AdaptiveTextResultDrawer {
    private static final String TAG           = AdaptiveTextResultDrawer.class.getSimpleName();
    public static final  int    TICK_INTERVAL = 5;
    private final float AXIS_LABEL_TEXTSIZE;
    private final float BORDER;
    private final float STROKE_WIDTH;
    private final float TICK_LENGTH;

    private final View          view;
    private final PerimetryData data;
    private final Canvas        canvas;

    private double minx;
    private double miny;
    private double maxx;
    private double maxy;
    private double columnWidth;
    private double rowHeight;
    private double centerX;
    private double centerY;

    public AdaptiveTextResultDrawer(Canvas canvas, PerimetryData data, View view) {

        this.canvas = canvas;
        this.data = data;
        this.view = view;

        STROKE_WIDTH = getPxFromDp(2);
        BORDER = getPxFromDp(10);
        TICK_LENGTH = getPxFromDp(8);
        AXIS_LABEL_TEXTSIZE = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, view.getResources().getDisplayMetrics());

        findMinAndMax();
    }

    private float getPxFromDp(float dp) {
        return getPxFromDp(dp, view.getResources().getDisplayMetrics());
    }

    private static float getPxFromDp(float dp, DisplayMetrics displayMetrics) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, displayMetrics);
    }

    private void findMinAndMax() {
        final double[] maxMin = data.getMaxMin();
        minx = maxMin[PerimetryData.MINX];
        miny = maxMin[PerimetryData.MINY];
        maxx = maxMin[PerimetryData.MAXX];
        maxy = maxMin[PerimetryData.MAXY];
    }

    // TODO: Clean up code
    public void draw() {
        final int columns = (int) (2 * Math.ceil(Math.max(-minx, maxx)));
        final int rows    = (int) (2 * Math.ceil(Math.max(-miny, maxy)));

        final int drawColumns;
        final int drawRows;
        drawColumns = drawRows = Math.max(columns, rows);

        double leftOffsetPx   = BORDER;
        double rightOffsetPx  = BORDER;
        double topOffsetPx    = BORDER;
        double bottomOffsetPx = BORDER;

        final double availableWidth  = view.getWidth() - leftOffsetPx - rightOffsetPx;
        final double availableHeight = view.getHeight() - topOffsetPx - bottomOffsetPx;

        final double drawWidth;
        final double drawHeight;
        drawWidth = drawHeight = Math.min(availableWidth, availableHeight);

        leftOffsetPx = rightOffsetPx = (view.getWidth() - drawWidth) / 2;
        topOffsetPx = bottomOffsetPx = (view.getHeight() - drawHeight) / 2;

        // Adjust for aspect ratio
        columnWidth = rowHeight = drawHeight / columns;

        centerX = (drawColumns / 2.0) * columnWidth + leftOffsetPx;
        centerY = (drawRows / 2.0) * rowHeight + topOffsetPx;

        drawVerticalLine((float) centerX, topOffsetPx, bottomOffsetPx);
        drawHorizontalLine((float) centerY, leftOffsetPx, rightOffsetPx);

        int yTick = (int) (-drawRows / 2.0 / TICK_INTERVAL) * TICK_INTERVAL;
        while (yTick <= drawRows / 2.0) {
            if (yTick != 0) {
                final String label = yTick % (TICK_INTERVAL * 2) == 0 ? String.format("%d", yTick) : null;
                drawYTick(-yTick * rowHeight + centerY, centerX, label);
            }
            yTick += TICK_INTERVAL;
        }

        int xTick = (int) (-drawColumns / 2.0 / TICK_INTERVAL) * TICK_INTERVAL;
        while (xTick <= drawColumns / 2.0) {
            if (xTick != 0) {
                final String label = xTick % (TICK_INTERVAL * 2) == 0 ? String.format("%d", xTick) : null;
                drawXTick(xTick * columnWidth + centerX, centerY, label);
            }
            xTick += TICK_INTERVAL;
        }

        double adaptiveTextSize = getAdaptiveTextSize();

        final Paint paint    = new Paint();
        final float textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, (float)adaptiveTextSize, view.getResources().getDisplayMetrics());
        paint.setTextSize(textSize);
        paint.setTypeface(Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL));
        for (Entry entry : data.getEntries()) {
            final double x = entry.getX();
            final double y = entry.getY();

            final double xDraw = x * columnWidth + centerX;
            final double yDraw = -y * rowHeight + centerY;

            CharSequence dispStr = entry.getStringLabel();
            if (dispStr == null) {
                dispStr = String.format("%.0f", entry.getValue());
            }




            Rect bounds     = getTextRectBounds(dispStr, paint);
            int  textHeight = bounds.height();
            int  textWidth  = bounds.width();
            canvas.drawText(dispStr, 0, dispStr.length(),
                    (float) xDraw - textWidth * 0.5f, (float) yDraw + textHeight * 0.5f,
                    paint);
        }

    }

    private double getAdaptiveTextSize() {
        return getAdaptiveTextSize(0);
    }

    private double getAdaptiveTextSize(double minSize) {
        final int offset = 0;
        final int step = 2;

        List<RectF> rectFs = new ArrayList<>();
        for (Entry entry : data.getEntries()) {
            final double x = entry.getX();
            final double y = entry.getY();

            final double xDraw = x * columnWidth + centerX;
            final double yDraw = -y * rowHeight + centerY;

            CharSequence dispStr = entry.getStringLabel();
            if (dispStr == null) {
                dispStr = String.format("%.0f", entry.getValue());
            }

            final Paint paint    = new Paint();
            final float textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, (float)minSize + offset, view.getResources().getDisplayMetrics());
            paint.setTextSize(textSize);

            RectF bounds     = new RectF(getTextRectBounds(dispStr, paint));
            bounds.offset((float)xDraw, (float)yDraw);
            rectFs.add(bounds);
        }
        if (RectFHelper.isRectsFit(rectFs)) {
            return getAdaptiveTextSize(minSize + step);
        } else {
            double size = minSize - step;
            return Math.max(0, size) * (0.382 * Math.exp(-0.1 * size) + 0.618);
        }
    }

    @NonNull
    private Rect getTextRectBounds(CharSequence dispStr, Paint paint) {
        Rect bounds = new Rect();
        paint.getTextBounds(dispStr.toString(), 0, dispStr.length(), bounds);
        return bounds;
    }


    private void drawHorizontalLine(double y, double leftOffset, double rightOffset) {
        final Paint paint = new Paint();
        paint.setStrokeWidth(STROKE_WIDTH);
        canvas.drawLine(0 + (float) leftOffset, (float) y, view.getWidth() - (float) rightOffset, (float) y, paint);
    }

    private void drawVerticalLine(double x, double topOffset, double bottomOffset) {
        final Paint paint = new Paint();
        paint.setStrokeWidth(STROKE_WIDTH);
        canvas.drawLine((float) x, 0 + (float) topOffset, (float) x, view.getHeight() - (float) bottomOffset, paint);
    }

    private void drawYTick(double y, double centerX, String labelText) {
        final Paint paint = new Paint();
        paint.setStrokeWidth(STROKE_WIDTH);
        canvas.drawLine((float) centerX - TICK_LENGTH / 2, (float) y,
                (float) centerX + TICK_LENGTH / 2, (float) y, paint);

        if (labelText != null) {
            if (!labelText.contains("°")) {
                labelText += "°";
            }
            paint.setTextSize(
                    AXIS_LABEL_TEXTSIZE
            );
            final int height = getTextRectBounds(labelText, paint).height();
            canvas.drawText(labelText, (float) centerX + getPxFromDp(5), (float) y + height / 2.0f, paint);
        }
    }

    private void drawXTick(double x, double centerY, String labelText) {
        final Paint paint = new Paint();
        paint.setStrokeWidth(STROKE_WIDTH);
        canvas.drawLine((float) x, (float) centerY - TICK_LENGTH / 2,
                (float) x, (float) centerY + TICK_LENGTH / 2, paint);

        if (labelText != null) {
            if (!labelText.contains("°")) {
                labelText += "°";
            }
            paint.setTextSize(
                    AXIS_LABEL_TEXTSIZE
            );
            final int width = getTextRectBounds(labelText, paint).width();
            canvas.drawText(labelText, (float) x - width / 2.0f, (float) centerY - getPxFromDp(5), paint);
        }
    }
}
