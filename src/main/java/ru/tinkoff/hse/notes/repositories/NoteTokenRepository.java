package ru.tinkoff.hse.notes.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ru.tinkoff.hse.notes.entities.NoteToken;

import java.util.Optional;
import java.util.UUID;

public interface NoteTokenRepository extends JpaRepository<NoteToken, UUID> {

    Optional<NoteToken> findByKey(String key);

    @Transactional
    @Modifying
    @Query("DELETE FROM NoteToken t WHERE t.expiresAt < CURRENT_TIMESTAMP OR t.isUsed = true")
    int deleteExpiredOrUsedTokens();
}
