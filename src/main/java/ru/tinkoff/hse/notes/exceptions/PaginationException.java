package ru.tinkoff.hse.notes.exceptions;

public class PaginationException extends RuntimeException {

    public PaginationException(String message) {
        super(message);
    }
}
