package ru.tinkoff.hse.general;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.tinkoff.hse.notes.exceptions.AlreadyExistsException;
import ru.tinkoff.hse.notes.exceptions.AlreadyUsedTokenException;
import ru.tinkoff.hse.notes.exceptions.ExpiredTokenException;
import ru.tinkoff.hse.notes.exceptions.IllegalTokenKeyException;
import ru.tinkoff.hse.notes.exceptions.NameNotFoundException;
import ru.tinkoff.hse.notes.exceptions.PaginationException;
import ru.tinkoff.hse.notes.exceptions.RemovedNoteException;
import ru.tinkoff.hse.notes.exceptions.RequiredFieldsException;
import ru.tinkoff.hse.notes.exceptions.UUIDException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(e.getMessage());
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<String> handleUsernameNotFoundException(UsernameNotFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(e.getMessage());
    }

    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<String> handleAlreadyExistsException(AlreadyExistsException e) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(e.getMessage());
    }

    @ExceptionHandler(PaginationException.class)
    public ResponseEntity<String> handlePaginationException(PaginationException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(e.getMessage());
    }

    @ExceptionHandler(RequiredFieldsException.class)
    public ResponseEntity<String> handleRequiredFieldsException(RequiredFieldsException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(e.getMessage());
    }

    @ExceptionHandler(UUIDException.class)
    public ResponseEntity<String> handleUUIDException(UUIDException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(e.getMessage());
    }

    @ExceptionHandler(IllegalTokenKeyException.class)
    public ResponseEntity<String> handleIllegalTokenKeyException(IllegalTokenKeyException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(e.getMessage());
    }

    @ExceptionHandler(AlreadyUsedTokenException.class)
    public ResponseEntity<String> handleAlreadyUsedTokenException(AlreadyUsedTokenException e) {
        return ResponseEntity
                .status(HttpStatus.GONE)
                .body(e.getMessage());
    }

    @ExceptionHandler(ExpiredTokenException.class)
    public ResponseEntity<String> handleExpiredTokenException(ExpiredTokenException e) {
        return ResponseEntity
                .status(HttpStatus.GONE)
                .body(e.getMessage());
    }

    @ExceptionHandler(RemovedNoteException.class)
    public ResponseEntity<String> handleRemovedNoteException(RemovedNoteException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(e.getMessage());
    }

    @ExceptionHandler(NameNotFoundException.class)
    public ResponseEntity<String> handleNameNotFoundException(NameNotFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(e.getMessage());
    }
}
