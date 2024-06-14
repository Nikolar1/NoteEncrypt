package com.nikolar.noteencrypt;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.nikolar.noteencrypt.utils.FileUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainActivity extends AppCompatActivity {
    private Map<NoteActions, ActivityResultLauncher<Intent>> activityResultLauncher;
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

        activityResultLauncher= new HashMap<>();
        activityResultLauncher.put(NoteActions.NEW, registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    handleNoteData(NoteActions.NEW, result.getResultCode(), result.getData());
                })
        );
        activityResultLauncher.put(NoteActions.EDIT, registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    handleNoteData(NoteActions.EDIT, result.getResultCode(), result.getData());
                })
        );

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
        logger.info("Setting listener for new note button");
        Button buttonNewNote = findViewById(R.id.button_new_note);
        buttonNewNote.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, NoteActivity.class);
            launchActivity(intent, NoteActions.NEW);
        });
        logger.info("Setting listener for notes list.");
        listViewNotes.setOnItemClickListener((parent, view, position, id) -> {
            String title = noteTitles.get(position);
            String body = notes.get(title);

            Intent intent = new Intent(MainActivity.this, NoteActivity.class);
            intent.putExtra("title", title);
            intent.putExtra("body", body);
            launchActivity(intent, NoteActions.EDIT);
        });
    }

    private void launchActivity(Intent intent, NoteActions action){
        logger.info("Launching activity with action: "
                + (action == NoteActions.NEW ? "NEW" : action == NoteActions.EDIT ? "EDIT" : "Unknown") );

        ActivityResultLauncher<Intent> arl = activityResultLauncher.get(action);
        if (arl != null)
            arl.launch(intent);
        else
            logger.log(Level.SEVERE, "Could find activityResultLauncher for requested action.");
    }

    private void handleNoteData(NoteActions action, int resultCode, Intent data) {
        logger.info("Handling NoteActivity results");
        if (resultCode == RESULT_OK && data != null) {
            String title = data.getStringExtra("title");
            String body = data.getStringExtra("body");

            if (action == NoteActions.NEW) {
                logger.info("Adding new note");
                notes.put(title, body);
                noteTitles.add(title);
            } else if (action == NoteActions.EDIT) {
                logger.info("Replacing edited note");
                String oldTitle = data.getStringExtra("oldTitle");
                if (oldTitle != null && !oldTitle.equals(title)) {
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
