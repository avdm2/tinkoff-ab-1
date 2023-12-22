package ru.tinkoff.hse.notes.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.tinkoff.hse.notes.dto.notes.CreateNoteDto;
import ru.tinkoff.hse.notes.dto.notes.GetNotesDto;
import ru.tinkoff.hse.notes.dto.notes.ResponseNoteDto;
import ru.tinkoff.hse.notes.dto.notes.UpdateNoteDto;
import ru.tinkoff.hse.notes.entities.NoteToken;
import ru.tinkoff.hse.notes.mappers.NoteMapper;
import ru.tinkoff.hse.notes.entities.Note;
import ru.tinkoff.hse.notes.services.NoteService;
import ru.tinkoff.hse.notes.services.NoteTokenService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/notes")
public class NoteController {

    private final NoteService noteService;
    private final NoteTokenService noteTokenService;

    public NoteController(NoteService noteService, NoteTokenService noteTokenService) {
        this.noteService = noteService;
        this.noteTokenService = noteTokenService;
    }

    @PostMapping("/create")
    public ResponseEntity<ResponseNoteDto> createNote(@RequestBody CreateNoteDto createNoteDto,
                                                      @AuthenticationPrincipal UserDetails userDetails) {
        String currentUser = userDetails.getUsername();

        Note createdNote = noteService.createNote(createNoteDto, currentUser);
        return ResponseEntity.ok().body(NoteMapper.toResponseNoteDto(createdNote));
    }

    @GetMapping("/list")
    public ResponseEntity<List<GetNotesDto>> getNotes(@RequestParam(required = false) Integer pageSize,
                                                      @RequestParam(required = false) Integer pageNumber,
                                                      @AuthenticationPrincipal UserDetails userDetails) {
        String currentUser = userDetails.getUsername();

        if (pageSize == null || pageNumber == null) {
            return ResponseEntity.ok().body(NoteMapper.toListGetNotesDto(noteService.getAllNotes(currentUser)));
        }

        List<Note> notes = noteService.getNotes(pageSize, pageNumber, currentUser);
        return ResponseEntity.ok().body(NoteMapper.toListGetNotesDto(notes));
    }

    @GetMapping("/list/{directoryUUID}")
    public ResponseEntity<List<GetNotesDto>> getNotesInDirectory(@RequestParam(required = false) Integer pageSize,
                                                                 @RequestParam(required = false) Integer pageNumber,
                                                                 @PathVariable UUID directoryUUID,
                                                                 @AuthenticationPrincipal UserDetails userDetails) {
        String currentUser = userDetails.getUsername();

        if (pageSize == null || pageNumber == null) {
            return ResponseEntity.ok().body(NoteMapper.toListGetNotesDto(noteService.getAllNotesInDirectory(directoryUUID, currentUser)));
        }

        List<Note> notes = noteService.getNotesInDirectory(pageSize, pageNumber, directoryUUID, currentUser);
        return ResponseEntity.ok().body(NoteMapper.toListGetNotesDto(notes));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Note> getNote(@PathVariable UUID id,
                                        @AuthenticationPrincipal UserDetails userDetails) {
        String currentUser = userDetails.getUsername();

        Note searchedNote = noteService.getNote(id, currentUser);
        return ResponseEntity.ok().body(searchedNote);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseNoteDto> updateNote(@PathVariable UUID id,
                                                      @RequestBody UpdateNoteDto noteDto,
                                                      @AuthenticationPrincipal UserDetails userDetails) {
        String currentUser = userDetails.getUsername();

        Note updatedNote = noteService.updateNote(id, noteDto, currentUser);
        return ResponseEntity.ok().body(NoteMapper.toResponseNoteDto(updatedNote));
    }

    @PostMapping("/tokens/{noteUUID}")
    public ResponseEntity<NoteToken> createNoteToken(@PathVariable UUID noteUUID,
                                                     @AuthenticationPrincipal UserDetails userDetails) {
        String currentUser = userDetails.getUsername();

        NoteToken noteToken = noteTokenService.createToken(noteUUID, currentUser);
        return ResponseEntity.ok().body(noteToken);
    }

    @GetMapping("/tokens/{key}")
    public ResponseEntity<Note> getNoteByTokenKey(@PathVariable String key,
                                                  @AuthenticationPrincipal UserDetails userDetails) {
        String currentUser = userDetails.getUsername();

        Note note = noteTokenService.getNoteByTokenKey(key, currentUser);
        return ResponseEntity.ok().body(note);
    }
}
