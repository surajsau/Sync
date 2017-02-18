package com.halfplatepoha.frnds.home.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;

import com.halfplatepoha.frnds.R;
import com.halfplatepoha.frnds.home.IFrndsConstants;
import com.halfplatepoha.frnds.home.fragment.FriendsListFragment;
import com.halfplatepoha.frnds.home.fragment.SoundCloudSearchFragment;
import com.halfplatepoha.frnds.home.fragment.TracksListFragment;

/**
 * Created by surajkumarsau on 03/10/16.
 */
public class TabPagerAdapter extends FragmentPagerAdapter implements TabHelper.IconPagerAdapter{

    private FriendsListFragment mFriendsListFragment;
    private SoundCloudSearchFragment mSoundCloudSearchFragment;
    private TracksListFragment mTracksListFragment;

    private Context mContext;

    public TabPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                if(mFriendsListFragment == null)
                    mFriendsListFragment = new FriendsListFragment();
                return mFriendsListFragment;

            case 1:
                if(mSoundCloudSearchFragment == null)
                    mSoundCloudSearchFragment = new SoundCloudSearchFragment();
                return mSoundCloudSearchFragment;

            case 2:
                if(mTracksListFragment == null)
                    mTracksListFragment = new TracksListFragment();
                return mTracksListFragment;
        }
        return null;
    }

    @Override
    public int getCount() {
        return IFrndsConstants.tabDrawables.length;
    }

    @Override
    public int getPageTitleIconRes(int position) {
        return IFrndsConstants.tabDrawables[position];
    }

    @Nullable
    @Override
    public Drawable getPageTitleIconDrawable(int position) {
        return ContextCompat.getDrawable(mContext, IFrndsConstants.tabDrawables[position]);
    }

}
