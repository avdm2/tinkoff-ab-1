package ru.tinkoff.hse.notes.mappers;

import ru.tinkoff.hse.notes.dto.notes.CreateNoteDto;
import ru.tinkoff.hse.notes.dto.notes.GetNotesDto;
import ru.tinkoff.hse.notes.dto.notes.ResponseNoteDto;
import ru.tinkoff.hse.notes.entities.Note;

import java.util.List;

public class NoteMapper {

    public static GetNotesDto toGetNotesDto(Note note) {
        return new GetNotesDto().setId(note.getId())
                .setName(note.getName());
    }

    public static ResponseNoteDto toResponseNoteDto(Note note) {
        return new ResponseNoteDto().setName(note.getName())
                .setLogin(note.getLogin()).setPassword(note.getPassword())
                .setUrl(note.getUrl()).setNoteOwner(note.getNoteOwner())
                .setDirectoryUUID(note.getDirectoryUUID());
    }

    public static Note fromCreateNoteDto(CreateNoteDto dto, String username) {
        return new Note().setName(dto.getName()).setLogin(dto.getLogin())
                .setPassword(dto.getPassword()).setUrl(dto.getUrl())
                .setNoteOwner(username).setDirectoryUUID(dto.getDirectoryUUID());
    }

    public static List<GetNotesDto> toListGetNotesDto(List<Note> notes) {
        return notes.stream().map(NoteMapper::toGetNotesDto).toList();
    }
}
