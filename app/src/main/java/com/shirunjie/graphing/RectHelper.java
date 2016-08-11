package com.shirunjie.graphing;

import android.graphics.Rect;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shirunjie on 2016-07-25.
 */
public class RectHelper {
    public static boolean isRectsFit(List<Rect> rects) {
        boolean fit = true;
        for (int i = 0; i < rects.size(); i++) {
            Rect rect1 = rects.get(i);

            for (int j = i + 1; j < rects.size(); j++) {
                Rect rect2 = rects.get(j);
                if (Rect.intersects(rect1, rect2)) {
                    fit = false;
                    break;
                }
            }

            if (!fit) {
                break;
            }
        }
        return fit;
    }

    public static List<Rect> getIncreasedSizeRects(List<Rect> rects,
                                                    int dLeft, int dTop, int dRight, int dBottom) {
        List<Rect> newRects = new ArrayList<>();
        for (Rect rect : rects) {
            newRects.add(new Rect(rect.left - dLeft, rect.top - dTop,
                    rect.right + dRight, rect.bottom + dBottom));
        }
        return newRects;
    }
}
