package pl.sda.testingadvanced.exceptions;

public class NoSuchClientException extends RuntimeException {
    public NoSuchClientException(String clientNumber) {
        super(String.format("Nie znaleziono klienta o takim identyfikatorze: [%s]",clientNumber ));
    }
}
