package com.halfplatepoha.frnds.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.util.SparseArray;

/**
 * Created by surajkumarsau on 07/09/16.
 */
public class OpenSansTypefaceManager {

    public static final int OPEN_SANS_REGULAR = 1;
    public static final int OPEN_SANS_BOLD = 2;

    private final static SparseArray<Typeface> mTypefaces = new SparseArray<>(20);

    public static Typeface obtainTypeface(Context context, int typefaceValue) throws IllegalArgumentException {
        Typeface typeface = mTypefaces.get(typefaceValue);
        if (typeface == null) {
            typeface = createTypeface(context, typefaceValue);
            mTypefaces.put(typefaceValue, typeface);
        }
        return typeface;
    }

    private static Typeface createTypeface(Context context, int typefaceValue) throws IllegalArgumentException {
        Typeface typeface;
        switch (typefaceValue) {


            case OPEN_SANS_REGULAR:
                typeface = Typeface.createFromAsset(context.getAssets(), "fonts/OpenSans-Regular.ttf");
                break;

            case OPEN_SANS_BOLD:
                typeface = Typeface.createFromAsset(context.getAssets(), "fonts/OpenSans-Bold.ttf");
                break;


            default:
                throw new IllegalArgumentException("Unknown `typeface` attribute value " + typefaceValue);
        }
        return typeface;
    }

}
