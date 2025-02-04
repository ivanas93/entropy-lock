package com.github.ivanas93.generator;

import com.github.ivanas93.exception.EntropySourceException;
import lombok.extern.slf4j.Slf4j;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

@Slf4j
public class WindowsPasswordGenerator extends AbstractPasswordGenerator {

    @Override
    protected byte[] getSystemEntropy(int length) throws EntropySourceException {
        try {
            Linker linker = Linker.nativeLinker();
            SymbolLookup bcrypt = SymbolLookup.libraryLookup("bcrypt.dll", Arena.global());

            MethodHandle genRandomHandle = linker.downcallHandle(
                    bcrypt.find("BCryptGenRandom").orElseThrow(() -> new EntropySourceException("Function BCryptGenRandom not found")),
                    FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.JAVA_LONG, ValueLayout.JAVA_INT)
            );

            try (Arena arena = Arena.ofConfined()) {
                MemorySegment randomBytes = arena.allocate(length);
                int result = (int) genRandomHandle.invoke(null, randomBytes, length, 0);

                if (result != 0) {
                    throw new EntropySourceException("BCryptGenRandom failed.");
                }

                long timestamp = System.currentTimeMillis();
                log.debug("System timestamp: {}", timestamp);

                byte[] combinedEntropy = this.combineEntropy(randomBytes.toArray(ValueLayout.JAVA_BYTE), timestamp);
                log.debug("Combined entropy: {}", combinedEntropy.length);

                return combinedEntropy;
            }
        } catch (Throwable t) {
            throw new EntropySourceException("Error while gathering entropy", t);
        }
    }
}