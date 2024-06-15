package com.nikolar.noteencrypt.model;

import static com.nikolar.noteencrypt.config.Consts.DELIMITER;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Optional;

public class Note {
    private String title;
    private String body;
    private LocalDateTime modifiedOn;

    private Note(String title, String body, LocalDateTime modifiedOn) {
        this.title = title;
        this.body = body;
        this.modifiedOn = modifiedOn;
    }

    public Note(String title, String body) {
        this(title, body, LocalDateTime.now());
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        modifiedOn = LocalDateTime.now();
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
        modifiedOn = LocalDateTime.now();
    }

    public LocalDateTime getModifiedOn() {
        return modifiedOn;
    }

    public static Optional<Note> fromString(@Nullable String noteString){
        if (noteString == null)
            return Optional.empty();
        String[] splitNote = noteString.split(DELIMITER);
        if(splitNote.length == 3){
            return Optional.of(new Note(splitNote[0], splitNote[1], LocalDateTime.parse(splitNote[2])));
        }
        return Optional.empty();
    }

    @NonNull
    @Override
    public String toString(){
        return title + DELIMITER + body + DELIMITER + modifiedOn;
    }

}
