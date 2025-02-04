package com.github.ivanas93.generator;

import com.github.ivanas93.exception.EntropySourceException;
import lombok.extern.slf4j.Slf4j;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

@Slf4j
public class LinuxPasswordGenerator extends AbstractPasswordGenerator {
    @Override
    protected byte[] getSystemEntropy(int length) throws EntropySourceException {
        try {
            Linker linker = Linker.nativeLinker();
            SymbolLookup libc = SymbolLookup.libraryLookup("libc.so.6", Arena.global());

            MethodHandle getRandomHandle = linker.downcallHandle(
                    libc.find("getrandom").orElseThrow(() -> new EntropySourceException("Function getrandom not found")),
                    FunctionDescriptor.of(ValueLayout.JAVA_LONG, ValueLayout.ADDRESS, ValueLayout.JAVA_LONG, ValueLayout.JAVA_INT)
            );
            
            try (Arena arena = Arena.ofConfined()) {
                MemorySegment randomBytes = arena.allocate(length);
                long bytesRead = (long) getRandomHandle.invoke(randomBytes, length, 0);
                if (bytesRead != length) {
                    throw new EntropySourceException("Insufficient random bytes collected.");
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
