package org.sber.storage;

public interface Storage {
    Object get(Object key);

    void store(Object key, Object value);
}
