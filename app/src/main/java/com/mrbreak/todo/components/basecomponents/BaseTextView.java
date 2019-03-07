package com.mrbreak.todo.components.basecomponents;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import com.mrbreak.todo.constants.Constants;

public class BaseTextView extends android.support.v7.widget.AppCompatTextView {

    private Typeface typefaceBold;
    private Typeface typefaceRegular;
    private Typeface typefaceSemiBold;

    public BaseTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        createTypeface(context, attrs);
    }

    private void createTypeface(Context context, AttributeSet attrs) {
        typefaceRegular = Typeface.createFromAsset(context.getAssets(), Constants.FONT_NUNITO_REGULAR);
        typefaceBold = Typeface.createFromAsset(context.getAssets(), Constants.FONT_NUNITO_BOLD);
        typefaceSemiBold = Typeface.createFromAsset(context.getAssets(), Constants.FONT_NUNITO_SEMIBOLD);
    }

    public Typeface getTypefaceBold() {
        return typefaceBold;
    }

    public Typeface getTypefaceRegular() {
        return typefaceRegular;
    }

    public Typeface getTypefaceSemiBold() {
        return typefaceSemiBold;
    }

    public void setTypefaceSemiBold(Typeface typefaceSemiBold) {
        this.typefaceSemiBold = typefaceSemiBold;
    }
}
