package com.exceedgulf.alainzoo.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.AttributeSet;

import com.exceedgulf.alainzoo.R;


public class CustomCheckBox extends AppCompatCheckBox {

    public CustomCheckBox(Context context) {
        super(context);
        init(context, null);
    }

    public CustomCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CustomCheckBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(final Context mContext, final AttributeSet attrs) {
        if (attrs != null) {
            final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CustomFontFamily);
            final String fontFamily = a.getString(R.styleable.CustomFontFamily_fontFamily);
            try {
                if (fontFamily != null) {
                    final Typeface myTypeface = Typeface.createFromAsset(getContext().getAssets(), fontFamily);
                    setTypeface(myTypeface);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            a.recycle();
        }
    }

}