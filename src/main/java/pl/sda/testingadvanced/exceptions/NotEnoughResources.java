package pl.sda.testingadvanced.exceptions;

public class NotEnoughResources extends RuntimeException {
    public NotEnoughResources(String clientNumber) {
        super(String.format("Brak wystarczających środków u klienta: [%s]",clientNumber ));
    }
}
