package com.hammad.findmyfamily.HomeScreen.FragmentSafety.EmergencyRoomDB;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.hammad.findmyfamily.Util.Constants;

import java.util.List;

@Dao
public interface EmergencyContactDao {

    @Insert(onConflict = REPLACE)
    void addContact(EmergencyContactEntity emergencyContactEntity);

    @Query("Select * from "+ Constants.EMERG_CONTACT_TABLE_NAME +" where owner_email= :ownerEmail")
    List<EmergencyContactEntity> getEmergencyContactsList(String ownerEmail);

    @Query("DELETE from "+Constants.EMERG_CONTACT_TABLE_NAME + " where owner_email= :ownerEmail AND contact_id= :contactId AND contact_no= :phoneNo")
    void deleteEmergencyContact(String ownerEmail,String contactId,String phoneNo);

}
