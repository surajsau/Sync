package com.halfplatepoha.frnds.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.Button;

import com.halfplatepoha.frnds.R;

/**
 * Created by surajkumarsau on 07/09/16.
 */
public class OpenSansButton extends Button {

    public OpenSansButton(Context context) {
        super(context);
        initTypeface(context, null, 0);
        removeShadow();
    }

    public OpenSansButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initTypeface(context, attrs, 0);
        removeShadow();
    }

    public OpenSansButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initTypeface(context, attrs, defStyleAttr);
        removeShadow();
    }

    private void removeShadow(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            this.setStateListAnimator(null);
        }
    }

    public void initTypeface(Context context, AttributeSet attrs, int defStyleAttr) {
        if (isInEditMode()) {
            return;
        }

        int typefaceValue = OpenSansTypefaceManager.OPEN_SANS_REGULAR;
        if (attrs != null) {
            TypedArray values = context.obtainStyledAttributes(attrs,
                    R.styleable.open_sans_text_style, defStyleAttr, 0);
            if (values != null) {
                typefaceValue = values.getInt(
                        R.styleable.open_sans_text_style_typeface, typefaceValue);
                values.recycle();
            }
        }

        Typeface openSansTypeface = OpenSansTypefaceManager.obtainTypeface(context,
                typefaceValue);
        setTypeface(openSansTypeface);
    }
}
