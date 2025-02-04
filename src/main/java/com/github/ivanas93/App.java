package com.github.ivanas93;

import com.github.ivanas93.exception.EntropySourceException;
import com.github.ivanas93.exception.HashingException;
import com.github.ivanas93.exception.SystemNotSupportedException;
import com.github.ivanas93.generator.AbstractPasswordGenerator;
import com.github.ivanas93.generator.LinuxPasswordGenerator;
import com.github.ivanas93.generator.MacPasswordGenerator;
import com.github.ivanas93.generator.WindowsPasswordGenerator;
import com.github.ivanas93.os.SystemCommunicator;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class App {
    public static void main(String[] args) {
        try {
            AbstractPasswordGenerator passwordGenerator;

            if (SystemCommunicator.isLinux()) {
                passwordGenerator = new LinuxPasswordGenerator();
            } else if (SystemCommunicator.isWindows()) {
                passwordGenerator = new WindowsPasswordGenerator();
            } else if (SystemCommunicator.isMac()) {
                passwordGenerator = new MacPasswordGenerator();
            } else {
                throw new SystemNotSupportedException("Unsupported operating system.");
            }

            String password = passwordGenerator.generatePassword(16);
            log.info("Generated password: {}", password);

        } catch (EntropySourceException | HashingException | SystemNotSupportedException e) {
            log.error("Password generation failed: {}", e.getMessage());
        }
    }
}
