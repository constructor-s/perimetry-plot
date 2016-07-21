package com.shirunjie.graphing;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

/**
 * Created by shirunjie on 2016-07-08.
 */

public class PerimetryDataView extends View {

    private PerimetryData data = null;

    public PerimetryDataView(Context context) {
        super(context);
    }

    public PerimetryDataView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PerimetryDataView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public PerimetryDataView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        if (data != null) {
            new TextResultDrawer(canvas, data, this).draw();
        }
    }

    public void setData(PerimetryData data) {
        this.data = data;
    }

    private static class TextResultDrawer {
        private static final String TAG           = TextResultDrawer.class.getSimpleName();
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

        public TextResultDrawer(Canvas canvas, PerimetryData data, View view) {

            this.canvas = canvas;
            this.data = data;
            this.view = view;

            STROKE_WIDTH = getPxFromDp(2);
            BORDER = getPxFromDp(10);
            TICK_LENGTH = getPxFromDp(8);
            AXIS_LABEL_TEXTSIZE = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, view.getResources().getDisplayMetrics());

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
        private void draw() {

            final int columns = (int) Math.ceil(
                    (maxx > 0 ? maxx : 0) -
                            (minx < 0 ? minx : 0));
            final int rows = (int) Math.ceil(
                    (maxy > 0 ? maxy : 0) -
                            (miny < 0 ? miny : 0));

            double leftOffsetPx   = BORDER;
            double rightOffsetPx  = BORDER;
            double topOffsetPx    = BORDER;
            double bottomOffsetPx = BORDER;

            final double availableWidth  = view.getWidth() - leftOffsetPx - rightOffsetPx;
            final double availableHeight = view.getHeight() - topOffsetPx - bottomOffsetPx;

            // Adjust for aspect ratio
            double columnWidth = availableWidth / columns;
            double rowHeight   = availableHeight / rows;
            if (columnWidth > rowHeight) {
                //noinspection SuspiciousNameCombination
                columnWidth = rowHeight;

                leftOffsetPx = rightOffsetPx = (view.getWidth() - columnWidth * columns) / 2;

            } else {
                //noinspection SuspiciousNameCombination
                rowHeight = columnWidth;

                topOffsetPx = bottomOffsetPx = (view.getHeight() - rowHeight * rows) / 2;

            }

            final double centerX = (0 - (minx < 0 ? minx : 0)) * columnWidth + leftOffsetPx;
            final double centerY = ((maxy > 0 ? maxy : 0)) * rowHeight + topOffsetPx;

            drawVerticalLine((float) centerX, topOffsetPx, bottomOffsetPx);
            drawHorizontalLine((float) centerY, leftOffsetPx, rightOffsetPx);

            int yTick = (int) (Math.ceil((miny < 0 ? miny : 0) / TICK_INTERVAL) * TICK_INTERVAL);
            while (yTick <= maxy) {
                if (yTick != 0) {
                    final String label = yTick % (TICK_INTERVAL * 2) == 0 ? String.format("%d", yTick) : null;
                    drawYTick(-yTick * rowHeight + centerY, centerX, label);
                }
                yTick += TICK_INTERVAL;
            }

            int xTick = (int) (Math.ceil((minx < 0 ? minx : 0) / TICK_INTERVAL) * TICK_INTERVAL);
            while (xTick <= maxx) {
                if (xTick != 0) {
                    final String label = xTick % (TICK_INTERVAL * 2) == 0 ? String.format("%d", xTick) : null;
                    drawXTick(xTick * columnWidth + centerX, centerY, label);
                }
                xTick += TICK_INTERVAL;
            }

            //            Log.d(TAG, String.format("draw: Rows: %d, Columns %d, CenterX: %.3f, CenterY: %.3f",
            //                    rows, columns, centerX, centerY));
            //            Log.d(TAG, String.format("draw: getWidth(): %d, getHeight(): %d", view.getWidth(), view.getHeight()));
            //            Log.d(TAG, "draw: " + view.getResources().getDisplayMetrics());

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
                final float textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 20, view.getResources().getDisplayMetrics());
                paint.setTextSize(textSize);

                Rect bounds     = getTextRectBounds(dispStr, paint);
                int  textHeight = bounds.height();
                int  textWidth  = bounds.width();
                canvas.drawText(dispStr, 0, dispStr.length(),
                        (float) xDraw - textWidth * 0.5f, (float) yDraw + textHeight * 0.5f,
                        paint);
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
}
