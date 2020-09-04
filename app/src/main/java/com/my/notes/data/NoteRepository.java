package com.my.notes.data;

import android.content.Context;
import android.util.Log;

import com.my.notes.pojo.Note;

import java.util.List;

import io.reactivex.CompletableObserver;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class NoteRepository {

    NoteDatabase database;
    String tag = "repotag";
    private List<Note> allNotes;

    public NoteRepository(Context context) {
        database = NoteDatabase.getInstance(context);
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
