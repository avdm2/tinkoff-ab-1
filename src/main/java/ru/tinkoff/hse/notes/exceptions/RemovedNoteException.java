package ru.tinkoff.hse.notes.exceptions;

public class RemovedNoteException extends RuntimeException {

    public RemovedNoteException(String message) {
        super(message);
    }
}
