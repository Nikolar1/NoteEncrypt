package com.nikolar.noteencryptserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

import static com.nikolar.noteencryptserver.config.Consts.DELIMITER;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NoteDTO {
    private Long id;
    private String title;
    private String body;
    private LocalDateTime modifiedOn;

    private NoteDTO(String title, String body, LocalDateTime modifiedOn) {
        this.title = title;
        this.body = body;
        this.modifiedOn = modifiedOn;
    }

    public NoteDTO(String title, String body) {
        this(title, body, LocalDateTime.now());
    }

    public static Optional<NoteDTO> fromString(String noteString){
        if (noteString == null)
            return Optional.empty();
        String[] splitNote = noteString.split(DELIMITER);
        if(splitNote.length == 3){
            return Optional.of(new NoteDTO(splitNote[0], splitNote[1], LocalDateTime.parse(splitNote[2])));
        }
        return Optional.empty();
    }

    @Override
    public String toString(){
        return title + DELIMITER + body + DELIMITER + modifiedOn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NoteDTO noteDTO = (NoteDTO) o;
        return Objects.equals(title, noteDTO.title) && Objects.equals(body, noteDTO.body);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, body);
    }
}
