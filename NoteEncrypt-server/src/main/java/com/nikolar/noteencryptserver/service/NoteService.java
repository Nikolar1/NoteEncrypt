package com.nikolar.noteencryptserver.service;

import com.nikolar.noteencryptserver.dto.NoteDTO;
import com.nikolar.noteencryptserver.mapper.NoteMapper;
import com.nikolar.noteencryptserver.repository.NoteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NoteService {
    private final NoteRepository noteRepository;
    private final NoteMapper noteMapper;
    public NoteService(NoteRepository noteRepository, NoteMapper noteMapper){
        this.noteMapper = noteMapper;
        this.noteRepository = noteRepository;
    }
    public List<NoteDTO> getAllNotes(){
        return noteMapper.entityToDto(noteRepository.findAll());
    }

    public void saveNotes(List<String> notesToSave){
        List<NoteDTO> existingNotes = getAllNotes();
        noteRepository.saveAll(
                notesToSave
                        .stream()
                        .map(note -> NoteDTO.fromString(note).orElse(null))
                        .filter(note -> note != null && !existingNotes.contains(note))
                        .map(noteMapper::dtoToEntity)
                        .collect(Collectors.toSet())
        );
    }
}
