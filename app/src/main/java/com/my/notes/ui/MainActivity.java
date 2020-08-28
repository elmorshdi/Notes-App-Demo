package com.my.notes.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.my.notes.NoteAdapter;
import com.my.notes.R;
import com.my.notes.databinding.ActivityMainBinding;
import com.my.notes.pojo.Note;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity {
    List<Note> notes;
    NoteViewModel noteViewModel;
    NoteAdapter adapter;
    Note note;
    private ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        adapter = new NoteAdapter();

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
                        if (notes == null || notes.size() == 0) {
                            binding.emptyText.setVisibility(View.VISIBLE);

                        } else {
                            binding.emptyText.setVisibility(View.INVISIBLE);

                            adapter.setNotes(notes);
                            adapter.notifyDataSetChanged();
                        }
                    }


                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }


                });

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setAdapter(adapter);

        binding.buttonAddNote.setOnClickListener(v -> {
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
                Snackbar.make(binding.layout, "Note Deleted", Snackbar.LENGTH_LONG)
                        .setAction("Undo", view -> noteViewModel.insert(note)).show();
            }
        }).attachToRecyclerView(binding.recyclerView);
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
