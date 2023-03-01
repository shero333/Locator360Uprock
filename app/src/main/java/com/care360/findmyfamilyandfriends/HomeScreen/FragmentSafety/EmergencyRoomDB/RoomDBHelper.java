package com.care360.findmyfamilyandfriends.HomeScreen.FragmentSafety.EmergencyRoomDB;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.care360.findmyfamilyandfriends.HomeScreen.FragmentLocation.Chat.DB.MessageDao;
import com.care360.findmyfamilyandfriends.HomeScreen.FragmentLocation.Chat.DB.MessageEntity;
import com.care360.findmyfamilyandfriends.Util.Constants;

@Database(entities = {EmergencyContactEntity.class, MessageEntity.class},version = 3,exportSchema = false)
public abstract class RoomDBHelper extends RoomDatabase {

    private static RoomDBHelper instance;

    public static synchronized RoomDBHelper getInstance(Context context) {

        if(instance == null) {
            instance = Room.databaseBuilder(context,RoomDBHelper.class, Constants.DATABASE_NAME)
                    .allowMainThreadQueries()
                    .addMigrations(MIGRATION_1_2,MIGRATION_2_3)
                    .fallbackToDestructiveMigration()
                    .build();
        }

        return instance;
    }

    static final Migration MIGRATION_1_2 = new Migration(1,2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE FindMyFamily_db ADD "+Constants.CONTACT_ID + " STRING");
        }
    };

    static final Migration MIGRATION_2_3 = new Migration(2,3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE " +Constants.TABLE_MESSAGES +"("+Constants.ID+"  INTEGER PRIMARY KEY," +
                    ""+Constants.OWNER_EMAIL+" TEXT,"+
                    ""+Constants.SENDER_ID+" TEXT," +
                    ""+Constants.RECEIVER_ID+" TEXT," +
                    ""+Constants.MESSAGE+" TEXT," +
                    ""+Constants.TIMESTAMP+" TEXT "+")");
        }
    };

    public abstract EmergencyContactDao emergencyContactDao();

    public abstract MessageDao messageDao();

}
