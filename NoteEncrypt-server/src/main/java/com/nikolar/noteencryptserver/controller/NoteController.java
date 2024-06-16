package com.nikolar.noteencryptserver.controller;

import com.nikolar.noteencryptserver.dto.NoteDTO;
import com.nikolar.noteencryptserver.service.NoteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RestController
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.OPTIONS, RequestMethod.POST})
public class NoteController {
    private final NoteService noteService;
    public  NoteController(NoteService noteService){
        this.noteService = noteService;
    }
    @GetMapping("/notes")
    public ResponseEntity<?> getNotes(){
        List<NoteDTO> result = noteService.getAllNotes();
        return new ResponseEntity<>(result.stream().map(NoteDTO::toString).collect(Collectors.toSet()), HttpStatus.OK);
    }
    @PostMapping("/saveNotes")
    public ResponseEntity<?> saveNotes(@RequestBody List<String> notes){
        noteService.saveNotes(notes);
        return new ResponseEntity<>("Saved", HttpStatus.OK);
    }
}
