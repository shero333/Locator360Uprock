package com.hammad.findmyfamily.HomeScreen.FragmentLocation.Chat.DB;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.hammad.findmyfamily.Util.Constants;

import java.util.List;

@Dao
public interface MessageDao {

    @Insert(onConflict = REPLACE)
    void saveMessage(MessageEntity messageEntity);

    @Query("SELECT * from "+ Constants.TABLE_MESSAGES+" where owner_email= :ownerEmail AND sender_id= :senderId AND receiver_id= :receiverId ORDER BY timestamp ASC")
    List<MessageEntity> getMessagesList(String ownerEmail,String senderId,String receiverId);

    @Query("SELECT timestamp from "+Constants.TABLE_MESSAGES+" where owner_email= :ownerEmail AND sender_id= :senderId AND receiver_id= :receiverId ORDER BY timestamp DESC")
    String lastMessageTimestamp(String ownerEmail,String senderId,String receiverId);
}
