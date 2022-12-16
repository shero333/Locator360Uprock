package com.hammad.findmyfamily.HomeScreen.FragmentLocation.Chat.DB;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.hammad.findmyfamily.Util.Constants;

import java.io.Serializable;

@Entity(tableName = Constants.TABLE_MESSAGES)
public class MessageEntity implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = Constants.OWNER_EMAIL)
    private String ownerEmail;

    @ColumnInfo(name = Constants.SENDER_ID)
    private String senderId;

    @ColumnInfo(name = Constants.RECEIVER_ID)
    private String receiverId;

    @ColumnInfo(name = Constants.MESSAGE)
    private String message;

    @ColumnInfo(name = Constants.TIMESTAMP)
    private String timestamp;

    public MessageEntity(String ownerEmail, String senderId, String receiverId, String message, String timestamp) {
        this.ownerEmail = ownerEmail;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.message = message;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
