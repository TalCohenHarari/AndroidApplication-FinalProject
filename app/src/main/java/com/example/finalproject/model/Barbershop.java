package com.example.finalproject.model;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.finalproject.MyApplication;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;

import java.util.HashMap;
import java.util.Map;



@Entity
public class Barbershop{
    @PrimaryKey
    @NonNull
    public String owner;
    public String name;
    public String address;
    public String phone;
    public String avatar;
    public Long lastUpdated;
    public boolean isDeleted;
    public double latitude;
    public double longitude;



    final static String OWNER = "owner";
    final static String NAME = "name";
    final static String ADDRESS = "address";
    final static String PHONE = "phone";
    final static String AVATAR = "avatar";
    final static String LAST_UPDATED = "lastUpdated";
    final static String IS_DELETED = "isDeleted";
    final static String LATITUDE = "latitude";
    final static String LONGITUDE = "longitude";

    //Setters:
    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setLastUpdated(Long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public void setDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }


    //Getters:
    public String getOwner() {
        return owner;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAvatar() {
        return avatar;
    }

    public Long getLastUpdated() {
        return lastUpdated;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public Map<String,Object> toJson(){
        Map<String, Object> json = new HashMap<>();
        json.put(OWNER, owner);
        json.put(NAME, name);
        json.put(ADDRESS, address);
        json.put(PHONE, phone);
        json.put(AVATAR, avatar);
        json.put(LAST_UPDATED, FieldValue.serverTimestamp());
        json.put(IS_DELETED, isDeleted);
        json.put(LATITUDE, latitude);
        json.put(LONGITUDE, longitude);

        return json;
    }

    static public Barbershop create(Map<String,Object> json) {
        Barbershop barbershop = new Barbershop();
        barbershop.owner = (String)json.get(OWNER);
        barbershop.name = (String)json.get(NAME);
        barbershop.address = (String)json.get(ADDRESS);
        barbershop.phone = (String)json.get(PHONE);
        barbershop.avatar = (String)json.get(AVATAR);
        barbershop.latitude = (double)json.get(LATITUDE);
        barbershop.longitude = (double)json.get(LONGITUDE);

        Timestamp ts = (Timestamp) json.get(LAST_UPDATED);
        if(ts!=null)
            barbershop.lastUpdated = new Long(ts.getSeconds());
        else
            barbershop.lastUpdated = new Long(0);

        barbershop.isDeleted = (boolean)json.get(IS_DELETED);

        return barbershop;
    }

    private static final String BARBERSHOP_LAST_UPDATE = "BarbershopLastUpdate";

    static public void setLocalLastUpdateTime(Long ts){
        //Shared preference, saving the ts on the disk (like the db):
        SharedPreferences.Editor editor = MyApplication.context.getSharedPreferences("TAG", Context.MODE_PRIVATE).edit();
        editor.putLong(BARBERSHOP_LAST_UPDATE,ts);
        editor.commit();
    }
    static public Long getLocalLastUpdateTime(){
        //Shared preference, saving the ts in app:
        return MyApplication.context.getSharedPreferences("TAG", Context.MODE_PRIVATE)
                .getLong(BARBERSHOP_LAST_UPDATE,0);
    }
}
