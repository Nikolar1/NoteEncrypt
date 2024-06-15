package com.nikolar.noteencrypt;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.nikolar.noteencrypt.utils.EncryptionUtils;

import java.util.logging.Level;
import java.util.logging.Logger;

public class PasswordActivity extends AppCompatActivity {

    private static Logger logger = Logger.getLogger("PasswordActivity");
    private EditText editTextPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        editTextPassword = findViewById(R.id.edit_text_password);
        Button buttonConfirm = findViewById(R.id.button_confirm);

        buttonConfirm.setOnClickListener(v -> {
            String enteredPassword = editTextPassword.getText().toString();
            try {
                if (EncryptionUtils.setPassword(this, enteredPassword)) {
                    Intent intent = new Intent(PasswordActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    Toast.makeText(PasswordActivity.this, "Wrong password", Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
                Toast.makeText(PasswordActivity.this, "Internal error, please try again.", Toast.LENGTH_SHORT).show();
                logger.log(Level.SEVERE, "Error checking password", e);

            }
        });
    }
}
