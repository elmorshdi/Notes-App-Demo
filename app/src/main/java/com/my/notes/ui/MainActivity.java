package com.my.notes.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.my.notes.NoteAdapter;
import com.my.notes.R;
import com.my.notes.pojo.Note;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity {
    List<Note> notes;
    NoteViewModel noteViewModel;
    RecyclerView recyclerView;
    NoteAdapter adapter;
    ConstraintLayout constraintLayout;
    Note note;
    FloatingActionButton buttonAddNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        adapter = new NoteAdapter();
        recyclerView = findViewById(R.id.recycler_view);
        constraintLayout = findViewById(R.id.layout);
        noteViewModel = new NoteViewModel(MainActivity.this);

        noteViewModel.getAllNotes()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Note>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<Note> notes) {
                        adapter.setNotes(notes);
                        adapter.notifyDataSetChanged();
                    }


                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }


                });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        buttonAddNote = findViewById(R.id.button_add_note);
        buttonAddNote.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddNoteActivity.class);
            intent.putExtra("Type", "Add");
            startActivity(intent);

        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                note = adapter.getNoteAt(viewHolder.getAdapterPosition());
                noteViewModel.delete(note);
                Snackbar.make(constraintLayout, "Note Deleted", Snackbar.LENGTH_LONG)
                        .setAction("Undo", view -> noteViewModel.insert(note)).show();
            }
        }).attachToRecyclerView(recyclerView);
        adapter.setOnItemClickListener(note -> {
            Intent intent = new Intent(MainActivity.this, AddNoteActivity.class);
            intent.putExtra("noteToUpdateId", note.getId());
            intent.putExtra("noteToUpdateTITLE", note.getTitle());
            intent.putExtra("noteToUpdateDESCRIPTION", note.getDescription());
            intent.putExtra("noteToUpdatePRIORITY", note.getPriority());
            intent.putExtra("Type", "Update");
            startActivity(intent);

        });
    }

}
