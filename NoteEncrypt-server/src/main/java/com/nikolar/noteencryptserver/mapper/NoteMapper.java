package com.nikolar.noteencryptserver.mapper;

import com.nikolar.noteencryptserver.dto.NoteDTO;
import com.nikolar.noteencryptserver.model.Note;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@NoArgsConstructor
public class NoteMapper {
    public Note dtoToEntity(NoteDTO dto){
        if(dto == null){
            return null;
        }
        Note entity = new Note();
        entity.setId(dto.getId());
        entity.setBody(dto.getBody());
        entity.setTitle(dto.getTitle());
        entity.setModifiedOn(dto.getModifiedOn());
        return entity;
    }

    public List<Note> dtoToEntity(List<NoteDTO> dtoList){
        if(dtoList == null){
            return null;
        }
        if (dtoList.isEmpty()){
            return new LinkedList<Note>();
        }
        return dtoList
                .stream()
                .map(this::dtoToEntity)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    public NoteDTO entityToDto(Note entity){
        if(entity == null){
            return null;
        }
        NoteDTO dto = new NoteDTO();
        dto.setId(entity.getId());
        dto.setBody(entity.getBody());
        dto.setTitle(entity.getTitle());
        dto.setModifiedOn(entity.getModifiedOn());
        return dto;
    }

    public List<NoteDTO> entityToDto(List<Note> entityList){
        if(entityList == null){
            return null;
        }
        if (entityList.isEmpty()){
            return new LinkedList<NoteDTO>();
        }
        return entityList
                .stream()
                .map(this::entityToDto)
                .collect(Collectors.toCollection(LinkedList::new));
    }
}
