package com.nikolar.noteencrypt;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_NEW_NOTE = 1;
    private static final int REQUEST_CODE_EDIT_NOTE = 2;

    private ArrayList<String> noteTitles;
    private HashMap<String, String> notes;
    private ArrayAdapter<String> adapter;
    private Logger logger = Logger.getLogger("Main_Activity");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        noteTitles = new ArrayList<>();
        notes = new HashMap<>();

        try {
            logger.info("Loading notes...");
            notes = FileUtils.loadNotes(this);
            noteTitles.addAll(notes.keySet());
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to load notes", e);
        }

        ListView listViewNotes = findViewById(R.id.list_view_notes);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, noteTitles);
        listViewNotes.setAdapter(adapter);

        Button buttonNewNote = findViewById(R.id.button_new_note);
        buttonNewNote.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, NoteActivity.class);
            startActivityForResult(intent, REQUEST_CODE_NEW_NOTE);
        });

        listViewNotes.setOnItemClickListener((parent, view, position, id) -> {
            String title = noteTitles.get(position);
            String body = notes.get(title);

            Intent intent = new Intent(MainActivity.this, NoteActivity.class);
            intent.putExtra("title", title);
            intent.putExtra("body", body);
            startActivityForResult(intent, REQUEST_CODE_EDIT_NOTE);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            String title = data.getStringExtra("title");
            String body = data.getStringExtra("body");

            if (requestCode == REQUEST_CODE_NEW_NOTE) {
                notes.put(title, body);
                noteTitles.add(title);
            } else if (requestCode == REQUEST_CODE_EDIT_NOTE) {
                String oldTitle = data.getStringExtra("oldTitle");
                if (!oldTitle.equals(title)) {
                    notes.remove(oldTitle);
                    noteTitles.remove(oldTitle);
                    notes.put(title, body);
                    noteTitles.add(title);
                } else {
                    notes.put(title, body);
                }
            }

            try {
                logger.info("Saving notes");
                FileUtils.saveNotes(this, notes);
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Encountered error while saving file", e);
            }

            adapter.notifyDataSetChanged();
        }
    }
}
