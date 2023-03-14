package pl.sda.testingadvanced.exceptions;

public class NotEnoughResourcesException extends RuntimeException{
    public NotEnoughResourcesException(String message) {
        super(message);
    }
}
