package com.m520it.mymobilsafe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * @author Kiven
 * @time 2016-12-12  21:32
 * Email f842728368@163.com
 * @desc 自定义的可聚焦的TextView
 */

public class FocusedTextView extends TextView {

    public FocusedTextView(Context context) {
        super(context);
    }

    public FocusedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean isFocused() {
        return true;
    }
}
