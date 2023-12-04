package org.ht.hashtable;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface Hashtable<TKey, TValue> {
    public Set<TKey> keySet();

    public Collection<TValue> values();

    public void put(TKey K, TValue V);

    public Optional<TValue> get(TKey K);

    public void remove(TKey K);

    public int size();

    public boolean isEmpty();

    public boolean exists(TKey K);

    public String toJSON();
}
