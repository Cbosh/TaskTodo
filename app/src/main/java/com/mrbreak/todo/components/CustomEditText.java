package com.mrbreak.todo.components;

import android.content.Context;
import android.util.AttributeSet;

import com.mrbreak.todo.R;
import com.mrbreak.todo.components.basecomponents.BaseEditText;

public class CustomEditText extends BaseEditText {

    public CustomEditText(Context context) {
        super(context);
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);

        setTextAppearance(context, android.R.style.TextAppearance_Medium);
        setTextAppearance(context, R.style.blackColorViewStyle);
        setTypeface(getTypefaceSemiRegular());
    }

    public CustomEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setTypeface(getTypefaceSemiBold());
    }
}
