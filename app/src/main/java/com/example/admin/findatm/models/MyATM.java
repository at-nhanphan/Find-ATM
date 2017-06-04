package com.example.admin.findatm.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * MyATM class model
 * Created by naunem on 19/04/2017.
 */

public class MyATM implements Parcelable {
    @SerializedName("madiadiem")
    private String maDiaDiem;
    @SerializedName("tendiadiem")
    private String tenDiaDiem;
    @SerializedName("diachi")
    private String diaChi;
    @SerializedName("maquan")
    private String maQuan;
    @SerializedName("manganhang")
    private String maNganHang;
    @SerializedName("lat")
    private String lat;
    @SerializedName("lng")
    private String lng;
    private boolean favorite;
    private int id;

    public MyATM(String maDiaDiem, String tenDiaDiem, String diaChi, String maQuan, String maNganHang, String lat, String lng, int id) {
        this.maDiaDiem = maDiaDiem;
        this.tenDiaDiem = tenDiaDiem;
        this.diaChi = diaChi;
        this.maQuan = maQuan;
        this.maNganHang = maNganHang;
        this.lat = lat;
        this.lng = lng;
        this.id = id;
    }

    public MyATM() {

    }

    public MyATM(int id, String maDiaDiem, String tenDiaDiem, String diaChi, String maQuan, String maNganHang, String lat, String lng, boolean favorite) {
        this.id = id;
        this.maDiaDiem = maDiaDiem;
        this.tenDiaDiem = tenDiaDiem;
        this.diaChi = diaChi;
        this.maQuan = maQuan;
        this.maNganHang = maNganHang;
        this.lat = lat;
        this.lng = lng;
        this.favorite = favorite;
    }

    protected MyATM(Parcel in) {
        maDiaDiem = in.readString();
        tenDiaDiem = in.readString();
        diaChi = in.readString();
        maQuan = in.readString();
        maNganHang = in.readString();
        lat = in.readString();
        lng = in.readString();
        favorite = in.readByte() != 0;
        id = in.readInt();
    }

    public static final Creator<MyATM> CREATOR = new Creator<MyATM>() {
        @Override
        public MyATM createFromParcel(Parcel in) {
            return new MyATM(in);
        }

        @Override
        public MyATM[] newArray(int size) {
            return new MyATM[size];
        }
    };

    public String getMaDiaDiem() {
        return maDiaDiem;
    }

    public void setMaDiaDiem(String maDiaDiem) {
        this.maDiaDiem = maDiaDiem;
    }

    public void setTenDiaDiem(String tenDiaDiem) {
        this.tenDiaDiem = tenDiaDiem;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public String getMaQuan() {
        return maQuan;
    }

    public void setMaQuan(String maQuan) {
        this.maQuan = maQuan;
    }

    public String getMaNganHang() {
        return maNganHang;
    }

    public void setMaNganHang(String maNganHang) {
        this.maNganHang = maNganHang;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    @SuppressWarnings("SameReturnValue")
    public static Creator<MyATM> getCREATOR() {
        return CREATOR;
    }

    public String getTenDiaDiem() {
        return tenDiaDiem;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public String getLat() {
        return lat;
    }

    public String getLng() {
        return lng;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(maDiaDiem);
        dest.writeString(tenDiaDiem);
        dest.writeString(diaChi);
        dest.writeString(maQuan);
        dest.writeString(maNganHang);
        dest.writeString(lat);
        dest.writeString(lng);
        dest.writeByte((byte) (favorite ? 1 : 0));
        dest.writeInt(id);
    }
}
