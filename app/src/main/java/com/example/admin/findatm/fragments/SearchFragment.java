package com.example.admin.findatm.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.findatm.R;
import com.example.admin.findatm.activities.DetailActivity_;
import com.example.admin.findatm.activities.ListBankDistrictActivity_;
import com.example.admin.findatm.activities.MapsActivity_;
import com.example.admin.findatm.adapters.ATMListAdapter;
import com.example.admin.findatm.databases.MyDatabase;
import com.example.admin.findatm.interfaces.CallBack;
import com.example.admin.findatm.interfaces.MyOnClickFavoriteListener;
import com.example.admin.findatm.interfaces.MyOnClickListener;
import com.example.admin.findatm.models.MyATM;
import com.example.admin.findatm.models.googleDirections.MyLocation;
import com.example.admin.findatm.services.ATMServiceImpl;
import com.example.admin.findatm.utils.NetworkConnection;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;

import java.util.ArrayList;

/**
 * SearchActivity class
 * Created by naunem on 05/04/2017.
 */
@EFragment(R.layout.fragment_search)
public class SearchFragment extends Fragment implements MyOnClickListener, MyOnClickFavoriteListener {

    private static final int REQUEST_CODE_BANK = 1;
    private static final int REQUEST_CODE_AREA = 2;
    private static final int REQUEST_CODE_SEARCH = 555;
    @ViewById(R.id.toolbar)
    Toolbar mToolbar;
    @ViewById(R.id.tvBank)
    TextView mTvBank;
    @ViewById(R.id.tvArea)
    TextView mTvArea;
    @ViewById(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @ViewById(R.id.tvMessage)
    TextView mTvMessage;
    @ViewById(R.id.progressBar)
    ProgressBar mProgressBar;
    @ViewById(R.id.imgWifi)
    ImageView mImgWifi;
    @StringRes(R.string.bank)
    String mStBank;
    @StringRes(R.string.district)
    String mStDistrict;

    private int mPositionBank = -1;
    private int mPositionDistrict = -1;
    private ATMListAdapter mAdapter;
    private ArrayList<MyATM> mAtms;
    private MyDatabase mMyDatabase;
    private boolean mCheck;
    private Animation mAnimation;

    @AfterViews
    void init() {
        mProgressBar.setVisibility(View.GONE);
        mTvMessage.setVisibility(View.INVISIBLE);
        mImgWifi.setVisibility(View.GONE);
        mMyDatabase = new MyDatabase(getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.blink);
    }

    @Click({R.id.tvBank, R.id.tvArea})
    void clickChoose(View v) {
        switch (v.getId()) {
            case R.id.tvBank:
                ListBankDistrictActivity_.intent(this)
                        .mCode(REQUEST_CODE_BANK)
                        .mPositionBank(mPositionBank)
                        .startForResult(REQUEST_CODE_BANK);
                break;
            case R.id.tvArea:
                ListBankDistrictActivity_.intent(this)
                        .mCode(REQUEST_CODE_AREA)
                        .mPositionDistrict(mPositionDistrict)
                        .startForResult(REQUEST_CODE_AREA);
                break;
        }
    }

    @OnActivityResult(REQUEST_CODE_BANK)
    void onResultBank(int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            String resultBank = data.getStringExtra(ListBankDistrictActivity_.RESULT_BANK);
            int position = data.getIntExtra(ListBankDistrictActivity_.POSITION_BANK, -1);
            if (position != -1) {
                mTvBank.setText(resultBank);
                mPositionBank = position;
            }
        }
    }

