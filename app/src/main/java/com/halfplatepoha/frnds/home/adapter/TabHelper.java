package com.halfplatepoha.frnds.home.adapter;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

/**
 * Created by surajkumarsau on 03/10/16.
 */
public class TabHelper {

    public interface IconPagerAdapter {
        @DrawableRes int getPageTitleIconRes(int position);
        @Nullable Drawable getPageTitleIconDrawable(int position);
    }

    public static void setupViewPager(TabLayout tabs, ViewPager pager) {
        pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabs));
        setTabsFromPagerAdapter(tabs, pager);
        tabs.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(pager));
    }

    public static void setTabsFromPagerAdapter(TabLayout tabs, ViewPager pager) {
        PagerAdapter adapter = pager.getAdapter();
        if(!(adapter instanceof IconPagerAdapter)) {
            tabs.setupWithViewPager(pager);
            return;
        }

        ColorStateList tabColors = tabs.getTabTextColors();
        for(int i=0; i<adapter.getCount(); i++) {
            Drawable icon = ((IconPagerAdapter)adapter).getPageTitleIconDrawable(i);
            if(icon == null) {
                int iconRes = ((IconPagerAdapter)adapter).getPageTitleIconRes(i);
                icon = ResourcesCompat.getDrawable(tabs.getResources(), iconRes, null);
            }

            icon = DrawableCompat.wrap(icon);
            DrawableCompat.setTintList(icon, tabColors);
            tabs.addTab(tabs.newTab().setIcon(icon).setContentDescription(adapter.getPageTitle(i)));
        }
    }
}
