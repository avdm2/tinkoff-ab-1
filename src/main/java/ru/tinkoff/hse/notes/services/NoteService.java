package ru.tinkoff.hse.notes.services;

import org.springframework.stereotype.Service;
import ru.tinkoff.hse.notes.dto.notes.CreateNoteDto;
import ru.tinkoff.hse.notes.dto.notes.UpdateNoteDto;
import ru.tinkoff.hse.notes.exceptions.AlreadyExistsException;
import ru.tinkoff.hse.notes.exceptions.NameNotFoundException;
import ru.tinkoff.hse.notes.exceptions.PaginationException;
import ru.tinkoff.hse.notes.exceptions.RequiredFieldsException;
import ru.tinkoff.hse.notes.exceptions.UUIDException;
import ru.tinkoff.hse.notes.mappers.NoteMapper;
import ru.tinkoff.hse.notes.entities.Note;
import ru.tinkoff.hse.notes.repositories.DirectoryRepository;
import ru.tinkoff.hse.notes.repositories.NoteRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class NoteService {

    private final NoteRepository noteRepository;
    private final DirectoryRepository directoryRepository;

    public NoteService(NoteRepository noteRepository, DirectoryRepository directoryRepository) {
        this.noteRepository = noteRepository;
        this.directoryRepository = directoryRepository;
    }

    public Note createNote(CreateNoteDto note, String username) throws UUIDException, AlreadyExistsException,
            RequiredFieldsException {
        UUID directoryUUID = note.getDirectoryUUID();
        if (directoryUUID == null) {
            directoryUUID = directoryRepository.findByNameAndDirectoryOwner("root", username).get().getId();
        } else if (directoryRepository.findByIdAndDirectoryOwner(directoryUUID, username).isEmpty()) {
            throw new UUIDException("No directory with such UUID were found.");
        }

        String noteName = note.getName();
        if (noteRepository.getNoteByName(noteName).isPresent()) {
            throw new AlreadyExistsException("A note by that name already exists.");
        }

        String notePassword = note.getPassword();
        if (noteName == null || noteName.isEmpty() || notePassword == null || notePassword.isEmpty()) {
            throw new RequiredFieldsException("Check body for all required fields.");
        }

        String login = note.getLogin();
        if (login == null || login.isEmpty()) {
            login = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss"));
        }

        String url = note.getUrl();
        if (url == null || url.isEmpty()) {
            url = login + ".com";
        }

        note.setLogin(login).setUrl(url).setDirectoryUUID(directoryUUID);

        return noteRepository.save(NoteMapper.fromCreateNoteDto(note, username));
    }

    public List<Note> getAllNotes(String username) {
        return noteRepository.findAllByNoteOwner(username);
    }

    public List<Note> getAllNotesInDirectory(UUID directoryUUID, String username) {
        return noteRepository.findAllByNoteOwnerAndDirectoryUUID(username, directoryUUID);
    }

    public List<Note> getNotes(int pageSize, int pageNumber, String username) throws PaginationException {
        if (pageSize < 1 || pageNumber < 1) {
            throw new PaginationException("Check amount for correctness.");
        }

        return getAllNotes(username).stream()
                .skip((long) (pageNumber - 1) * pageSize)
                .limit(pageSize)
                .collect(Collectors.toList());
    }

    public List<Note> getNotesInDirectory(int pageSize, int pageNumber, UUID directoryUUID, String username)
            throws PaginationException, AlreadyExistsException {
        if (pageSize < 1 || pageNumber < 1) {
            throw new PaginationException("Check amount for correctness.");
        }

        if (directoryUUID == null) {
            directoryUUID = directoryRepository.findByNameAndDirectoryOwner("root", username).get().getId();
        } else if (directoryRepository.findByIdAndDirectoryOwner(directoryUUID, username).isEmpty()) {
            throw new NameNotFoundException("No directory with such name were found.");
        }

        return getAllNotesInDirectory(directoryUUID, username).stream()
                .skip((long) (pageNumber - 1) * pageSize)
                .limit(pageSize)
                .collect(Collectors.toList());
    }

    public Note getNote(UUID id, String username) throws UUIDException {
        Optional<Note> note = noteRepository.findById(id);
        if (note.isPresent() && note.get().getNoteOwner().equals(username)) {
            return note.get();
        }
        throw new UUIDException("No note for this UUID.");
    }

    public Note updateNote(UUID id, UpdateNoteDto noteDto, String username) throws UUIDException, NameNotFoundException {
        Note note = getNote(id, username);

        if (noteDto.getName() != null) {
            note.setName(noteDto.getName());
        }
        if (noteDto.getLogin() != null) {
            note.setLogin(noteDto.getLogin());
        }
        if (noteDto.getPassword() != null) {
            note.setPassword(noteDto.getPassword());
        }
        if (noteDto.getUrl() != null) {
            note.setUrl(noteDto.getUrl());
        }

        UUID newDirectoryUUID = noteDto.getDirectoryUUID();
        if (newDirectoryUUID != null) {
            if (directoryRepository.findByIdAndDirectoryOwner(newDirectoryUUID, username).isEmpty()) {
                throw new NameNotFoundException("No directory with such UUID were found.");
            }

            note.setDirectoryUUID(newDirectoryUUID);
        }

        return noteRepository.save(note);
    }
}
