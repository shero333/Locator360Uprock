package com.hammad.findmyfamily.HomeScreen.FragmentSafety.EmergContactRoomDB;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.hammad.findmyfamily.Util.Constants;

@Database(entities = {EmergencyContactEntity.class},version = 1,exportSchema = false)
public abstract class RoomDBHelper extends RoomDatabase {

    private static RoomDBHelper instance;

    public static synchronized RoomDBHelper getInstance(Context context) {

        if(instance == null) {
            instance = Room.databaseBuilder(context,RoomDBHelper.class, Constants.DATABASE_NAME)
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }

        return instance;
    }
}
