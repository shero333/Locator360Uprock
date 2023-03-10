package com.care360.findmyfamilyandfriends.HomeScreen.ui.FragmentSafety.EmergencyRoomDB;

import android.database.Cursor;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings({"unchecked", "deprecation"})
public final class EmergencyContactDao_Impl implements EmergencyContactDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<EmergencyContactEntity> __insertionAdapterOfEmergencyContactEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteEmergencyContact;

  public EmergencyContactDao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfEmergencyContactEntity = new EntityInsertionAdapter<EmergencyContactEntity>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR REPLACE INTO `emergency_contact` (`id`,`owner_email`,`contact_id`,`contact_name`,`contact_no`) VALUES (nullif(?, 0),?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, EmergencyContactEntity value) {
        stmt.bindLong(1, value.getId());
        if (value.getOwnerEmail() == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.getOwnerEmail());
        }
        if (value.getContactId() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.getContactId());
        }
        if (value.getContactName() == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.getContactName());
        }
        if (value.getContactNo() == null) {
          stmt.bindNull(5);
        } else {
          stmt.bindString(5, value.getContactNo());
        }
      }
    };
    this.__preparedStmtOfDeleteEmergencyContact = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "DELETE from emergency_contact where owner_email= ? AND contact_id= ? AND contact_no= ?";
        return _query;
      }
    };
  }

  @Override
  public void addContact(final EmergencyContactEntity emergencyContactEntity) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfEmergencyContactEntity.insert(emergencyContactEntity);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void deleteEmergencyContact(final String ownerEmail, final String contactId,
      final String phoneNo) {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteEmergencyContact.acquire();
    int _argIndex = 1;
    if (ownerEmail == null) {
      _stmt.bindNull(_argIndex);
    } else {
      _stmt.bindString(_argIndex, ownerEmail);
    }
    _argIndex = 2;
    if (contactId == null) {
      _stmt.bindNull(_argIndex);
    } else {
      _stmt.bindString(_argIndex, contactId);
    }
    _argIndex = 3;
    if (phoneNo == null) {
      _stmt.bindNull(_argIndex);
    } else {
      _stmt.bindString(_argIndex, phoneNo);
    }
    __db.beginTransaction();
    try {
      _stmt.executeUpdateDelete();
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
      __preparedStmtOfDeleteEmergencyContact.release(_stmt);
    }
  }

  @Override
  public List<EmergencyContactEntity> getEmergencyContactsList(final String ownerEmail) {
    final String _sql = "Select * from emergency_contact where owner_email= ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (ownerEmail == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, ownerEmail);
    }
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfOwnerEmail = CursorUtil.getColumnIndexOrThrow(_cursor, "owner_email");
      final int _cursorIndexOfContactId = CursorUtil.getColumnIndexOrThrow(_cursor, "contact_id");
      final int _cursorIndexOfContactName = CursorUtil.getColumnIndexOrThrow(_cursor, "contact_name");
      final int _cursorIndexOfContactNo = CursorUtil.getColumnIndexOrThrow(_cursor, "contact_no");
      final List<EmergencyContactEntity> _result = new ArrayList<EmergencyContactEntity>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final EmergencyContactEntity _item;
        _item = new EmergencyContactEntity();
        final int _tmpId;
        _tmpId = _cursor.getInt(_cursorIndexOfId);
        _item.setId(_tmpId);
        final String _tmpOwnerEmail;
        if (_cursor.isNull(_cursorIndexOfOwnerEmail)) {
          _tmpOwnerEmail = null;
        } else {
          _tmpOwnerEmail = _cursor.getString(_cursorIndexOfOwnerEmail);
        }
        _item.setOwnerEmail(_tmpOwnerEmail);
        final String _tmpContactId;
        if (_cursor.isNull(_cursorIndexOfContactId)) {
          _tmpContactId = null;
        } else {
          _tmpContactId = _cursor.getString(_cursorIndexOfContactId);
        }
        _item.setContactId(_tmpContactId);
        final String _tmpContactName;
        if (_cursor.isNull(_cursorIndexOfContactName)) {
          _tmpContactName = null;
        } else {
          _tmpContactName = _cursor.getString(_cursorIndexOfContactName);
        }
        _item.setContactName(_tmpContactName);
        final String _tmpContactNo;
        if (_cursor.isNull(_cursorIndexOfContactNo)) {
          _tmpContactNo = null;
        } else {
          _tmpContactNo = _cursor.getString(_cursorIndexOfContactNo);
        }
        _item.setContactNo(_tmpContactNo);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
