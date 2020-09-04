package com.my.notes.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.my.notes.pojo.Note;

@Database(entities = Note.class, version = 1, exportSchema = false)
abstract class NoteDatabase extends RoomDatabase {
    public static NoteDatabase instance;

    public static synchronized NoteDatabase getInstance(Context context) {

        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext()
                    , NoteDatabase.class, "Note")
                    .fallbackToDestructiveMigration()
                    .build();


        }
        return instance;
    }

    public abstract NoteDao noteDao();
}
