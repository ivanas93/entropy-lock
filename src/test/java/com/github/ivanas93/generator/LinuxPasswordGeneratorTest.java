package com.github.ivanas93.generator;

import com.github.ivanas93.exception.EntropySourceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LinuxPasswordGeneratorTest {
    LinuxPasswordGenerator passwordGenerator;

    @BeforeEach
    void setUp() {
        passwordGenerator = new LinuxPasswordGenerator();
    }

    @Test
    void shouldGenerateEntropyBytesCorrectly() throws EntropySourceException {
        // Given
        int entropyLength = 16;

        // When
        byte[] entropy = passwordGenerator.getSystemEntropy(entropyLength);

        // Then
        assertThat(entropy).isNotNull().hasSize(entropyLength + Long.BYTES);
    }

    @Test
    void shouldThrowExceptionWhenEntropyFails() {
        LinuxPasswordGenerator faultyGenerator = new LinuxPasswordGenerator() {
            @Override
            protected byte[] getSystemEntropy(int length) throws EntropySourceException {
                throw new EntropySourceException("Simulated entropy failure");
            }
        };

        // When - Then
        assertThatThrownBy(() -> faultyGenerator.getSystemEntropy(16))
                .isInstanceOf(EntropySourceException.class)
                .hasMessage("Simulated entropy failure");
    }
}