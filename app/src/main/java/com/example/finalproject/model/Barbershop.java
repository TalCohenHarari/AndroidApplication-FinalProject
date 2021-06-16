package com.example.finalproject.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.util.HashMap;
import java.util.Map;


interface JsonBarbershopModel{
    Map<String,Object> toJson();
}

@Entity
public class Barbershop implements JsonBarbershopModel{
    @PrimaryKey
    @NonNull
    public String owner;
    public String name;
    public String address;
    public String phone;
    public String avatar;
    public boolean isAvailable;

    final static String OWNER = "owner";
    final static String NAME = "name";
    final static String ADDRESS = "address";
    final static String PHONE = "phone";
    final static String AVATAR = "avatar";
    final static String IS_AVAILABLE = "isAvailable";

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

    public void setAvailable(boolean available) {
        isAvailable = available;
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

    public boolean isAvailable() {
        return isAvailable;
    }

    public Map<String,Object> toJson(){
        Map<String, Object> json = new HashMap<>();
        json.put(OWNER, owner);
        json.put(NAME, name);
        json.put(ADDRESS, address);
        json.put(PHONE, phone);
        json.put(AVATAR, avatar);
        json.put(IS_AVAILABLE, isAvailable);

        return json;
    }

    static public Barbershop create(Map<String,Object> json) {
        Barbershop barbershop = new Barbershop();
        barbershop.owner = (String)json.get(OWNER);
        barbershop.name = (String)json.get(NAME);
        barbershop.address = (String)json.get(ADDRESS);
        barbershop.phone = (String)json.get(PHONE);
        barbershop.avatar = (String)json.get(AVATAR);
        barbershop.owner = (String)json.get(OWNER);

        return barbershop;
    }
}
