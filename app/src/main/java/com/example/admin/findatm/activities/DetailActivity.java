package com.example.admin.findatm.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.findatm.R;
import com.example.admin.findatm.databases.MyDatabase;
import com.example.admin.findatm.models.MyATM;
import com.example.admin.findatm.models.googleDirections.MyLocation;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

/**
 * DetailActivity class
 * Created by naunem on 25/03/2017.
 */

@EActivity(R.layout.activity_detail)
public class DetailActivity extends AppCompatActivity {

    @ViewById(R.id.tvName)
    TextView mTvName;
    @ViewById(R.id.tvId)
    TextView mTvId;
    @ViewById(R.id.tvBank)
    TextView mTvBank;
    @ViewById(R.id.tvAddress)
    TextView mTvAddress;
    @ViewById(R.id.tvIdDistrict)
    TextView mTvDistrict;
    @ViewById(R.id.tvIdBank)
    TextView mTvIdBank;
    @ViewById(R.id.tvLatLng)
    TextView mTvLatLng;
    @ViewById(R.id.imgFavorite)
    ImageView mImgFavorite;
    @Extra
    MyATM mAtm;
    @Extra
    MyLocation mMyLocation;
    private MyDatabase mMyDatabase;

    @AfterViews
    void init() {
        mMyDatabase = new MyDatabase(this);
        mTvName.setText(mAtm.getAddressName());
        mTvId.setText(mAtm.getAddressId());
        mTvBank.setText(mAtm.getAddressName());
        mTvAddress.setText(mAtm.getAddress());
        mTvDistrict.setText(mAtm.getDistrictId());
        mTvIdBank.setText(mAtm.getBankId());
        mTvLatLng.setText(mAtm.getLat() + " , " + mAtm.getLng());
        mImgFavorite.setSelected(mAtm.isFavorite());
    }

    @Click(R.id.btnShowMap)
    void onClickShowMap() {
        MapsActivity_.intent(this).mAtm(mAtm).mAddressAtm(mMyLocation).start();
        finish();
    }

    @Click(R.id.imgBack)
    void clickBack() {
        setResult(RESULT_OK, new Intent());
        finish();
    }

    @Click(R.id.imgFavorite)
    void clickFavorite() {
        mImgFavorite.setSelected(!mAtm.isFavorite());
        mAtm.setFavorite(!mAtm.isFavorite());
        if (mImgFavorite.isSelected()) {
            mMyDatabase.insertATM(mAtm);
            Toast.makeText(this, R.string.favorite_item, Toast.LENGTH_SHORT).show();
        } else {
            mMyDatabase.deleteATM(Integer.parseInt(mAtm.getAddressId()));
            Toast.makeText(this, R.string.unfavorite_item, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        clickBack();
    }
}
