package ru.tinkoff.hse.notes.exceptions;

public class AlreadyUsedTokenException extends RuntimeException {

    public AlreadyUsedTokenException(String message) {
        super(message);
    }
}
