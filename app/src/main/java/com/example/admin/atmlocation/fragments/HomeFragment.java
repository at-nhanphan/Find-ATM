package com.example.admin.atmlocation.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.atmlocation.R;
import com.example.admin.atmlocation.activities.DetailActivity_;
import com.example.admin.atmlocation.activities.MainActivity_;
import com.example.admin.atmlocation.adapters.ATMListAdapter;
import com.example.admin.atmlocation.databases.MyDatabase;
import com.example.admin.atmlocation.interfaces.CallBack;
import com.example.admin.atmlocation.interfaces.MyOnClickFavoriteListener;
import com.example.admin.atmlocation.interfaces.MyOnClickListener;
import com.example.admin.atmlocation.interfaces.OnQueryTextChange;
import com.example.admin.atmlocation.models.MyATM;
import com.example.admin.atmlocation.models.googleDirections.MyLocation;
import com.example.admin.atmlocation.services.ATMServiceImpl;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

import dmax.dialog.SpotsDialog;

import static android.content.Context.LOCATION_SERVICE;

/**
 * HomeFragment class
 * Created by naunem on 24/03/2017.
 */
@EFragment(R.layout.fragment_home)
public class HomeFragment extends Fragment implements MyOnClickListener, OnQueryTextChange, MyOnClickFavoriteListener {

    private static final int REQUEST_CODE = 1;
    @ViewById(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @ViewById(R.id.tvReload)
    TextView mTvReload;
    private ATMListAdapter mAdapter;
    private ArrayList<MyATM> mAtms;
    private SpotsDialog mDialog;
    private MyDatabase mMyDatabase;
    private ATMServiceImpl mAtmServiceImpl;
    private double mLat;
    private double mLng;
    private boolean mCheck;

    @AfterViews
    void init() {
        LinearLayoutManager ln = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(ln);

        mMyDatabase = new MyDatabase(getContext());
        mDialog = new SpotsDialog(getContext(), R.style.CustomDialog);
        ((MainActivity_) getContext()).setOnQueryTextChangeHome(this);

        mAtms = new ArrayList<>();
        mAdapter = new ATMListAdapter(getContext(), mAtms, this);
        new MyAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        mAtmServiceImpl = new ATMServiceImpl(getContext());
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(final Location location) {
                //your code here
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        checkLocationEnabled(getContext());
        LocationManager locationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 5000, locationListener);
        Location locations = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (locations != null) {
            // 16.063487, 108.223178
            // locations.getLatitude(), locations.getLongitude()
            mLat = locations.getLatitude();
            mLng = locations.getLongitude();
            getDataResponse(mAtmServiceImpl, mLat, mLng, 2);
        } else {
            Log.e("location null", "onCreateView: ");
        }

        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setMyOnClickFavoriteListener(this);
    }

    public void getDataResponse(ATMServiceImpl atmServiceImpl, double lat, double lng, int radius) {
        atmServiceImpl.getATM(lat, lng, radius, new CallBack<ArrayList<MyATM>>() {
            @Override
            public void next(ArrayList<MyATM> myATMs) {
                if (myATMs != null) {
                    mAtms.clear();
                    mAtms.addAll(myATMs);
                    for (int i = 0; i < mAtms.size(); i++) {
                        for (int j = 0; j < mMyDatabase.getAll().size(); j++) {
                            if (mAtms.get(i).getMaDiaDiem().equals(mMyDatabase.getAll().get(j).getMaDiaDiem())) {
                                mAtms.get(i).setFavorite(true);
                            }
                        }
                    }
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    public void checkLocationEnabled(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;
        try {
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ignored) {
            Log.e("ddd", "checkLocationEnabled: ", ignored);
        }

        if (!gps_enabled && !network_enabled) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
            dialog.setCancelable(false);
            dialog.setMessage(getContext().getResources().getString(R.string.gps_network_not_enabled));
            dialog.setPositiveButton(getResources().getString(R.string.positiveButton), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    Intent myIntent = new Intent(Settings.ACTION_SETTINGS);
                    getContext().startActivity(myIntent);
                    //get gps
                }
            });
            dialog.setNegativeButton(getResources().getString(R.string.cancelButton), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub

                }
            });
            dialog.show();
        }
    }

    @Override
    public void onClick(int position) {
        MyLocation myLocation = new MyLocation(Double.parseDouble(mAdapter.getResultFilter().get(position).getLat()),
                Double.parseDouble(mAdapter.getResultFilter().get(position).getLng()));
        DetailActivity_.intent(this)
                .mAtm(mAdapter.getResultFilter().get(position))
                .mMyLocation(myLocation)
                .startForResult(REQUEST_CODE);
    }

    @OnActivityResult(REQUEST_CODE)
    void onResult(int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            boolean isFavorite = data.getBooleanExtra("isFavorite", false);
            getDataResponse(mAtmServiceImpl, mLat, mLng, 2);
        }
    }

    @Override
    public void onTextChange(String newText) {
        if (mAdapter != null) {
            mAdapter.getFilter().filter(newText);
        }
    }

    @Click(R.id.tvReload)
    void clickReload() {
        init();
        mTvReload.setVisibility(View.GONE);
    }

    @Override
    public void onClickFavorite(int position) {
        MyATM myATM = mAtms.get(position);
        ArrayList<MyATM> lists = mMyDatabase.getAll();
        if (myATM.isFavorite()) {
            int count = 0;
            if (lists.size() > 0) {
                for (int i = 0; i < lists.size(); i++) {
                    if (myATM.getMaDiaDiem().equals(lists.get(i).getMaDiaDiem())) {
                        Toast.makeText(getContext(), "Item is favorited", Toast.LENGTH_SHORT).show();
                    } else {
                        count++;
                    }
                }
            }
            if (count == lists.size()) {
                mMyDatabase.insertATM(myATM);
            }
        } else {
            if (lists.size() > 0) {
                for (int i = 0; i < lists.size(); i++) {
                    if (myATM.getMaDiaDiem().equals(lists.get(i).getMaDiaDiem())) {
                        mMyDatabase.deleteATM(Integer.parseInt(lists.get(i).getMaDiaDiem()));
                    }
                }
            }
        }
    }

    private class MyAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog.show();
            mTvReload.setVisibility(View.GONE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            int count = 0;
            while (mAtms.size() <= 0) {
                count++;
                try {
                    Thread.sleep(1000);
                    mCheck = false;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (count >= 3) {
                    mCheck = true;
                    break;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mDialog.dismiss();
            if (mCheck) {
                mTvReload.setVisibility(View.VISIBLE);
            } else {
                mTvReload.setVisibility(View.GONE);
            }
        }
    }
}
