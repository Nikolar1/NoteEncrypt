package com.nikolar.noteencrypt;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.nikolar.noteencrypt.model.Note;

import java.util.Optional;
import java.util.logging.Logger;

public class NoteActivity extends AppCompatActivity {

    private EditText editTextTitle;
    private EditText editTextBody;
    private int position = -1;
    private Note note;
    private Logger logger = Logger.getLogger("Note_Activity");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        note = new Note("","");
        editTextTitle = findViewById(R.id.edit_text_title);
        editTextBody = findViewById(R.id.edit_text_body);
        Button buttonSave = findViewById(R.id.button_save);
        Button buttonDelete = findViewById(R.id.button_delete);

        Intent intent = getIntent();
        logger.info("Checking if there is old note data to display");
        if (intent.hasExtra("note") && intent.hasExtra("position")) {
            position = intent.getIntExtra("position", -1);
            Optional<Note> noteOption = Note.fromString(intent.getStringExtra("note"));
            if (noteOption.isPresent()) {
                editTextTitle.setText(noteOption.get().getTitle());
                editTextBody.setText(noteOption.get().getBody());
            }else{
                position = -1;
            }
        }

        buttonDelete.setOnClickListener(v -> {
            logger.info("Delete button clicked closing activity and returning result");
            Intent resultIntent = new Intent();

            if (position != -1) {
                resultIntent.putExtra("position", position);
                resultIntent.putExtra("deleteNote", true);
                setResult(RESULT_OK, resultIntent);
                finish();
            }else {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        buttonSave.setOnClickListener(v -> {
            logger.info("Save button clicked closing activity and returning result");
            note.setTitle(editTextTitle.getText().toString());
            note.setBody(editTextBody.getText().toString());
            Intent resultIntent = new Intent();
            resultIntent.putExtra("note", note.toString());

            if (position != -1) {
                resultIntent.putExtra("position", position);
            }
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }
}
