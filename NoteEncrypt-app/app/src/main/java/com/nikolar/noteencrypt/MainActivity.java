package com.nikolar.noteencrypt;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.nikolar.noteencrypt.model.Note;
import com.nikolar.noteencrypt.utils.FileUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {
    private Map<NoteActions, ActivityResultLauncher<Intent>> activityResultLauncher;
    private List<Note> notes;
    private List<String> noteTitles;
    private ArrayAdapter<String> adapter;
    private Logger logger = Logger.getLogger("Main_Activity");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
            notes.sort(Comparator.comparing(Note::getModifiedOn).reversed());
        } catch (FileNotFoundException e){
          notes = new LinkedList<>();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to load notes", e);
        } catch (InvalidAlgorithmParameterException|InvalidKeyException e){
            logger.log(Level.SEVERE, "Encountered an cipher exception while loading notes", e);
        }
        noteTitles = notes.stream()
                .map(Note::getTitle)
                .collect(Collectors.toList());
        ListView listViewNotes = findViewById(R.id.list_view_notes);
        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                noteTitles
        );
        listViewNotes.setAdapter(adapter);
        logger.info("Setting listener for new note button");
        Button buttonNewNote = findViewById(R.id.button_new_note);
        buttonNewNote.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, NoteActivity.class);
            launchActivity(intent, NoteActions.NEW);
        });
        logger.info("Setting listener for notes list.");
        listViewNotes.setOnItemClickListener((parent, view, position, id) -> {
            Note note = notes.get(position);

            Intent intent = new Intent(MainActivity.this, NoteActivity.class);
            intent.putExtra("note", note.toString());
            intent.putExtra("position", position);
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
        if (resultCode == RESULT_OK && data != null && data.hasExtra("note")) {
            Optional<Note> note = Note.fromString(data.getStringExtra("note"));
            if (note.isPresent()) {
                if (action == NoteActions.NEW) {
                    logger.info("Adding new note");
                    notes.add(note.get());
                } else if (action == NoteActions.EDIT) {
                    logger.info("Replacing edited note");
                    int position = data.getIntExtra("position",-1);
                    if (position  != -1) {
                        notes.remove(position);
                        notes.add(note.get());
                    } else {
                        logger.log(Level.WARNING, "No position data was found adding as new note");
                        notes.add(note.get());
                    }
                }
                logger.info("Saving notes");
                FileUtils.saveNotes(this, notes);
                notes.sort(Comparator.comparing(Note::getModifiedOn).reversed());
                noteTitles.clear();
                noteTitles.addAll(
                        notes.stream()
                                .map(Note::getTitle)
                                .collect(Collectors.toList())
                );
                adapter.notifyDataSetChanged();

            }else {
                logger.log(Level.SEVERE, "Note data is missing");
            }
        }else {
            logger.log(Level.SEVERE, "Something went wrong when returning from note edit, " + resultCode);
        }
    }
}
