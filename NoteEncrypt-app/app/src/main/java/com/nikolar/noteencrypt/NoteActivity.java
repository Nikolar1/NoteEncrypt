package com.nikolar.noteencrypt;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.util.logging.Logger;

public class NoteActivity extends AppCompatActivity {

    private EditText editTextTitle;
    private EditText editTextBody;
    private String oldTitle;
    private Logger logger = Logger.getLogger("Note_Activity");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        editTextTitle = findViewById(R.id.edit_text_title);
        editTextBody = findViewById(R.id.edit_text_body);
        Button buttonSave = findViewById(R.id.button_save);

        Intent intent = getIntent();
        logger.info("Checking if there is old note data to display");
        if (intent.hasExtra("title") && intent.hasExtra("body")) {
            oldTitle = intent.getStringExtra("title");
            editTextTitle.setText(oldTitle);
            editTextBody.setText(intent.getStringExtra("body"));
        }

        buttonSave.setOnClickListener(v -> {
            logger.info("Save button clicked closing activity and returning result");
            String title = editTextTitle.getText().toString();
            String body = editTextBody.getText().toString();

            Intent resultIntent = new Intent();
            resultIntent.putExtra("title", title);
            resultIntent.putExtra("body", body);
            if (oldTitle != null) {
                resultIntent.putExtra("oldTitle", oldTitle);
            }
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }
}
