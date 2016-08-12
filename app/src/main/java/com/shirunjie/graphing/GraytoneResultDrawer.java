package com.shirunjie.graphing;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by shirunjie on 2016-07-21.
 */
public class GraytoneResultDrawer {
    private static final String TAG           = GraytoneResultDrawer.class.getSimpleName();
    private static final int    TICK_INTERVAL = 5;
    private final float AXIS_LABEL_TEXTSIZE;
    private final float BORDER;
    private final float STROKE_WIDTH;
    private final float TICK_LENGTH;
    private final float POINT_SIZE;

    private final View          view;
    private final PerimetryData data;
    private final Canvas        canvas;

    private double minx;
    private double miny;
    private double maxx;
    private double maxy;
    private Paint paint = new Paint();

    public GraytoneResultDrawer(Canvas canvas, PerimetryData data, View view) {

        this.canvas = canvas;
        this.data = data;
        this.view = view;

        STROKE_WIDTH = getPxFromDp(2);
        BORDER = getPxFromDp(30);
        TICK_LENGTH = getPxFromDp(8);
        AXIS_LABEL_TEXTSIZE = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, view.getResources().getDisplayMetrics());
        POINT_SIZE = getPxFromDp(1);

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
        double columnWidth;
        double rowHeight;
        columnWidth = rowHeight = drawHeight / columns;

        final double centerX = (drawColumns / 2.0) * columnWidth + leftOffsetPx;
        final double centerY = (drawRows / 2.0) * rowHeight + topOffsetPx;


        // Interpolate data
        Map<Integer, Map<Integer, Double>> dataMap = mapData(data);
        Map<Integer, Map<Integer, Double>> newMap  = interpolate(dataMap);

