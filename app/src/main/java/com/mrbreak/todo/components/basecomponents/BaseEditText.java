package com.mrbreak.todo.components.basecomponents;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import com.mrbreak.todo.constants.Constants;

public class BaseEditText extends android.support.v7.widget.AppCompatEditText {

    private Typeface typefaceSemiBold;
    private Typeface typefaceSemiRegular;

    public BaseEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public BaseEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            createTypeface(context, attrs);
        }
    }

    public BaseEditText(Context context) {
        super(context);
    }


    private void createTypeface(Context context, AttributeSet attrs) {
        typefaceSemiBold = Typeface.createFromAsset(context.getAssets(),
                Constants.FONT_NUNITO_SEMIBOLD);
        typefaceSemiRegular = Typeface.createFromAsset(context.getAssets(),
                Constants.FONT_NUNITO_REGULAR);
    }


    @Override
    public View focusSearch(int direction) {
        View v = super.focusSearch(direction);
        if (v != null) {
            if (v.isEnabled()) {
                return v;
            } else {
                // keep searching
                return v.focusSearch(direction);
            }
        }
        return v;
    }

    public Typeface getTypefaceSemiRegular() {
        return typefaceSemiRegular;
    }

    public void setTypefaceSemiRegular(Typeface typefaceSemiRegular) {
        this.typefaceSemiRegular = typefaceSemiRegular;
    }

    public Typeface getTypefaceSemiBold() {
        return typefaceSemiBold;
    }

    public void setTypefaceSemiBold(Typeface typefaceSemiBold) {
        this.typefaceSemiBold = typefaceSemiBold;
    }
}
