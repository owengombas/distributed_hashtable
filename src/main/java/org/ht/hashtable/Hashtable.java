package org.ht.hashtable;

import java.util.Optional;

public interface Hashtable<TKey, TValue> {
    public void put(TKey K, TValue V);
    public Optional<TValue> get(TKey K);
    public boolean exists(TKey K);
}
