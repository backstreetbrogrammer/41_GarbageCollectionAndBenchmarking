package com.backstreetbrogrammer;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class StudentTest {

    @Test
    void testMemoryLeakWhenNoEqualsOrHashCodeMethodImplemented() {
        final Map<Student, Integer> map = new HashMap<>();
        for (int i = 0; i < 100; i++) {
            map.put(new Student("John"), 1);
        }
        assertNotEquals(1, map.size());
    }
}
