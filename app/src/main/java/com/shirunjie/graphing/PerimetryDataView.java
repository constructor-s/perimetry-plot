package com.shirunjie.graphing;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
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
        private static final String TAG = TextResultDrawer.class.getSimpleName();
        private final float STROKE_WIDTH;

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

            STROKE_WIDTH = getPxFromDp(2, view.getResources().getDisplayMetrics());
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
            findMinAndMax();

            final int columns = (int) Math.ceil(
                    (maxx > 0 ? maxx : 0) -
                            (minx < 0 ? minx : 0));
            final int rows = (int) Math.ceil(
                    (maxy > 0 ? maxy : 0) -
                            (miny < 0 ? miny : 0));

            double columnLeftOffset  = columns * 0.05;
            double columnRightOffset = columnLeftOffset;
            double rowTopOffset      = rows * 0.05;
            double rowBottomOffset   = rowTopOffset;

            double columnWidth = (double) view.getWidth() / (columns + columnLeftOffset + columnRightOffset);
            double rowHeight   = (double) view.getHeight() / (rows + rowTopOffset + rowBottomOffset);

            if (columnWidth > rowHeight) {
                final double diff = columns * (columnWidth - rowHeight) / rowHeight;
                columnLeftOffset += diff / 2;
                columnRightOffset += diff / 2;
                columnWidth = (double) view.getWidth() / (columns + columnLeftOffset + columnRightOffset);
                //                rowHeight   = (double) view.getHeight() / (rows + rowTopOffset + rowBottomOffset);
            } else if (columnWidth < rowHeight) {
                final double diff = -columns * (columnWidth - rowHeight) / columnWidth;
                rowTopOffset += diff / 2;
                rowBottomOffset += diff / 2;
                //                columnWidth = (double) view.getWidth() / (columns + columnLeftOffset + columnRightOffset);
                rowHeight = (double) view.getHeight() / (rows + rowTopOffset + rowBottomOffset);
            }

            final double centerX = (0 - (minx < 0 ? minx : 0) + columnLeftOffset) * columnWidth;
            final double centerY = ((maxy > 0 ? maxy : 0) + rowTopOffset) * rowHeight;

            drawVerticalLine((float) centerX, rowTopOffset * rowHeight, rowBottomOffset * rowHeight);
            drawHorizontalLine((float) centerY, columnLeftOffset * columnWidth, columnRightOffset * columnWidth);

            int yTick = (int) (Math.ceil(miny / 10) * 10);
            while (yTick <= maxy) {
                if (yTick != 0) {
                    drawYTick(-yTick * rowHeight + centerY, centerX, String.format("%d", yTick));
                }
                yTick += 10;
            }

            int xTick = (int) (Math.ceil(minx / 10) * 10);
            while (xTick <= maxx) {
                if (xTick != 0) {
                    drawXTick(xTick * columnWidth + centerX, centerY, String.format("%d", xTick));
                }
                xTick += 10;
            }

            Log.d(TAG, String.format("draw: Rows: %d, Columns %d, CenterX: %.3f, CenterY: %.3f",
                    rows, columns, centerX, centerY));
            Log.d(TAG, String.format("draw: getWidth(): %d, getHeight(): %d", view.getWidth(), view.getHeight()));
            Log.d(TAG, "draw: " + view.getResources().getDisplayMetrics());

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
                final float textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14, view.getResources().getDisplayMetrics());
                paint.setTextSize(textSize);

                Rect bounds = new Rect();
                paint.getTextBounds(dispStr.toString(), 0, dispStr.length(), bounds);
                int textHeight = bounds.height();
                int textWidth  = bounds.width();
                canvas.drawText(dispStr, 0, dispStr.length(),
                        (float) xDraw - textWidth * 0.5f, (float) yDraw + textHeight * 0.5f,
                        paint);
            }
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
            float length = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, view.getResources().getDisplayMetrics());
            canvas.drawLine((float) centerX - length / 2, (float) y,
                    (float) centerX + length / 2, (float) y, paint);

            if (labelText != null) {
                paint.setTextSize(
                        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 8, view.getResources().getDisplayMetrics())
                );
                canvas.drawText(labelText, (float) centerX, (float) y, paint);
            }
        }

        private void drawXTick(double x, double centerY, String labelText) {
            final Paint paint = new Paint();
            paint.setStrokeWidth(STROKE_WIDTH);
            float length = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, view.getResources().getDisplayMetrics());
            canvas.drawLine((float) x, (float) centerY - length / 2,
                    (float) x, (float) centerY + length / 2, paint);

            if (labelText != null) {
                paint.setTextSize(
                        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 8, view.getResources().getDisplayMetrics())
                );
                canvas.drawText(labelText, (float) x, (float) centerY, paint);
            }
        }
    }
}
