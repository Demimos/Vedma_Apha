package com.Vedma.Roleplay.Vedma_Alpha.ACTIVITY;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.Vedma.Roleplay.Vedma_Alpha.ADAPTER.CharacterPage;
import com.Vedma.Roleplay.Vedma_Alpha.POJO.FragmentView;

import java.util.List;

class SectionsPagerAdapter extends FragmentPagerAdapter {

    private List<FragmentView> views;
    SectionsPagerAdapter(FragmentManager fm, List<FragmentView> views) {
        super(fm);
        this.views=views;
    }

    @Override
    public Fragment getItem(int i) {
        return CharacterPage.newInstance(views.get(i).Name, views.get(i).getProperties());
    }
    @Override
    public CharSequence getPageTitle(int position) {
        return String.valueOf(views.get(position).Name);
    }
    @Override
    public int getCount() {
        return views.size();
    }
}
