package exception;

public class BookNotAvailableException extends RuntimeException {
    public BookNotAvailableException(String isbn) {
        super("El libro con ISBN " + isbn + " no está disponible para préstamo");
    }
}
