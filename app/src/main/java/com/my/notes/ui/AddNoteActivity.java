package com.my.notes.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.my.notes.R;
import com.my.notes.databinding.ActivityAddNoteBinding;
import com.my.notes.pojo.Note;

public class AddNoteActivity extends AppCompatActivity {
    Intent intent;

    private NoteViewModel noteViewModel;
    ActivityAddNoteBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_note);

        noteViewModel = new NoteViewModel(AddNoteActivity.this);

        binding.numberPickerPriority.setMinValue(1);
        binding.numberPickerPriority.setMaxValue(10);
        intent = getIntent();

        if (intent.getStringExtra("Type").equals("Update")) {
            binding.tit.setText("Edit Note");
            binding.editTextTitle.setText(intent.getStringExtra("noteToUpdateTITLE"));
            binding.editTextDescription.setText(intent.getStringExtra("noteToUpdateDESCRIPTION"));
            binding.numberPickerPriority.setValue(intent.getIntExtra("noteToUpdatePRIORITY", 1));
        } else {
            binding.tit.setText("Add Note");
        }

    }

    public void save(View view) {
        String title = binding.editTextTitle.getText().toString();
        String description = binding.editTextDescription.getText().toString();
        int priority = binding.numberPickerPriority.getValue();

        if (title.trim().isEmpty() || description.trim().isEmpty()) {
            Toast.makeText(this, "Enter Title and Description", Toast.LENGTH_SHORT).show();

        } else {
            if (intent.getStringExtra("Type").equals("Update")) {
                Note note = new Note(title, description, priority);
                note.setId(intent.getIntExtra("noteToUpdateId", 1));
                noteViewModel.update(note);
                Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show();


            } else {

                noteViewModel.insert(new Note(title, description, priority));
                Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
            }
            binding.editTextDescription.setText("");
            binding.editTextDescription.clearFocus();
            binding.editTextTitle.setText("");
            binding.editTextTitle.clearFocus();
            binding.numberPickerPriority.setValue(1);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void close(View view) {
        onBackPressed();
    }


}


