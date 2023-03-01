package com.hammad.findmyfamily.HomeScreen.FragmentLocation.Chat.DB;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.hammad.findmyfamily.Util.Constants;

import java.util.List;

@Dao
public interface MessageDao {

    @Insert(onConflict = REPLACE)
    void saveMessage(MessageEntity messageEntity);

    @Query("SELECT * from " + Constants.TABLE_MESSAGES + " WHERE sender_id = :senderId AND receiver_id = :receiverId " +
            "UNION " +
            "SELECT * from " + Constants.TABLE_MESSAGES + " WHERE sender_id = :receiverId AND receiver_id = :senderId ORDER by timestamp DESC")
    LiveData<List<MessageEntity>> getMessagesList(String senderId, String receiverId);

    @Query("SELECT * from " + Constants.TABLE_MESSAGES + " WHERE sender_id= :senderId AND receiver_id= :receiverId " +
            "UNION " +
            "SELECT * from " + Constants.TABLE_MESSAGES + " WHERE sender_id= :receiverId AND receiver_id= :senderId " +
            "ORDER BY timestamp desc limit 1")
    MessageEntity lastMessageTimestamp(String senderId,String receiverId);
}
