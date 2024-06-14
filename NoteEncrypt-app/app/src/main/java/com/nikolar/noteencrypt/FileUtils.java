package com.nikolar.noteencrypt;

import android.content.Context;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;

public class FileUtils {

    private static final String FILE_NAME = "notes.txt";

    public static void saveNotes(Context context, HashMap<String, String> notes) throws IOException {
        FileOutputStream fos = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos));

        for (HashMap.Entry<String, String> entry : notes.entrySet()) {
            writer.write(entry.getKey() + "\n" + entry.getValue() + "\n---\n");
        }

        writer.close();
    }

    public static HashMap<String, String> loadNotes(Context context) throws IOException {
        HashMap<String, String> notes = new HashMap<>();
        FileInputStream fis = context.openFileInput(FILE_NAME);
        BufferedReader reader = new BufferedReader(new InputStreamReader(fis));

        String line;
        String title = null;
        StringBuilder body = new StringBuilder();

        while ((line = reader.readLine()) != null) {
            if (line.equals("---")) {
                notes.put(title, body.toString());
                title = null;
                body = new StringBuilder();
            } else if (title == null) {
                title = line;
            } else {
                if (body.length() > 0) {
                    body.append("\n");
                }
                body.append(line);
            }
        }

        reader.close();
        return notes;
    }
}
