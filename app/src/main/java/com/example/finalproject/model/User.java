package com.example.finalproject.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.HashMap;
import java.util.Map;


interface JsonUsersModel{
    Map<String,Object> toJson();
}

@Entity
public class User implements JsonUsersModel{
    @PrimaryKey
    @NonNull
    public String id;
    public String name;
    public String password;
    public String email;
    public String phone;
    public String avatar;
    public boolean isBarbershop;
    public boolean isAvailable;

    final static String ID = "id";
    final static String NAME = "name";
    final static String PASSWORD = "password";
    final static String EMAIL = "email";
    final static String PHONE = "phone";
    final static String AVATAR = "avatar";
    final static String IS_BARBERSHOP = "isBarbershop";
    final static String IS_AVAILABLE = "isAvailable";


    //Setters:
    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setBarbershop(boolean barbershop) {
        isBarbershop = barbershop;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    //Getters:
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getAvatar() {
        return avatar;
    }

    public boolean isBarbershop() {
        return isBarbershop;
    }

    public boolean isAvailable() {
        return isAvailable;
    }


    public Map<String,Object> toJson(){
        Map<String, Object> json = new HashMap<>();
        json.put(ID, id);
        json.put(NAME, name);
        json.put(PASSWORD, password);
        json.put(EMAIL, email);
        json.put(PHONE, phone);
        json.put(AVATAR, avatar);
        json.put(IS_BARBERSHOP,isBarbershop);
        json.put(IS_AVAILABLE,isAvailable);

        return json;
    }
    static public User create(Map<String,Object> json) {
        User user = new User();
        user.id = (String)json.get(ID);
        user.name = (String)json.get(NAME);
        user.password = (String)json.get(PASSWORD);
        user.email = (String)json.get(EMAIL);
        user.phone = (String)json.get(PHONE);
        user.avatar = (String)json.get(AVATAR);
        user.isBarbershop = (boolean)json.get(IS_BARBERSHOP);
        user.isAvailable = (boolean)json.get(IS_AVAILABLE);

        return user;
    }
}
