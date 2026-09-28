package dev.nickl0s.opencode;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class OpenCodeReferenceTest {
    @Test
    public void formatsMultipleLines() {
        assertEquals("@src/main.js#L10-25", OpenCodeReference.format("src/main.js", 10, 25));
    }

    @Test
    public void formatsSingleLinesAsARange() {
        assertEquals("@src/A.java#L12-12", OpenCodeReference.format("src/A.java", 12, 12));
    }

    @Test
    public void normalizesWindowsSeparators() {
        assertEquals("@src/main.js#L10-25", OpenCodeReference.format("src\\main.js", 10, 25));
    }
}