    @OnActivityResult(REQUEST_CODE_AREA)
    void onResultDistrict(int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            String resultDistrict = data.getStringExtra(ListBankDistrictActivity_.RESULT_DISTRICT);
            int position = data.getIntExtra(ListBankDistrictActivity_.POSITION_DISTRICT, -1);
            if (position != -1) {
                mTvArea.setText(resultDistrict);
                mPositionDistrict = position;
            }
        }
    }

    @Click(R.id.tvSearch)
    void clickSearch() {
        if (mTvBank.getText().equals(mStBank) || mTvArea.getText().equals(mStDistrict)) {
            Toast.makeText(getContext(), R.string.validate, Toast.LENGTH_SHORT).show();
        } else if (NetworkConnection.isInternetConnected(getContext())) {
            mImgWifi.setVisibility(View.GONE);
            mTvMessage.setVisibility(View.INVISIBLE);
            mAtms = new ArrayList<>();
            loadData();
            new MyAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            mAdapter = new ATMListAdapter(mAtms, this);
            mRecyclerView.setAdapter(mAdapter);
            mAdapter.setMyOnClickFavoriteListener(this);
        } else {
            mImgWifi.setVisibility(View.VISIBLE);
            mImgWifi.startAnimation(mAnimation);
        }
    }

    @Click(R.id.imgWifi)
    void clickImgWifi() {
        mImgWifi.startAnimation(mAnimation);
    }

    @Override
    public void onClick(int position) {
        MyLocation myLocation = new MyLocation(Double.parseDouble(mAdapter.getResultFilter().get(position).getLat()),
                Double.parseDouble(mAdapter.getResultFilter().get(position).getLng()));
        MapsActivity_.intent(this)
                .mAddressAtm(myLocation)
                .mAtm(mAdapter.getResultFilter().get(position))
                .start();
    }

    @Override
    public void onLongClick(int position) {
        MyLocation myLocation = new MyLocation(Double.parseDouble(mAdapter.getResultFilter().get(position).getLat()),
                Double.parseDouble(mAdapter.getResultFilter().get(position).getLng()));
        DetailActivity_.intent(this)
                .mAtm(mAdapter.getResultFilter().get(position))
                .mMyLocation(myLocation)
                .startForResult(REQUEST_CODE_SEARCH);
    }

    @OnActivityResult(REQUEST_CODE_SEARCH)
    void onResultDetail(int resultCode) {
        if (resultCode == Activity.RESULT_OK) {
            for (int i = 0; i < mAtms.size(); i++) {
                for (int j = 0; j < mMyDatabase.getAll().size(); j++) {
                    if (mAtms.get(i).getAddressId().equals(mMyDatabase.getAll().get(j).getAddressId())) {
                        mAtms.get(i).setFavorite(true);
                    }
                }
            }
            mAdapter.notifyDataSetChanged();
        }
    }

    private void loadData() {
        ATMServiceImpl atmServiceImpl = new ATMServiceImpl(getContext());
        atmServiceImpl.getAtmSearch(String.valueOf(mTvBank.getText()), String.valueOf(mTvArea.getText()), new CallBack<ArrayList<MyATM>>() {
            @Override
            public void next(ArrayList<MyATM> myATMs) {
                if (myATMs != null) {
                    mAtms.clear();
                    mAtms.addAll(myATMs);
                    for (int i = 0; i < mAtms.size(); i++) {
                        for (int j = 0; j < mMyDatabase.getAll().size(); j++) {
                            if (mAtms.get(i).getAddressId().equals(mMyDatabase.getAll().get(j).getAddressId())) {
                                mAtms.get(i).setFavorite(true);
                            }
                        }
                    }
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void onClickFavorite(int position) {
        MyATM myATM = mAtms.get(position);
        if (myATM.isFavorite()) {
            mMyDatabase.insertATM(myATM);
            Toast.makeText(getContext(), R.string.favorite_item, Toast.LENGTH_SHORT).show();
        } else {
            mMyDatabase.deleteATM(Integer.parseInt(myATM.getAddressId()));
            Toast.makeText(getContext(), R.string.unfavorite_item, Toast.LENGTH_SHORT).show();
        }
    }

    private class MyAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBar.setVisibility(View.VISIBLE);
            mTvMessage.setVisibility(View.INVISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            int count = 0;
            while (mAtms.size() <= 0) {
                count++;
                SystemClock.sleep(1000);
                mCheck = false;
                if (count >= 5) {
                    mCheck = true;
                    break;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                mProgressBar.setVisibility(View.GONE);
                if (mCheck) {
                    mTvMessage.setVisibility(View.VISIBLE);
                } else {
                    mTvMessage.setVisibility(View.INVISIBLE);
                }
            } catch (NullPointerException ignored) {
            }
        }
    }
}

