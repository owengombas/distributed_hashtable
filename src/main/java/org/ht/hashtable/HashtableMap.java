package org.ht.hashtable;

import org.json.JSONObject;

import java.util.*;

public class HashtableMap<TKey, TValue> implements Hashtable<TKey, TValue> {
    private Map<TKey, TValue> mapRepresentation;

    public HashtableMap() {
        this.mapRepresentation = new HashMap<>();
    }

    public HashtableMap(Map<TKey, TValue> mapInstance) {
        this.mapRepresentation = mapInstance;
    }

    @Override
    public Set<TKey> keySet() {
        return this.mapRepresentation.keySet();
    }

    @Override
    public Collection<TValue> values() {
        return this.mapRepresentation.values();
    }

    @Override
    public void put(TKey key, TValue value) {
        this.mapRepresentation.put(key, value);
    }

    @Override
    public Optional<TValue> get(TKey key) {
        if (this.exists(key)) {
            return Optional.of(this.mapRepresentation.get(key));
        }
        return Optional.empty();
    }

    @Override
    public void remove(TKey K) {
        this.mapRepresentation.remove(K);
    }

    @Override
    public int size() {
        return this.mapRepresentation.size();
    }

    @Override
    public boolean isEmpty() {
        return this.mapRepresentation.isEmpty();
    }

    @Override
    public boolean exists(TKey K) {
        return this.mapRepresentation.containsKey(K);
    }

    @Override
    public String toJSON() {
        return new JSONObject(this.mapRepresentation).toString();
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("Map of ").append(this.mapRepresentation.size()).append(" elements:\n");
        s.append(
                this.mapRepresentation
                .entrySet()
                .stream()
                .map((e) -> String.format("%s = %s\n", e.getKey(), e.getValue()))
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString()
        );
        return s.toString();
    }
}
