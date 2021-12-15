package de.comparus.opensource.longmap;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link LongMapImpl}
 */
public class LongMapImplTest {

    private int capacity = 4;
    private float loadFactor = 0.75f;

    LongMap<String> map = new LongMapImpl<>(capacity, loadFactor);

    @Test
    public void test() {

        // checking isEmpty() method before putting value
        assertTrue(map.isEmpty());

        // checking size() method before putting value
        assertEquals(map.size(), 0);


        // checking put() method
        assertNull(map.put(34434L, "First value"));

        // checking isEmpty() method after putting value
        assertFalse(map.isEmpty());

        // checking size() method after putting value
        assertEquals(map.size(), 1);


        // checking value before rehashing
        assertEquals(map.get(34434L), "First value");

        assertNull(map.put(35634L, "Second value"));
        assertNull(map.put(32425L, "Third value"));

        // checking size after resizing
        assertEquals(map.size(), 3);

        // checking value after rehashing
        assertEquals(map.get(34434L), "First value");


        // checking putting null value
        assertNull(map.put(23764L, null));

        assertNull(map.get(23764L));

        // checking containsNullValue() method
        assertTrue(map.containsValue(null));


        // checking replacing value
        String oldValue = map.put(32425L, "Fourth value");

        assertEquals(oldValue, "Third value");

        assertEquals(map.get(32425L), "Fourth value");

        assertEquals(map.size(), 4);


        // checking contains Key/Value methods
        assertTrue(map.containsKey(34434L));

        assertTrue(map.containsValue("First value"));


        // checking remove() method
        String removedValue = map.remove(34434L);

        assertEquals(removedValue, "First value");

        // checking size() method after removing value
        assertEquals(map.size(), 3);

        assertFalse(map.containsKey(34434L));

        assertFalse(map.containsValue("First value"));


        // checking keys() method
        long[] keys = map.keys();

        assertNotNull(keys);

        assertEquals(keys.length, map.size());


        // checking values() method
        assertNotNull(map.values());


        // checking clear() method
        map.clear();

        assertTrue(map.isEmpty());

        assertEquals(map.size(), 0);
    }
}
