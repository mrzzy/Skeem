package sstinc.skeem.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewParent;
import android.widget.DatePicker;

/**
 * This class extends {@link DatePicker} and makes the scroll locked when
 * interacting with the date picker.
 *
 * @see DatePicker
 */
public class ScrollableDatePicker extends DatePicker {

    public ScrollableDatePicker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ScrollableDatePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollableDatePicker(Context context) {
        super(context);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // Keeps events to date picker
        if (ev.getActionMasked() == MotionEvent.ACTION_DOWN) {
            ViewParent p = getParent();
            if (p != null) {
                p.requestDisallowInterceptTouchEvent(true);
            }
        }

        return false;
    }
}
