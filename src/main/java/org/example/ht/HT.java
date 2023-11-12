package org.example.ht;

import java.util.Optional;

public interface HT<TKey, TValue> {
    public void put(TKey K, TValue V);
    public Optional<TValue> get(TKey K);
    public boolean exists(TKey K);
}
