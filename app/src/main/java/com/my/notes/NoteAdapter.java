package com.my.notes;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.my.notes.pojo.Note;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteHolder> {
    private List<Note> notes = new ArrayList<>();
    private OnItemClickListener listener;
    private Timer timer;
    private List<Note> sourceNotes;


    @NonNull
    @Override
    public NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note_item, parent, false);
        return new NoteHolder(itemView);
    }

    // convert from byte array to bitmap
    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    @Override
    public void onBindViewHolder(@NonNull NoteHolder holder, int position) {
        Note currentNote = notes.get(position);
        holder.textViewTitle.setText(currentNote.getTitle());
        holder.textViewdate.setText(currentNote.getDate());
        holder.textViewDescription.setText(currentNote.getDescription());
        holder.textViewPriority.setText(String.valueOf(currentNote.getPriority()));
        GradientDrawable gradientDrawable = (GradientDrawable) holder.itemView.getBackground();
        gradientDrawable.setColor(Color.parseColor(currentNote.getColor()));

        if (!currentNote.getUri().equals("")) {
            holder.textViewUri.setText(currentNote.getUri());
            holder.textViewUri.setVisibility(View.VISIBLE);
        } else {
            holder.textViewUri.setVisibility(View.GONE);
        }

        if (currentNote.getImage().length != 0) {
            Bitmap myBitmap = getImage(currentNote.getImage());
            holder.noteImage.setImageBitmap(myBitmap);
            holder.noteImage.setVisibility(View.VISIBLE);
        } else {
            holder.noteImage.setVisibility(View.GONE);
        }
    }

    public Note getNoteAt(int position) {
        return notes.get(position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(Note note);
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
        sourceNotes = notes;
        notifyDataSetChanged();
    }

    public void search(String s) {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (s.trim().isEmpty()) {
                    notes = sourceNotes;
                } else {
                    ArrayList<Note> temp = new ArrayList<Note>();
                    for (Note note : sourceNotes) {
                        if (note.getTitle().toLowerCase().contains(s.trim().toLowerCase())
                                || note.getDescription().toLowerCase().contains(s.trim().toLowerCase())) {
                            temp.add(note);
                        }

                    }
                    notes = temp;

                }
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });

            }
        }, 500);

    }

    public void cancelTimer() {
        if (timer != null) {
            timer.cancel();
        }
    }

    class NoteHolder extends RecyclerView.ViewHolder {
        private TextView textViewTitle;
        private TextView textViewdate;
        private RoundedImageView noteImage;
        private TextView textViewDescription;
        private TextView textViewUri;
        private TextView textViewPriority;

        public NoteHolder(View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.text_view_title);
            textViewUri = itemView.findViewById(R.id.itemUriText);
            textViewDescription = itemView.findViewById(R.id.text_view_description);
            textViewPriority = itemView.findViewById(R.id.text_view_priority);
            textViewdate = itemView.findViewById(R.id.text_view_date);
            noteImage = itemView.findViewById(R.id.noteImageView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(notes.get(position));
                    }
                }
            });
        }
    }
}
