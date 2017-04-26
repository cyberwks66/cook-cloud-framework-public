package com.cooksys.cloud.selftest.monkey;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Memory leak self test implementation. Creates a true java memory leak that
 * will eventually hang the JVM.
 *
 * @author timd
 */
public class MemoryLeakMonkey implements Runnable {

    /**
     * Class intentionally does not have an equals() method, to create memory
     * leak.
     *
     * @author timd
     */
    static class Key {
        Integer id;

        Key(Integer id) {
            this.id = id;
        }

        @Override
        public int hashCode() {
            return id.hashCode();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Runnable#run()
     */
    public void run() {
        Map<Key, String> m = new HashMap<Key, String>();
        while (true) {
            try {
                for (int i = 0; i < 10000; i++) {
                    if (!m.containsKey(new Key(i))) {
                        m.put(new Key(i), StringUtils.leftPad("a", 1024));
                    }
                }
            } catch (OutOfMemoryError e) {
                System.out.println(e.getMessage());
            }
        }

    }
}