        // Draw
        double cellHalfWidth = columnWidth / 2;
        for (int x = (int) Math.round(minx); x <= (int) Math.round(maxx); x++) {
            for (int y = (int) Math.round(miny); y <= (int) Math.round(maxy); y++) {
                // Leave out space for axes
                if (x != 0 && y != 0) {
                    Double dbValue = get(newMap, x, y);
                    if (dbValue != null) {
                        final double xDraw = x * columnWidth + centerX;
                        final double yDraw = -y * rowHeight + centerY;

//                        int grayValue = (int) (dbValue / 40 * 255);
//                        paint.setColor((0xFF << 24) | (grayValue << 16) | (grayValue << 8) | grayValue);


                        //                    canvas.drawRect((float) (xDraw - cellHalfWidth), (float) (yDraw - cellHalfWidth),
                        //                            (float) (xDraw + cellHalfWidth), (float) (yDraw + cellHalfWidth), paint);

                        paint.setColor(0xFF << 24);
                        //                    for (int xLoc = (int) xDraw - cellHalfWidth; xLoc < xDraw + cellHalfWidth; xLoc++) {
                        //                        for (int yLoc = (int) yDraw - cellHalfWidth; yLoc < yDraw + cellHalfWidth; yLoc++) {
                        //                            if (Math.random() > dbValue / 40) {
                        //                                canvas.drawPoint(xLoc, yLoc, paint);
                        //                            }
                        //                        }
                        //                    }


                        // Calculate spacing between points
                        double spacing = Math.max(1, Math.min(1.05, (dbValue / 30.5)) * cellHalfWidth);
                        float pointSizeFactor = (float) Math.min(Math.max((30.5 - dbValue) / 5, 1), 1.8);
                        // Draw starting from each cell's center
                        for (double dx = 0; dx <= cellHalfWidth; dx += spacing) {
                            for (double dy = 0; dy <= cellHalfWidth; dy += spacing) {
                                //                            for (int i = 0; i < pointSize; i++) {
                                //                                for (int j = 0; j < pointSize; j++) {
                                //                                    pts.add(dx + i);
                                //                                    pts.add(dy + j);
                                //                                }
                                //                            }
                                //                            canvas.drawCircle((float) (xDraw), (float) (yDraw), pointSize / 2, paint);
                                canvas.drawCircle((float) (xDraw + dx), (float) (yDraw + dy), POINT_SIZE / 2 * pointSizeFactor, paint);
                                canvas.drawCircle((float) (xDraw - dx), (float) (yDraw + dy), POINT_SIZE / 2 * pointSizeFactor, paint);
                                canvas.drawCircle((float) (xDraw + dx), (float) (yDraw - dy), POINT_SIZE / 2 * pointSizeFactor, paint);
                                canvas.drawCircle((float) (xDraw - dx), (float) (yDraw - dy), POINT_SIZE / 2 * pointSizeFactor, paint);

                            }
                        }

                        //                    if (grayValue < 0x80) {
                        //                        paint.setColor(0xFFFFFFFF);
                        //                    } else {
                        //                        paint.setColor(0xFF000000);
                        //                    }
                        //                    paint.setTextSize(cellHalfWidth);
                        //                    canvas.drawText(String.format("%.0f", dbValue), (float) (xDraw - cellHalfWidth), (float) (yDraw), paint);
                    }

                }
            }
        }


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


    }

    private Map<Integer, Map<Integer, Double>> interpolate(Map<Integer, Map<Integer, Double>> dataMap) {
        Map<Integer, Map<Integer, Double>> newMap = new HashMap<>();

        Iterator<Map.Entry<Integer, Map<Integer, Double>>> iterator = dataMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, Map<Integer, Double>> pair   = iterator.next();
            int                                      x      = pair.getKey();
            Map<Integer, Double>                     column = pair.getValue();

            ArrayList<Map.Entry<Integer, Double>> list = new ArrayList<>(column.entrySet());
            Collections.sort(list, new Comparator<Map.Entry<Integer, Double>>() {
                @Override
                public int compare(Map.Entry<Integer, Double> lhs, Map.Entry<Integer, Double> rhs) {
                    return lhs.getKey() - rhs.getKey();
                }
            });

            Iterator<Map.Entry<Integer, Double>> columnIterator = list.iterator();
            Map.Entry<Integer, Double>           prevEntry      = null;
            Map.Entry<Integer, Double>           currEntry      = null;
            while (columnIterator.hasNext()) {
                if (prevEntry == null) {
                    // first time
                    prevEntry = columnIterator.next();
                } else {
                    if (currEntry == null) {
                        // second time
                        currEntry = columnIterator.next();
                    } else {
                        // normal
                        prevEntry = currEntry;
                        currEntry = columnIterator.next();
                    }
                    int prevY    = prevEntry.getKey();
                    int currY    = currEntry.getKey();
                    int distance = currY - prevY;
                    for (int y = prevY; y <= currY; y++) {
                        double value =
                                prevEntry.getValue() * (currY - y) / distance +
                                        currEntry.getValue() * (y - prevY) / distance;
                        set(newMap, x, y, value);
                    }
                }
            }
        }

        for (int y = (int) Math.round(miny); y <= (int) Math.round(maxy); y++) {
            SortedSet<Map.Entry<Integer, Double>> row = new TreeSet<>(new Comparator<Map.Entry<Integer, Double>>() {
                @Override
                public int compare(Map.Entry<Integer, Double> lhs, Map.Entry<Integer, Double> rhs) {
                    return lhs.getKey() - rhs.getKey();
                }
            });
            for (int x = (int) Math.round(minx); x <= (int) Math.round(maxx); x++) {
                Double value = get(newMap, x, y);
                if (value != null) {
                    Map.Entry<Integer, Double> entry = new AbstractMap.SimpleEntry<>(x, value);
                    row.add(entry);
                }
            }

            List<Map.Entry<Integer, Double>> rowList = new ArrayList<>(row);

            Iterator<Map.Entry<Integer, Double>> columnIterator = rowList.iterator();
            Map.Entry<Integer, Double>           prevEntry      = null;
            Map.Entry<Integer, Double>           currEntry      = null;
            while (columnIterator.hasNext()) {
                if (prevEntry == null) {
                    // first time
                    prevEntry = columnIterator.next();
                } else {
                    if (currEntry == null) {
                        // second time
                        currEntry = columnIterator.next();
                    } else {
                        // normal
                        prevEntry = currEntry;
                        currEntry = columnIterator.next();
                    }
                    int prevX    = prevEntry.getKey();
                    int currX    = currEntry.getKey();
                    int distance = currX - prevX;
                    for (int x = prevX; x <= currX; x++) {
                        double value =
                                prevEntry.getValue() * (currX - x) / distance +
                                        currEntry.getValue() * (x - prevX) / distance;
                        set(newMap, x, y, value);
                    }
                }
            }
        }
        return newMap;
    }

