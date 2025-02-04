package com.github.ivanas93.generator;

import com.github.ivanas93.exception.EntropySourceException;
import lombok.extern.slf4j.Slf4j;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

@Slf4j
public class MacPasswordGenerator extends AbstractPasswordGenerator {

    @Override
    protected byte[] getSystemEntropy(int length) throws EntropySourceException {
        try {
            Linker linker = Linker.nativeLinker();
            SymbolLookup libSystem = SymbolLookup.libraryLookup("libSystem.B.dylib", Arena.global());

            MethodHandle getEntropyHandle = linker.downcallHandle(
                    libSystem.find("getentropy").orElseThrow(() -> new EntropySourceException("Function getentropy not found")),
                    FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.JAVA_LONG)
            );

            try (Arena arena = Arena.ofConfined()) {
                MemorySegment randomBytes = arena.allocate(length);

                int result = (int) getEntropyHandle.invoke(randomBytes, (long) length);
                if (result != 0) {
                    throw new EntropySourceException("getentropy failed to generate random bytes.");
                }

                long timestamp = System.currentTimeMillis();
                log.debug("System timestamp: {}", timestamp);

                byte[] combinedEntropy = this.combineEntropy(randomBytes.toArray(ValueLayout.JAVA_BYTE), timestamp);
                log.debug("Combined entropy length: {}", combinedEntropy.length);

                return combinedEntropy;
            }
        } catch (Throwable t) {
            throw new EntropySourceException("Error while gathering entropy", t);
        }
    }

}
