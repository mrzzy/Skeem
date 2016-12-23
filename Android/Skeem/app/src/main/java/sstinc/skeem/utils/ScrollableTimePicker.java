package sstinc.skeem.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewParent;
import android.widget.TimePicker;

/**
 * This class extends {@link TimePicker} and makes the scroll locked when
 * interacting with the time picker.
 *
 * @see TimePicker
 */
public class ScrollableTimePicker extends TimePicker {

    public ScrollableTimePicker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ScrollableTimePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollableTimePicker(Context context) {
        super(context);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // Keeps events to time picker
        if (ev.getActionMasked() == MotionEvent.ACTION_DOWN) {
            ViewParent p = getParent();
            if (p != null) {
                p.requestDisallowInterceptTouchEvent(true);
            }
        }

        return false;
    }

}
