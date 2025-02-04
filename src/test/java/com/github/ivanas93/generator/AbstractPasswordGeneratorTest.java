package com.github.ivanas93.generator;

import com.github.ivanas93.exception.EntropySourceException;
import com.github.ivanas93.exception.HashingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AbstractPasswordGeneratorTest {
    AbstractPasswordGenerator passwordGenerator;

    @BeforeEach
    void setUp() {
        passwordGenerator = new AbstractPasswordGenerator() {
            @Override
            protected byte[] getSystemEntropy(int length) {
                byte[] entropy = new byte[length];
                for (int i = 0; i < length; i++) {
                    entropy[i] = (byte) i;
                }
                return entropy;
            }
        };
    }

    @Test
    void shouldGeneratePasswordWithCorrectLength() throws EntropySourceException, HashingException {
        // Given
        int passwordLength = 16;

        // When
        String password = passwordGenerator.generatePassword(passwordLength);

        // Then
        assertThat(password).isNotNull().hasSize(passwordLength);
    }

    @Test
    void shouldThrowHashingExceptionWhenSha256Fails() {
        // Given 
        AbstractPasswordGenerator faultyGenerator = new AbstractPasswordGenerator() {
            @Override
            protected byte[] getSystemEntropy(int length) {
                return new byte[length];
            }

            @Override
            protected byte[] applySha256(byte[] input) throws HashingException {
                throw new HashingException("Forcing hash failure");
            }
        };

        // When - Then
        assertThatThrownBy(() -> faultyGenerator.generatePassword(16))
                .isInstanceOf(HashingException.class)
                .hasMessage("Forcing hash failure");
    }
}