package com.hammad.findmyfamily.HomeScreen.FragmentSafety.EmergRoomDB;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.hammad.findmyfamily.Util.Constants;

@Database(entities = {EmergencyContactEntity.class},version = 2,exportSchema = false)
public abstract class RoomDBHelper extends RoomDatabase {

    private static RoomDBHelper instance;

    public static synchronized RoomDBHelper getInstance(Context context) {

        if(instance == null) {
            instance = Room.databaseBuilder(context,RoomDBHelper.class, Constants.DATABASE_NAME)
                    .allowMainThreadQueries()
                    .addMigrations(MIGRATION_1_2)
                    .fallbackToDestructiveMigration()
                    .build();
        }

        return instance;
    }

    static final Migration MIGRATION_1_2 = new Migration(1,2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE FindMyFamily_db ADD "+Constants.CONTACT_ID + " LONG");
        }
    };

    public abstract EmergencyContactDao emergencyContactDao();

}