//    private Entry[] findNeighbors(Map<Integer, Map<Integer, Double>> dataMap, int x, int y, int n, int maxDist) {
//        Entry[] results  = new Entry[n];
//        int     stepsOut = 0;
//        int     found    = 0;
//        while (stepsOut <= maxDist && found < n) {
//            stepsOut++;
//            for (int dx = 0; dx < stepsOut; dx++) {
//                int dy = stepsOut - dx;
//                for (int xSign = -1; xSign <= (dx != 0 ? 1 : -1); xSign += 2) {
//                    for (int ySign = -1; ySign <= (dy != 0 ? 1 : -1); ySign += 2) {
//                        if (found >= n) {
//                            break;
//                        }
//                        int    xLoc  = x + xSign * dx;
//                        int    yLoc  = y + ySign * dy;
//                        Double value = get(dataMap, xLoc, yLoc);
//                        if (value != null && value != Double.NaN) {
//                            results[found] = new Entry(xLoc, yLoc, value);
//                            found++;
//                        }
//                    }
//                }
//            }
//        }
//        return results;
//    }
//
//    private void normalize(double[] weights) {
//        double sum = 0;
//        for (double weight : weights) {
//            sum += weight;
//        }
//        if (sum > 0) {
//            for (int i = 0; i < weights.length; i++) {
//                weights[i] /= sum;
//            }
//        }
//    }


    //    /**
    //     * @param dataMap
    //     * @param x
    //     * @param y
    //     * @param range
    //     * @return By CSS standard: [top, right, bottom, left]
    //     */
    //    private Entry[] findNeighbors(Map<Integer, Map<Integer, Double>> dataMap, int x, int y, int range) {
    //        Entry[] result = new Entry[4];
    //        for (int i = x - range / 2; i <= x + range / 2; i++) {
    //            for (int j = y - range / 2; j <= y + range / 2; j++) {
    //                Double value = get(dataMap, i, j);
    //                if (value != null) {
    //                    putValue(result, new Entry(i, j, value), x, y);
    //                }
    //            }
    //        }
    //        return result;
    //    }
    //
    //    private void putValue(Entry[] result, Entry entry, int originX, int originY) {
    //        double shiftedX = entry.getX() - originX;
    //        double shiftedY = entry.getY() - originY;
    //        int index;
    //        if (shiftedY > shiftedX) {
    //            // Top or left
    //            if (shiftedY > -shiftedX) {
    //                index = 0;
    //            } else {
    //                index = 3;
    //            }
    //        } else {
    //            if (shiftedY > -shiftedX) {
    //                index = 1;
    //            } else {
    //                index = 2;
    //            }
    //        }
    //
    //        Entry existing = result[index];
    //        if (existing == null) {
    //            result[index] = entry;
    //        } else {
    //            double currDist = getDistance(new double[]{existing.getX(), existing.getY()}, new double[]{originX, originY});
    //            double newDist = getDistance(new double[]{shiftedX, shiftedY}, new double[]{0, 0});
    //            if (newDist < currDist) {
    //                result[index] = entry;
    //            }
    //        }
    //    }

//    private static double getDistance(double[] point1, double[] point2) {
//        if (point1.length == point2.length) {
//            double sum = 0;
//            for (int i = 0; i < point1.length; i++) {
//                sum += Math.pow(point1[i] - point2[i], 2);
//            }
//            return Math.sqrt(sum);
//        } else {
//            if (BuildConfig.DEBUG) {
//                throw new IndexOutOfBoundsException("Coordinate arguments must be of the same length");
//            } else {
//                return -1;
//            }
//        }
//    }

    //    private Entry findNeighbor(Map<Integer, Map<Integer, Double>> dataMap, int x, int y, int dx, int dy) {
    //        return findNeighbor(dataMap, x, y, dx, dy, 0);
    //    }
    //
    //    private Entry findNeighbor(Map<Integer, Map<Integer, Double>> dataMap, int x, int y, int dx, int dy, int nTry) {
    //        if (nTry < 7) {
    //            Double value = get(dataMap, x + dx, y + dy);
    //            if (value != null) {
    //                return new Entry(x + dx, y + dy, value);
    //            } else {
    //                return findNeighbor(dataMap, x + dx, y + dy, dx, dy, nTry + 1);
    //            }
    //        }
    //        return null;
    //    }

    @Nullable
    private static Double get(Map<Integer, Map<Integer, Double>> map, int x, int y) {
        if (map != null) {
            Map<Integer, Double> col = map.get(x);
            if (col != null) {
                return col.get(y);
            }
        }
        return null;
    }

    private static void set(Map<Integer, Map<Integer, Double>> map, int x, int y, double value) {
        if (map != null) {
            Map<Integer, Double> col = map.get(x);
            if (col == null) {
                Map<Integer, Double> newCol = new HashMap<>();
                newCol.put(y, value);
                map.put(x, newCol);
            } else {
                col.put(y, value);
            }
        }
    }

    private static Map<Integer, Map<Integer, Double>> mapData(PerimetryData data) {
        Map<Integer, Map<Integer, Double>> dataMap = new HashMap<>();
        for (Entry entry : data) {
            double value = entry.getValue();
            if (value >= 0) {
                int x = (int) Math.round(entry.getX());
                int y = (int) Math.round(entry.getY());
                set(dataMap, x, y, value);
            }
        }
        return dataMap;
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
