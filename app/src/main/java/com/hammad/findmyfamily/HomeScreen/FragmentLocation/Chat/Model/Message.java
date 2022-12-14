package com.hammad.findmyfamily.HomeScreen.FragmentLocation.Chat.Model;

public class Message {
    private String senderId;
    private String receiverId;
    private String messageText;
    private String messageTimestamp;

    public Message() {}

    public Message(String senderId, String receiverId, String messageText, String messageTimestamp) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.messageText = messageText;
        this.messageTimestamp = messageTimestamp;
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

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageTimestamp() {
        return messageTimestamp;
    }

    public void setMessageTimestamp(String messageTimestamp) {
        this.messageTimestamp = messageTimestamp;
    }
}