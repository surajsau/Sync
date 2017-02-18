package com.halfplatepoha.frnds.ui;

import android.app.Fragment;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.bumptech.glide.request.target.Target;

import java.io.File;

public class GlideImageView extends ImageView {

    private Context mContext;
    private @DrawableRes
    int mPlaceHolderDrawable;
    private @DrawableRes int mErrorDrawable;

    private int mMaxWidth = Target.SIZE_ORIGINAL;
    private int mMaxHeight = Target.SIZE_ORIGINAL;

    private ScaleErrorImageViewTarget mTarget = ScaleErrorImageViewTarget.getInstance(this);

    public GlideImageView(Context context) {
        super(context);
    }

    public GlideImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GlideImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setImageUrl(Fragment fragment, String imageUrl) {
        Glide.with(fragment).fromString()
                .load(imageUrl)
                .placeholder(mPlaceHolderDrawable)
                .error(mErrorDrawable)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .dontAnimate()
                .into(mTarget);
    }

    public void setImageUrl(android.support.v4.app.Fragment fragment, String imageUrl) {
        Glide.with(fragment).fromString()
                .load(imageUrl)
                .placeholder(mPlaceHolderDrawable)
                .error(mErrorDrawable)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .dontAnimate()
                .into(mTarget);
    }

    public void setImageUrl(Context context, String imageUrl) {
        Glide.with(context).fromString()
                .load(imageUrl)
                .placeholder(mPlaceHolderDrawable)
                .error(mErrorDrawable)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .dontAnimate()
                .into(mTarget);
    }

    public void setDefaultImageResId(@DrawableRes int placeHolderDrawable) {
        mPlaceHolderDrawable = placeHolderDrawable;
    }

    public void setErrorImageResId(@DrawableRes int errorDrawable) {
        mErrorDrawable = errorDrawable;
    }


    //when showing placeholder and error drawable set scale type to centre inside to avoid up scaling
    //else set scale type to as defined in xml for the image view.
    public static class ScaleErrorImageViewTarget extends GlideDrawableImageViewTarget {
        private final ScaleType mDefaultScaleType;
        ScaleType mErrorScaleType;


        public ScaleErrorImageViewTarget(ImageView view, ScaleType errorScaleType) {
            super(view);
            mDefaultScaleType = view.getScaleType();
            mErrorScaleType = errorScaleType;
        }

        @Override
        public void onLoadStarted(Drawable placeholder) {
            ImageView imageView = getView();
            imageView.setScaleType(mErrorScaleType);
            super.onLoadStarted(placeholder);
        }

        @Override
        public void onLoadCleared(Drawable placeholder) {
            ImageView imageView = getView();
            imageView.setScaleType(mErrorScaleType);
            super.onLoadCleared(placeholder);
        }

        @Override
        protected void setResource(GlideDrawable resource) {
            ImageView imageView = getView();
            imageView.setScaleType(mDefaultScaleType);
            super.setResource(resource);
        }

        @Override
        public void setDrawable(Drawable drawable) {
            ImageView imageView = getView();
            imageView.setScaleType(mDefaultScaleType);
            super.setDrawable(drawable);
        }

        @Override
        public void onLoadFailed(Exception e, Drawable errorDrawable) {
            ImageView imageView = getView();
            imageView.setScaleType(mErrorScaleType);
            super.onLoadFailed(e, errorDrawable);
        }


        @NonNull
        public static ScaleErrorImageViewTarget getInstance(ImageView imageView) {
            return new ScaleErrorImageViewTarget(imageView, ScaleType.CENTER_INSIDE);
        }
    }

}
