package com.my.notes.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

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
    List<Note> notesList;
    NoteViewModel noteViewModel;
    NoteAdapter adapter;
    Note note;
    String tag = "gettag";
    private ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        adapter = new NoteAdapter();
        adapter.notifyDataSetChanged();
        noteViewModel = new NoteViewModel(MainActivity.this);
        binding.searchEdit.clearFocus();

        noteViewModel.getAllNotes()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Note>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.v(tag, "getSubscribe ");

                    }

                    @Override
                    public void onNext(List<Note> notes) {
                        if (notes == null || notes.size() == 0) {
                            binding.emptyText.setVisibility(View.VISIBLE);
                            binding.recyclerView.setVisibility(View.INVISIBLE);

                        } else {
                            binding.emptyText.setVisibility(View.INVISIBLE);
                            binding.recyclerView.setVisibility(View.VISIBLE);
                            Log.v(tag, "inserttoadaptar ");

                            notesList = notes;
                            adapter.setNotes(notes);
                            adapter.notifyDataSetChanged();
                        }
                    }


                    @Override
                    public void onError(Throwable e) {
                        Log.v(tag, "error" + e.getMessage());

                    }

                    @Override
                    public void onComplete() {
                        Log.v(tag, "complet");

                    }


                });


        binding.recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setAdapter(adapter);

        binding.buttonAddNote.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddNoteActivity.class);
            intent.putExtra("Type", "Add");
            startActivity(intent);

        });
        binding.searchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                adapter.cancelTimer();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (notesList.size() != 0)
                    adapter.search(editable.toString());
            }
        });
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
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
            intent.putExtra("noteToUpdateDate", note.getDate());
            intent.putExtra("noteToUpdateColor", note.getColor());
            intent.putExtra("noteToUpdateUri", note.getUri());
            intent.putExtra("noteToUpdateImage", note.getImage());
            intent.putExtra("noteToUpdateDESCRIPTION", note.getDescription());
            intent.putExtra("noteToUpdatePRIORITY", note.getPriority());
            intent.putExtra("Type", "Update");
            startActivity(intent);

        });
    }

}
