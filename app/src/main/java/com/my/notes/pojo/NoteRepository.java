package com.my.notes.pojo;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.CompletableObserver;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class NoteRepository {

    NoteDatabase database;
    String tag = "repo";
    private List<Note> allNotes;

    public NoteRepository(Context context) {
        database = NoteDatabase.getInstance(context);
        NoteDao noteDao = database.noteDao();
        allNotes = new ArrayList<>();
    }

    public void insert(final Note note) {
        database.noteDao().insert(note).subscribeOn(Schedulers.io())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.v(tag, "insertSubscribe ");

                    }

                    @Override
                    public void onComplete() {
                        Log.v(tag, "insertcomplet" + note.getTitle());
                    }

                    @Override
                    public void onError(Throwable e) {
                    }
                });
    }

    public void update(Note note) {
        database.noteDao().update(note).subscribeOn(Schedulers.io())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
    }

    public void delete(Note note) {
        database.noteDao().delete(note).subscribeOn(Schedulers.computation())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
    }

    public void deleteAllNotes() {
        database.noteDao().deleteAll().subscribeOn(Schedulers.computation())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
    }

    public Observable<List<Note>> getAllNotes() {

        return database.noteDao().getAllNote();
    }
}
