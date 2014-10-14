package com.test.news.animations;

import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;

/**
 * Created by Nikita on 14.10.2014.
 */
public class AnimUtils {

    public static void doYTranslation(final View view, int value){
        view.animate().translationY(value).setDuration(500).
                setInterpolator(new AccelerateInterpolator()).
                setListener(new HardwareAccelerationAnimationListener(view));
    }

    public static Integer moveBehindParentBottom(final View view, final ViewGroup parent){
        android.graphics.Rect rect = new Rect();
        view.getDrawingRect(rect);

        parent.offsetDescendantRectToMyCoords(view, rect);
        int value = parent.getBottom() - rect.top;
        doYTranslation(view, value);
        return value;
    }

}
