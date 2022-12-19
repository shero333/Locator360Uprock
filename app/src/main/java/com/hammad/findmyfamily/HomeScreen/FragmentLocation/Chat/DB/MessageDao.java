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

    @Query("SELECT * from "+ Constants.TABLE_MESSAGES+" where sender_id= :senderId OR receiver_id= :receiverId OR sender_id= :receiverId OR receiver_id= :senderId ORDER BY timestamp ASC")
    LiveData<List<MessageEntity>> getMessagesList(/*String ownerEmail,*/ String senderId, String receiverId);

    @Query("SELECT timestamp from "+Constants.TABLE_MESSAGES+" where sender_id= :senderId AND receiver_id= :receiverId ORDER BY timestamp DESC")
    String lastMessageTimestamp(/*String ownerEmail,*/String senderId,String receiverId);
}
