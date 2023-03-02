package com.care360.findmyfamilyandfriends.HomeScreen.FragmentLocation.Chat.DB;

import android.database.Cursor;
import androidx.lifecycle.LiveData;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

@SuppressWarnings({"unchecked", "deprecation"})
public final class MessageDao_Impl implements MessageDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<MessageEntity> __insertionAdapterOfMessageEntity;

  public MessageDao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfMessageEntity = new EntityInsertionAdapter<MessageEntity>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR REPLACE INTO `message_table` (`id`,`sender_id`,`receiver_id`,`message`,`timestamp`) VALUES (nullif(?, 0),?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, MessageEntity value) {
        stmt.bindLong(1, value.getId());
        if (value.getSenderId() == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.getSenderId());
        }
        if (value.getReceiverId() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.getReceiverId());
        }
        if (value.getMessage() == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.getMessage());
        }
        if (value.getTimestamp() == null) {
          stmt.bindNull(5);
        } else {
          stmt.bindString(5, value.getTimestamp());
        }
      }
    };
  }

  @Override
  public void saveMessage(final MessageEntity messageEntity) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfMessageEntity.insert(messageEntity);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public LiveData<List<MessageEntity>> getMessagesList(final String senderId,
      final String receiverId) {
    final String _sql = "SELECT * from message_table WHERE sender_id = ? AND receiver_id = ? UNION SELECT * from message_table WHERE sender_id = ? AND receiver_id = ? ORDER by timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 4);
    int _argIndex = 1;
    if (senderId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, senderId);
    }
    _argIndex = 2;
    if (receiverId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, receiverId);
    }
    _argIndex = 3;
    if (receiverId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, receiverId);
    }
    _argIndex = 4;
    if (senderId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, senderId);
    }
    return __db.getInvalidationTracker().createLiveData(new String[]{"message_table"}, false, new Callable<List<MessageEntity>>() {
      @Override
      public List<MessageEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSenderId = CursorUtil.getColumnIndexOrThrow(_cursor, "sender_id");
          final int _cursorIndexOfReceiverId = CursorUtil.getColumnIndexOrThrow(_cursor, "receiver_id");
          final int _cursorIndexOfMessage = CursorUtil.getColumnIndexOrThrow(_cursor, "message");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final List<MessageEntity> _result = new ArrayList<MessageEntity>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final MessageEntity _item;
            _item = new MessageEntity();
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            _item.setId(_tmpId);
            final String _tmpSenderId;
            if (_cursor.isNull(_cursorIndexOfSenderId)) {
              _tmpSenderId = null;
            } else {
              _tmpSenderId = _cursor.getString(_cursorIndexOfSenderId);
            }
            _item.setSenderId(_tmpSenderId);
            final String _tmpReceiverId;
            if (_cursor.isNull(_cursorIndexOfReceiverId)) {
              _tmpReceiverId = null;
            } else {
              _tmpReceiverId = _cursor.getString(_cursorIndexOfReceiverId);
            }
            _item.setReceiverId(_tmpReceiverId);
            final String _tmpMessage;
            if (_cursor.isNull(_cursorIndexOfMessage)) {
              _tmpMessage = null;
            } else {
              _tmpMessage = _cursor.getString(_cursorIndexOfMessage);
            }
            _item.setMessage(_tmpMessage);
            final String _tmpTimestamp;
            if (_cursor.isNull(_cursorIndexOfTimestamp)) {
              _tmpTimestamp = null;
            } else {
              _tmpTimestamp = _cursor.getString(_cursorIndexOfTimestamp);
            }
            _item.setTimestamp(_tmpTimestamp);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public MessageEntity lastMessageTimestamp(final String senderId, final String receiverId) {
    final String _sql = "SELECT * from message_table WHERE sender_id= ? AND receiver_id= ? UNION SELECT * from message_table WHERE sender_id= ? AND receiver_id= ? ORDER BY timestamp desc limit 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 4);
    int _argIndex = 1;
    if (senderId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, senderId);
    }
    _argIndex = 2;
    if (receiverId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, receiverId);
    }
    _argIndex = 3;
    if (receiverId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, receiverId);
    }
    _argIndex = 4;
    if (senderId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, senderId);
    }
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfSenderId = CursorUtil.getColumnIndexOrThrow(_cursor, "sender_id");
      final int _cursorIndexOfReceiverId = CursorUtil.getColumnIndexOrThrow(_cursor, "receiver_id");
      final int _cursorIndexOfMessage = CursorUtil.getColumnIndexOrThrow(_cursor, "message");
      final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
      final MessageEntity _result;
      if(_cursor.moveToFirst()) {
        _result = new MessageEntity();
        final int _tmpId;
        _tmpId = _cursor.getInt(_cursorIndexOfId);
        _result.setId(_tmpId);
        final String _tmpSenderId;
        if (_cursor.isNull(_cursorIndexOfSenderId)) {
          _tmpSenderId = null;
        } else {
          _tmpSenderId = _cursor.getString(_cursorIndexOfSenderId);
        }
        _result.setSenderId(_tmpSenderId);
        final String _tmpReceiverId;
        if (_cursor.isNull(_cursorIndexOfReceiverId)) {
          _tmpReceiverId = null;
        } else {
          _tmpReceiverId = _cursor.getString(_cursorIndexOfReceiverId);
        }
        _result.setReceiverId(_tmpReceiverId);
        final String _tmpMessage;
        if (_cursor.isNull(_cursorIndexOfMessage)) {
          _tmpMessage = null;
        } else {
          _tmpMessage = _cursor.getString(_cursorIndexOfMessage);
        }
        _result.setMessage(_tmpMessage);
        final String _tmpTimestamp;
        if (_cursor.isNull(_cursorIndexOfTimestamp)) {
          _tmpTimestamp = null;
        } else {
          _tmpTimestamp = _cursor.getString(_cursorIndexOfTimestamp);
        }
        _result.setTimestamp(_tmpTimestamp);
      } else {
        _result = null;
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
