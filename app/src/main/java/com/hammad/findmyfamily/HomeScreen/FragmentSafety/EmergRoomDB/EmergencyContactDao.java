package com.hammad.findmyfamily.HomeScreen.FragmentSafety.EmergRoomDB;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Insert;

@Dao
public interface EmergencyContactDao {

    @Insert(onConflict = REPLACE)
    void addContact(EmergencyContactEntity emergencyContactEntity);

}
