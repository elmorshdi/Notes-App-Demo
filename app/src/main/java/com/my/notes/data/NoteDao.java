package com.my.notes.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.my.notes.pojo.Note;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;

@Dao
interface NoteDao {
    @Insert
    Completable insert(Note note);

    @Delete
    Completable delete(Note note);

    @Update
    Completable update(Note note);

    @Query("DELETE FROM Note ")
    Completable deleteAll();

    @Query("SELECT * FROM Note ORDER BY priority DESC")
    Observable<List<Note>> getAllNote();
}
