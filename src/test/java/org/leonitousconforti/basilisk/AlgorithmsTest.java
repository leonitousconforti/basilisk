package org.leonitousconforti.basilisk;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.MalformedURLException;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.leonitousconforti.basilisk.core.Algorithms;

/**
 * Unit tests for the algorithms manager.
 */
class AlgorithmsTest {
    // Test object
    private final Algorithms algorithms = new Algorithms();

    @Test
    void testLoadingOutSourcedAlgorithm()
            throws MalformedURLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        // Attempt to load an algorithm not in the jar/class path
        algorithms.loadOutSourcedAlgorithm(
                "/Users/leo.conforti/Desktop/basilisk/src/main/resources/testOutSourcedAlgorithm.java");

        // Check if the loading worked
        String[] allLoadedAlgorithms = algorithms.getAllLoadedAlgorithms();
        boolean isAlgorithmLoaded = Arrays.stream(allLoadedAlgorithms).filter("testOutSourcedAlgorithm"::equals)
                .findAny().isPresent();
        assertEquals(isAlgorithmLoaded, true, "Test out-sourced algorithm was not successfully loaded");

        // Attempt to set the algorithm to the running algorithm
        algorithms.setRunningAlgorithm("testOutSourcedAlgorithm");

        // Make sure the running algorithm was set properly
        assertEquals(algorithms.getRunningAlgorithm().getName(), "testOutSourcedAlgorithm",
                "Test out-sourced algorithm is not successfully running");

    }
}
