package com.example.admin.findatm.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.admin.findatm.fragments.ItemStartEndFragment_;
import com.example.admin.findatm.fragments.ItemStepFragment_;
import com.example.admin.findatm.models.MyATM;
import com.example.admin.findatm.models.googleDirections.Leg;

import java.util.ArrayList;
import java.util.List;

/**
 * StepAdapter class
 * Created by naunem on 13/04/2017.
 */

public class StepAdapter extends FragmentStatePagerAdapter {

    private List<Leg> mLegs = new ArrayList<>();
    private final MyATM mMyATM;

    public StepAdapter(FragmentManager fm, List<Leg> legs, MyATM myATM) {
        super(fm);
        this.mLegs = legs;
        this.mMyATM = myATM;
    }

    @Override
    public Fragment getItem(int position) {
        if (position <= 1 || position >= mLegs.get(0).getSteps().size() + 1) {
            return ItemStartEndFragment_.builder().build().newInstance(mLegs.get(0), mMyATM, position);
        } else {
            return ItemStepFragment_.builder().build().newInstance(mLegs.get(0).getSteps().get(position - 1));
        }
    }

    @Override
    public int getCount() {
        return mLegs.get(0).getSteps().size() + 3;
    }
}
