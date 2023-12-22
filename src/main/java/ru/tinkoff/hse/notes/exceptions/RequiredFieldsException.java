package ru.tinkoff.hse.notes.exceptions;

public class RequiredFieldsException extends RuntimeException {

    public RequiredFieldsException(String message) {
        super(message);
    }
}
