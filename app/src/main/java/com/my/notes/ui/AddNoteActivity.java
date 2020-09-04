package com.my.notes.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.my.notes.R;
import com.my.notes.databinding.ActivityAddNoteBinding;
import com.my.notes.pojo.Note;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class AddNoteActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_STORAGE_PERMISSION = 1;
    private static final int REQUEST_CODE_SELECT_IMAGE = 2;
    private Intent intent;
    private String selectedColor;
    private ActivityAddNoteBinding binding;
    private byte[] selectedImage;
    private NoteViewModel noteViewModel;
    private AlertDialog alertDialog;
    private Note currentNote;

    // convert from bitmap to byte array
    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    // convert from byte array to bitmap
    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_note);
        noteViewModel = new NoteViewModel(AddNoteActivity.this);
        bottomSheet();
        setDate();
        setViewColor("#333333", 0);
        currentNote = new Note();
        binding.editTextDescription.clearFocus();
        binding.editTextTitle.clearFocus();
        intent = getIntent();
        if (Objects.equals(intent.getStringExtra("Type"), "Update")) {
            updateUI();
        }

        binding.imagedelet.setOnClickListener(view -> {
            binding.noteImage.setImageBitmap(null);
            selectedImage = null;
            binding.noteImagelayout.setVisibility(View.GONE);
        });
        binding.deleteUri.setOnClickListener(view -> {
            binding.uriText.setText(null);
            binding.uriTextLayout.setVisibility(View.GONE);
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void updateUI() {
        binding.editTextTitle.setText(intent.getStringExtra("noteToUpdateTITLE"));
        currentNote.setTitle(intent.getStringExtra("noteToUpdateTITLE"));
        binding.editTextDescription.setText(intent.getStringExtra("noteToUpdateDESCRIPTION"));
        currentNote.setDescription(intent.getStringExtra("noteToUpdateTITLE"));
        binding.numberPickerPriority.setValue(intent.getIntExtra("noteToUpdatePRIORITY", 1));
        currentNote.setPriority(intent.getIntExtra("noteToUpdatePRIORITY", 1));
        binding.date.setText(intent.getStringExtra("noteToUpdateDate"));
        currentNote.setDate(intent.getStringExtra("noteToUpdateDate"));
        if (!intent.getStringExtra("noteToUpdateUri").equals("")) {
            binding.uriText.setText(intent.getStringExtra("noteToUpdateUri"));
            currentNote.setUri(intent.getStringExtra("noteToUpdateUri"));
            binding.uriTextLayout.setVisibility(View.VISIBLE);
        }
        byte[] bytes = intent.getByteArrayExtra("noteToUpdateImage");
        if (bytes.length != 0) {
            currentNote.setImage(intent.getByteArrayExtra("noteToUpdateImage"));
            binding.noteImage.setImageBitmap(getImage(intent.getByteArrayExtra("noteToUpdateImage")));
            binding.noteImagelayout.setVisibility(View.VISIBLE);
            selectedImage = intent.getByteArrayExtra("noteToUpdateImage");
        }
        setViewColor(intent.getStringExtra("noteToUpdateColor"), 0);
        currentNote.setColor(intent.getStringExtra("noteToUpdateColor"));
        currentNote.setId(intent.getIntExtra("noteToUpdateId", 1));

    }

    public void save(View view) {
        String title = binding.editTextTitle.getText().toString();
        String date = binding.date.getText().toString();
        String description = binding.editTextDescription.getText().toString();
        int priority = binding.numberPickerPriority.getValue();

        if (title.trim().isEmpty() || description.trim().isEmpty()) {
            Toast.makeText(this, "Enter Title and Description", Toast.LENGTH_SHORT).show();

        } else {
            if (Objects.equals(intent.getStringExtra("Type"), "Update")) {
                currentNote = new Note(title, date, description, selectedColor, priority);
                currentNote.setId(intent.getIntExtra("noteToUpdateId", 1));
                if (selectedImage != null) {
                    currentNote.setImage(selectedImage);
                } else {
                    currentNote.setImage(new byte[0]);
                }
                if (binding.uriTextLayout.getVisibility() == View.VISIBLE) {
                    currentNote.setUri(binding.uriText.getText().toString());
                } else {
                    currentNote.setUri("");
                }
                noteViewModel.update(currentNote);
                Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show();
            } else {
                currentNote = new Note(title, date, description, selectedColor, priority);
                if (selectedImage != null) {
                    currentNote.setImage(selectedImage);
                } else {
                    currentNote.setImage(new byte[0]);
                }
                if (binding.uriTextLayout.getVisibility() == View.VISIBLE) {
                    currentNote.setUri(binding.uriText.getText().toString());
                } else {
                    currentNote.setUri("");
                }
                noteViewModel.insert(currentNote);
                Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
            }
            clearUi();

        }

    }

    private void clearUi() {
        binding.editTextDescription.setText(null);
        binding.editTextTitle.setText(null);
        setDate();
        binding.numberPickerPriority.setValue(1);
        binding.noteImage.setImageBitmap(null);
        binding.noteImagelayout.setVisibility(View.GONE);
        selectedImage = null;
        binding.uriText.setText(null);
        binding.uriTextLayout.setVisibility(View.GONE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getSelectImage();
            } else {
                Toast.makeText(this, "PERMISSION DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                try {
                    String Tag = "tag";
                    Log.v(Tag, "done");
                    InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    binding.noteImage.setImageBitmap(bitmap);
                    binding.noteImagelayout.setVisibility(View.VISIBLE);
                    selectedImage = getBytes(bitmap);
                } catch (Exception e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            }
        }

    }

    public void close(View view) {
        onBackPressed();
    }

    void setDate() {
        binding.date.setText(new SimpleDateFormat("EEE,d MMM YYYY hh:mm a", Locale.getDefault())
                .format(new Date()));
    }

    public void bottomSheet() {

        final LinearLayout bottomSheetLayout = binding.includeLayout.bottomSheet;
        final BottomSheetBehavior<LinearLayout> bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout);
        bottomSheetLayout.findViewById(R.id.tileLayout).setOnClickListener(view -> {
            if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

            } else {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
        binding.includeLayout.color0.setOnClickListener(view -> setViewColor("#333333", 0));
        binding.includeLayout.color1.setOnClickListener(view -> setViewColor("#fdbc3e", 1));
        binding.includeLayout.color2.setOnClickListener(view -> setViewColor("#ed6663", 2));
        binding.includeLayout.color3.setOnClickListener(view -> setViewColor("#f8bd7f", 3));
        binding.includeLayout.color4.setOnClickListener(view -> setViewColor("#145374", 4));
        binding.includeLayout.color5.setOnClickListener(view -> setViewColor("#68b0ab", 5));

        binding.includeLayout.addImgLayout.setOnClickListener(view -> {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(AddNoteActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CODE_STORAGE_PERMISSION);
            } else {
                getSelectImage();
            }
        });

        binding.includeLayout.addLinkLayout.setOnClickListener(view -> {

            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            showAlertUri();
        });
        binding.includeLayout.removeNoteLayout.setOnClickListener(view -> {

            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            if (Objects.equals(intent.getStringExtra("Type"), "Update")) {
                noteViewModel.delete(currentNote);

            }
            clearUi();

        });
    }

    private void getSelectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);
        }

    }

    private void setViewColor(String color, int i) {
        selectedColor = color;
        GradientDrawable gradientDrawable = (GradientDrawable) binding.view.getBackground();
        gradientDrawable.setColor(Color.parseColor(color));
        binding.numberPickerPriority.setDividerColor(Color.parseColor(color));
        switch (i) {
            case 5:
                binding.includeLayout.imagecolor0.setImageResource(0);
                binding.includeLayout.imagecolor1.setImageResource(0);
                binding.includeLayout.imagecolor2.setImageResource(0);
                binding.includeLayout.imagecolor3.setImageResource(0);
                binding.includeLayout.imagecolor4.setImageResource(0);
                binding.includeLayout.imagecolor5.setImageResource(R.drawable.ic_done);
                break;
            case 4:
                binding.includeLayout.imagecolor0.setImageResource(0);
                binding.includeLayout.imagecolor1.setImageResource(0);
                binding.includeLayout.imagecolor2.setImageResource(0);
                binding.includeLayout.imagecolor3.setImageResource(0);
                binding.includeLayout.imagecolor4.setImageResource(R.drawable.ic_done);
                binding.includeLayout.imagecolor5.setImageResource(0);
                break;
            case 3:
                binding.includeLayout.imagecolor0.setImageResource(0);
                binding.includeLayout.imagecolor1.setImageResource(0);
                binding.includeLayout.imagecolor2.setImageResource(0);
                binding.includeLayout.imagecolor3.setImageResource(R.drawable.ic_done);
                binding.includeLayout.imagecolor4.setImageResource(0);
                binding.includeLayout.imagecolor5.setImageResource(0);
                break;
            case 2:
                binding.includeLayout.imagecolor0.setImageResource(0);
                binding.includeLayout.imagecolor1.setImageResource(0);
                binding.includeLayout.imagecolor2.setImageResource(R.drawable.ic_done);
                binding.includeLayout.imagecolor3.setImageResource(0);
                binding.includeLayout.imagecolor4.setImageResource(0);
                binding.includeLayout.imagecolor5.setImageResource(0);
                break;
            case 1:
                binding.includeLayout.imagecolor0.setImageResource(0);
                binding.includeLayout.imagecolor1.setImageResource(R.drawable.ic_done);
                binding.includeLayout.imagecolor2.setImageResource(0);
                binding.includeLayout.imagecolor3.setImageResource(0);
                binding.includeLayout.imagecolor4.setImageResource(0);
                binding.includeLayout.imagecolor5.setImageResource(0);
                break;
            case 0:
                binding.includeLayout.imagecolor0.setImageResource(R.drawable.ic_done);
                binding.includeLayout.imagecolor1.setImageResource(0);
                binding.includeLayout.imagecolor2.setImageResource(0);
                binding.includeLayout.imagecolor3.setImageResource(0);
                binding.includeLayout.imagecolor4.setImageResource(0);
                binding.includeLayout.imagecolor5.setImageResource(0);
                break;
        }
    }

    private void showAlertUri() {

        if (alertDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(AddNoteActivity.this);
            View view = LayoutInflater.from(this).inflate(R.layout.layout_add_uri, findViewById(R.id.add_link_dialog));
            builder.setView(view);
            alertDialog = builder.show();
            if (alertDialog.getWindow() != null) {
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            final EditText editText = view.findViewById(R.id.edit_text_uri);
            editText.requestFocus();
            view.findViewById(R.id.add_text).setOnClickListener(view1 -> {
                if (editText.getText().toString().trim().isEmpty()) {
                    Toast.makeText(AddNoteActivity.this, "Enter Uri", Toast.LENGTH_SHORT).show();

                } else if (!Patterns.WEB_URL.matcher(editText.getText().toString()).matches()) {
                    Toast.makeText(AddNoteActivity.this, "Enter Valid Uri", Toast.LENGTH_SHORT).show();
                } else {
                    binding.uriText.setText(editText.getText().toString());
                    binding.uriTextLayout.setVisibility(View.VISIBLE);
                    alertDialog.dismiss();

                }

            });
            view.findViewById(R.id.cancel_text).setOnClickListener(view2 -> alertDialog.dismiss());
        }
    }

}


