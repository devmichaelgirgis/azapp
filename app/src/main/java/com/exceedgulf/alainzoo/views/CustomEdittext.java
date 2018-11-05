package com.exceedgulf.alainzoo.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;

import com.exceedgulf.alainzoo.R;


public class CustomEdittext extends AppCompatEditText {

    public CustomEdittext(Context context) {
        super(context);
        init(context, null);
    }

    public CustomEdittext(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CustomEdittext(Context context, AttributeSet attrs, int defStyleAttr) {
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