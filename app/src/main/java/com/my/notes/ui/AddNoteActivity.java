package com.my.notes.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.my.notes.R;
import com.my.notes.pojo.Note;

public class AddNoteActivity extends AppCompatActivity {
    Intent intent;
    private EditText editTextTitle;
    private EditText editTextDescription;
    private NumberPicker numberPickerPriority;
    private TextView Tit;
    private ImageButton buttonSave;
    private ImageButton buttonClose;
    private NoteViewModel noteViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        editTextTitle = findViewById(R.id.edit_text_title);
        editTextDescription = findViewById(R.id.edit_text_description);
        numberPickerPriority = findViewById(R.id.number_picker_priority);
        buttonClose = findViewById(R.id.close);
        buttonSave = findViewById(R.id.save);
        Tit = findViewById(R.id.tit);
        noteViewModel = new NoteViewModel(AddNoteActivity.this);

        numberPickerPriority.setMinValue(1);
        numberPickerPriority.setMaxValue(10);
        intent = getIntent();

        if (intent.getStringExtra("Type").equals("Update")) {
            Tit.setText("Edit Note");
            editTextTitle.setText(intent.getStringExtra("noteToUpdateTITLE"));
            editTextDescription.setText(intent.getStringExtra("noteToUpdateDESCRIPTION"));
            numberPickerPriority.setValue(intent.getIntExtra("noteToUpdatePRIORITY", 1));
        } else {
            Tit.setText("Add Note");
        }

    }

    public void save(View view) {
        String title = editTextTitle.getText().toString();
        String description = editTextDescription.getText().toString();
        int priority = numberPickerPriority.getValue();

        if (title.trim().isEmpty() || description.trim().isEmpty()) {
            return;
        } else {
            if (intent.getStringExtra("Type").equals("Update")) {
                Note note = new Note(title, description, priority);
                note.setId(intent.getIntExtra("noteToUpdateId", 1));
                noteViewModel.update(note);
            } else {

                noteViewModel.insert(new Note(title, description, priority));
                Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
            }
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


