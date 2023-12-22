package ru.tinkoff.hse.notes.exceptions;

public class NameNotFoundException extends RuntimeException {

    public NameNotFoundException(String message) {
        super(message);
    }
}
