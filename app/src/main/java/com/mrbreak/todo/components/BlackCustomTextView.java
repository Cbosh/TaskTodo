package com.mrbreak.todo.components;

import android.content.Context;
import android.util.AttributeSet;

import com.mrbreak.todo.R;
import com.mrbreak.todo.components.basecomponents.BaseTextView;

public class BlackCustomTextView extends BaseTextView {

    public BlackCustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setTextAppearance(context, android.R.style.TextAppearance_Small);
        setTextAppearance(context, R.style.blackColorViewStyle);

        setTypeface(getTypefaceRegular());
    }
}
