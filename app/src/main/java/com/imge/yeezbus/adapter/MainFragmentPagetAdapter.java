package com.imge.yeezbus.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import com.imge.yeezbus.fragment.RouteListFragment;
import java.util.List;

public class MainFragmentPagetAdapter extends FragmentStatePagerAdapter {
    private List<List<String[]>> list;
    private RouteListFragment fragment;

    public MainFragmentPagetAdapter(FragmentManager fm, List<List<String[]>> list) {
        super(fm);
        this.list = list;
    }

    @Override
    public Fragment getItem(int position) {
        fragment = new RouteListFragment();
        fragment.setData(list.get(position), position);
        return fragment;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "去程";
            case 1:
                return "返程";
            default:
                return null;
        }
    }

}
