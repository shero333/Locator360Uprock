package com.hammad.findmyfamily.HomeScreen.FragmentSafety.EmergRoomDB;

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

    @Query("Select * from "+ Constants.EMERGENCY_CONTACT+" where owner_email= :ownerEmail")
    List<EmergencyContactEntity> getEmergencyContactsList(String ownerEmail);

}
