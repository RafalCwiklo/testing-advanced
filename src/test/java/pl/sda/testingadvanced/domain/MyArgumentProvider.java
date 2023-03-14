package pl.sda.testingadvanced.domain;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.stream.Stream;

public class MyArgumentProvider implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
        return Stream.of(
                Arguments.of("1", "true"),
                Arguments.of("2", "false"),
                Arguments.of("4", "false"),
                Arguments.of("5", "true"));
    }
}
