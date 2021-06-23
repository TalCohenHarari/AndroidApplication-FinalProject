package com.example.finalproject.model;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.example.finalproject.MyApplication;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;

import java.util.HashMap;
import java.util.Map;



@Entity
public class Queue{
    @PrimaryKey
    @NonNull
    public String id;
    public String userId;
    public String barbershopId;
    public String barbershopName;
    public String queueDate;
    public String queueTime;
    public String queueAddress;
    public Long lastUpdated;
    public boolean isQueueAvailable;
    public boolean isDeleted;



    final static String ID = "id";
    final static String USER_ID = "userId";
    final static String BARBERSHOP_ID = "barbershopId";
    final static String BARBERSHOP_NAME = "barbershopName";
    final static String QUEUE_DATE = "queueDate";
    final static String QUEUE_TIME = "queueTime";
    final static String QUEUE_ADDRESS = "queueAddress";
    final static String LAST_UPDATED = "lastUpdated";
    final static String IS_QUEUE_AVAILABLE = "isQueueAvailable";
    final static String IS_DELETED = "isDeleted";

    public Queue(){}
    @Ignore
    public Queue( String id, String userId, String barbershopId, String barbershopName, String queueDate,
                  String queueTime, String queueAddress, boolean isQueueAvailable, boolean isDeleted){
        this.id=id;
        this.userId=userId;
        this.barbershopId=barbershopId;
        this.barbershopName=barbershopName;
        this.queueDate=queueDate;
        this.queueTime=queueTime;
        this.queueAddress=queueAddress;
        this.isQueueAvailable=isQueueAvailable;
        this.isDeleted=isDeleted;
    }


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

    public void setLastUpdated(Long lastUpdated) {
        this.lastUpdated = lastUpdated;
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

    public Long getLastUpdated() {
        return lastUpdated;
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
        json.put(LAST_UPDATED, FieldValue.serverTimestamp());
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

        Timestamp ts = (Timestamp) json.get(LAST_UPDATED);
        if(ts!=null) {
            userQueue.lastUpdated = new Long(ts.getSeconds());
        }
        else {
            userQueue.lastUpdated = new Long(0);
        }

        return userQueue;
    }

    private static final String QUEUE_LAST_UPDATE = "QueueLastUpdate";

    static public void setLocalLastUpdateTime(Long ts){
        //Shared preference, saving the ts on the disk (like the db):
        SharedPreferences.Editor editor = MyApplication.context.getSharedPreferences("TAG", Context.MODE_PRIVATE).edit();
        editor.putLong(QUEUE_LAST_UPDATE,ts);
        editor.commit();
    }
    static public Long getLocalLastUpdateTime(){
        //Shared preference, saving the ts in app:
        return MyApplication.context.getSharedPreferences("TAG", Context.MODE_PRIVATE)
                .getLong(QUEUE_LAST_UPDATE,0);
    }
}
