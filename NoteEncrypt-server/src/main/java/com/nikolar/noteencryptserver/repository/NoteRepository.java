package com.nikolar.noteencryptserver.repository;

import com.nikolar.noteencryptserver.model.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {
}
