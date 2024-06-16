package com.nikolar.noteencrypt;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.nikolar.noteencrypt.model.Note;
import com.nikolar.noteencrypt.retrofit.NoteApiService;
import com.nikolar.noteencrypt.retrofit.RetrofitClient;
import com.nikolar.noteencrypt.utils.EncryptionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ServerExchangeActivity extends AppCompatActivity {

    private static final Logger logger = Logger.getLogger("ServerExchangeActivity");
    private EditText editTextServerAddress;
    private NoteApiService noteApiService = null;

    private List<Note> notes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_exchange);
        Intent intent = getIntent();
        List<String> noteStrings = intent.getStringArrayListExtra("notes");
        if (noteStrings == null) {
            logger.log(Level.SEVERE, "Notes weren't passed");
            setResult(RESULT_CANCELED);
            finish();
        } else if (!noteStrings.isEmpty()) {
            notes = noteStrings.stream()
                    .map(Note::fromString)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());
        }else {
            notes = new ArrayList<>();
        }


        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if(notes != null){
                    Intent resultIntent = new Intent();
                    resultIntent.putStringArrayListExtra(
                            "notes",
                            notes.stream()
                                    .map(Note::toString)
                                    .collect(Collectors.toCollection(ArrayList::new))
                    );
                    setResult(RESULT_OK, resultIntent);
                    finish();
                }
            }
        });

        editTextServerAddress = findViewById(R.id.edit_text_server_address);
        Button buttonSendNotes = findViewById(R.id.button_send_notes);
        Button buttonRequestNotes = findViewById(R.id.button_request_notes);
        Button buttonSetAddress = findViewById(R.id.button_set_address);
        buttonSendNotes.setActivated(false);
        buttonRequestNotes.setActivated(false);
        buttonSetAddress.setOnClickListener(v -> {
            String serverAddress = editTextServerAddress.getText().toString();
            Optional<Retrofit> client = RetrofitClient.getClient(serverAddress);
            if (client.isPresent()) {
                noteApiService = client.get().create(NoteApiService.class);
                buttonSendNotes.setActivated(true);
                buttonRequestNotes.setActivated(true);
            }else {
                Toast.makeText(ServerExchangeActivity.this, "Error while setting address", Toast.LENGTH_SHORT).show();
                logger.log(Level.SEVERE, "Failed to set address: " + serverAddress);
            }
        });

        buttonSendNotes.setOnClickListener(v -> {
            if (noteApiService != null) {
                Call<String> call = noteApiService.saveNotes(notes.stream().map(Note::toString).collect(Collectors.toList()));
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (response.isSuccessful()){
                            Toast.makeText(ServerExchangeActivity.this, "Saved notes successfully", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(ServerExchangeActivity.this, "Failed to save notes", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable throwable) {
                        Toast.makeText(ServerExchangeActivity.this, "Failed to save notes", Toast.LENGTH_SHORT).show();
                        logger.log(Level.SEVERE, "Failed to save notes.", throwable);
                    }
                });
            }else {
                Toast.makeText(ServerExchangeActivity.this, "Address not set", Toast.LENGTH_SHORT).show();
            }
        });

        buttonRequestNotes.setOnClickListener(v -> {
            if (noteApiService != null) {
                Call<List<String>> call = noteApiService.getNotes();
                call.enqueue(new Callback<List<String>>() {
                    @Override
                    public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                        if (response.isSuccessful() && response.body() != null){
                            notes.addAll(
                                    response.body()
                                            .stream()
                                            .map(Note::fromString)
                                            .filter(Optional::isPresent)
                                            .map(Optional::get)
                                            .filter(note -> !notes.contains(note))
                                            .collect(Collectors.toList())
                            );
                            Toast.makeText(ServerExchangeActivity.this, "Notes retried", Toast.LENGTH_SHORT).show();
                        }else
                            Toast.makeText(ServerExchangeActivity.this, "Failed to retrieve notes.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<List<String>> call, Throwable throwable) {
                        Toast.makeText(ServerExchangeActivity.this, "Failed to retrieve notes.", Toast.LENGTH_SHORT).show();
                        logger.log(Level.SEVERE, "Failed to retrieve notes.", throwable);
                    }
                });
            }else {
                Toast.makeText(ServerExchangeActivity.this, "Address not set", Toast.LENGTH_SHORT).show();
            }
        });
    }

}