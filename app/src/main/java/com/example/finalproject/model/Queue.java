package com.example.finalproject.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.HashMap;
import java.util.Map;


interface JsonQueueModel {
    Map<String,Object> toJson();
}

@Entity
public class Queue implements JsonQueueModel{
    @PrimaryKey
    @NonNull
    public String id;
    public String userId;
    public String barbershopId;
    public String barbershopName;
    public String queueDate;
    public String queueTime;
    public String queueAddress;
    public boolean isQueueAvailable;
    public boolean isDeleted;



    final static String ID = "id";
    final static String USER_ID = "userId";
    final static String BARBERSHOP_ID = "barbershopId";
    final static String BARBERSHOP_NAME = "barbershopName";
    final static String QUEUE_DATE = "queueDate";
    final static String QUEUE_TIME = "queueTime";
    final static String QUEUE_ADDRESS = "queueAddress";
    final static String IS_QUEUE_AVAILABLE = "isQueueAvailable";
    final static String IS_DELETED = "isDeleted";


    public void setId(String id) {
        this.id = id;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setBarbershopId(String barbershopId) {
        this.barbershopId = barbershopId;
    }

    public void setBarbershopName(String barbershopName) {
        this.barbershopName = barbershopName;
    }

    public void setQueueDate(String queueDate) {
        this.queueDate = queueDate;
    }

    public void setQueueTime(String queueTime) {
        this.queueTime = queueTime;
    }

    public void setQueueAddress(String queueAddress) {
        this.queueAddress = queueAddress;
    }

    public void setQueueAvailable(boolean queueAvailable) {
        isQueueAvailable = queueAvailable;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getBarbershopId() {
        return barbershopId;
    }

    public String getBarbershopName() {
        return barbershopName;
    }

    public String getQueueDate() {
        return queueDate;
    }

    public String getQueueTime() {
        return queueTime;
    }

    public String getQueueAddress() {
        return queueAddress;
    }

    public boolean isQueueAvailable() {
        return isQueueAvailable;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public Map<String,Object> toJson(){
        Map<String, Object> json = new HashMap<>();
        json.put(ID, id);
        json.put(USER_ID, userId);
        json.put(BARBERSHOP_ID, barbershopId);
        json.put(BARBERSHOP_NAME, barbershopName);
        json.put(QUEUE_DATE, queueDate);
        json.put(QUEUE_TIME, queueTime);
        json.put(QUEUE_ADDRESS, queueAddress);
        json.put(IS_QUEUE_AVAILABLE, isQueueAvailable);
        json.put(IS_DELETED, isDeleted);
        return json;
    }

    static public Queue create(Map<String,Object> json) {
        Queue userQueue = new Queue();
        userQueue.id = (String)json.get(ID);
        userQueue.userId = (String)json.get(USER_ID);
        userQueue.barbershopId = (String)json.get(BARBERSHOP_ID);
        userQueue.barbershopName = (String)json.get(BARBERSHOP_NAME);
        userQueue.queueDate = (String)json.get(QUEUE_DATE);
        userQueue.queueTime = (String)json.get(QUEUE_TIME);
        userQueue.queueAddress = (String)json.get(QUEUE_ADDRESS);
        userQueue.isQueueAvailable = (boolean) json.get(IS_QUEUE_AVAILABLE);
        userQueue.isDeleted = (boolean) json.get(IS_DELETED);
        return userQueue;
    }
}
