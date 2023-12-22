package ru.tinkoff.hse.notes.services;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.tinkoff.hse.notes.entities.Note;
import ru.tinkoff.hse.notes.entities.NoteToken;
import ru.tinkoff.hse.notes.exceptions.AlreadyUsedTokenException;
import ru.tinkoff.hse.notes.exceptions.ExpiredTokenException;
import ru.tinkoff.hse.notes.exceptions.IllegalTokenKeyException;
import ru.tinkoff.hse.notes.exceptions.RemovedNoteException;
import ru.tinkoff.hse.notes.exceptions.UUIDException;
import ru.tinkoff.hse.notes.repositories.NoteRepository;
import ru.tinkoff.hse.notes.repositories.NoteTokenRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class NoteTokenService {

    @Value("${token.notes.generation-key}")
    private String generationKey;

    @Value("${token.notes.duration-minutes}")
    private long tokenDurationMinutes;

    private final Map<String, Long> tokenCreationTimes = new HashMap<>();

    private final NoteTokenRepository noteTokenRepository;
    private final NoteRepository noteRepository;
    private final MeterRegistry meterRegistry;

    public NoteTokenService(NoteTokenRepository noteTokenRepository, NoteRepository noteRepository, MeterRegistry meterRegistry) {
        this.noteTokenRepository = noteTokenRepository;
        this.noteRepository = noteRepository;
        this.meterRegistry = meterRegistry;
    }

    public NoteToken createToken(UUID noteId, String username) throws UUIDException {
        Optional<Note> note = noteRepository.findById(noteId);
        if (note.isEmpty() || !note.get().getNoteOwner().equals(username)) {
            throw new UUIDException("No note for this UUID.");
        }

        NoteToken noteToken = new NoteToken()
                .setKey(generateKey())
                .setNoteId(noteId)
                .setOwner(username)
                .setExpiresAt(LocalDateTime.now().plusMinutes(tokenDurationMinutes));
        noteTokenRepository.save(noteToken);

        tokenCreationTimes.put(noteToken.getKey(), System.nanoTime());
        return noteToken;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Note getNoteByTokenKey(String key, String newOwner)
            throws IllegalTokenKeyException, AlreadyUsedTokenException, ExpiredTokenException, RemovedNoteException {
        Optional<NoteToken> noteTokenOptional = noteTokenRepository.findByKey(key);
        if (noteTokenOptional.isEmpty()) {
            meterRegistry.counter("total_failed_token_accesses").increment();
            meterRegistry.counter("invalid_token_accesses").increment();

            throw new IllegalTokenKeyException("No token found with such key. Probable cause: it might be already used or expired.");
        }

        NoteToken noteToken = noteTokenOptional.get();
        if (noteToken.isUsed()) {
            meterRegistry.counter("total_failed_token_accesses").increment();
            meterRegistry.counter("used_token_accesses").increment();

            throw new AlreadyUsedTokenException("This token has already been used by another user.");
        }
        if (noteToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            meterRegistry.counter("total_failed_token_accesses").increment();
            meterRegistry.counter("expired_token_accesses").increment();

            throw new ExpiredTokenException("This token has expired.");
        }

        long creationTime = tokenCreationTimes.getOrDefault(noteToken.getKey(), System.nanoTime());
        meterRegistry.timer("token_usage_time").record(System.nanoTime() - creationTime, TimeUnit.NANOSECONDS);

        noteToken.setUsed(true);
        noteTokenRepository.save(noteToken);

        Optional<Note> optionalNote = noteRepository.findById(noteToken.getNoteId());
        if (optionalNote.isEmpty()) {
            // Этот кейс я решил не считать за "обращение к невалидному токену", поскольку
            // на самом деле токен существует. Не существует лишь запись, на которую первый ссылается.
            throw new RemovedNoteException("This note has been deleted by author.");
        }
        Note note = optionalNote.get();

        noteRepository.save(new Note()
                .setName(note.getName())
                .setLogin(note.getLogin())
                .setPassword(note.getPassword())
                .setUrl(note.getUrl())
                .setNoteOwner(newOwner));

        return note;
    }

    private String generateKey() {
        String source = UUID.randomUUID() + generationKey;
        List<String> list = Arrays.asList(source.split(""));
        Collections.shuffle(list);
        return list.stream().reduce(String.valueOf(0), (first, second) -> first + second);
    }

    @Scheduled(fixedRateString = "#{${token.notes.cleanup-minutes} * 60 * 1000}")
    public void cleanupExpiredTokens() {
        noteTokenRepository.deleteExpiredOrUsedTokens();
    }
}
