package com.hammad.findmyfamily.HomeScreen.FragmentSafety.EmergContactRoomDB;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.hammad.findmyfamily.Util.Constants;

import java.io.Serializable;

@Entity (tableName = "emergency_contact")
public class EmergencyContactEntity implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = Constants.OWNER_EMAIL)
    private String owner_email;

}
