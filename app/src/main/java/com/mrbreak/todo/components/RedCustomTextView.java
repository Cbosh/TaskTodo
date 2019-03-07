package com.mrbreak.todo.components;

import android.content.Context;
import android.util.AttributeSet;

import com.mrbreak.todo.R;
import com.mrbreak.todo.components.basecomponents.BaseTextView;

public class RedCustomTextView extends BaseTextView {

    public RedCustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setTextAppearance(context, android.R.style.TextAppearance_Small);
        setTextAppearance(context, R.style.redColorViewStyle);

        setTypeface(getTypefaceRegular());
    }
}
