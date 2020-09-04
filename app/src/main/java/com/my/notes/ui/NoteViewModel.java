package com.my.notes.ui;

import android.content.Context;

import androidx.lifecycle.ViewModel;

import com.my.notes.data.NoteRepository;
import com.my.notes.pojo.Note;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;


class NoteViewModel extends ViewModel {
    String tag = "tag";
    private List<Note> notes = new ArrayList<>();
    private NoteRepository repository;

    public NoteViewModel(Context context) {
        repository = new NoteRepository(context);
    }

    public void insert(Note note) {
        repository.insert(note);
    }

    public void update(Note note) {
        repository.update(note);
    }

    public void delete(Note note) {
        repository.delete(note);
    }

    public void deleteAllNotes() {
        repository.deleteAllNotes();
    }

    public Observable<List<Note>> getAllNotes() {

        return repository.getAllNotes();
    }
}
