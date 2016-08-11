package com.shirunjie.graphing;

import android.graphics.RectF;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shirunjie on 2016-07-25.
 */

public class RectFHelper {
    public static boolean isRectsFit(List<RectF> rects) {
        boolean fit = true;
        for (int i = 0; i < rects.size(); i++) {
            RectF rect1 = rects.get(i);

            for (int j = i + 1; j < rects.size(); j++) {
                RectF rect2 = rects.get(j);
                if (RectF.intersects(rect1, rect2)) {
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

    public static List<RectF> getIncreasedSizeRects(List<RectF> rects,
                                                     float dLeft, float dTop, float dRight, float dBottom) {
        List<RectF> newRects = new ArrayList<>();
        for (RectF rect : rects) {
            newRects.add(new RectF(rect.left - dLeft, rect.top - dTop,
                    rect.right + dRight, rect.bottom + dBottom));
        }
        return newRects;
    }

}
