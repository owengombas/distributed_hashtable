package org.example.ht;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HTMap<TKey, TValue> implements HT<TKey, TValue> {
    private Map<TKey, TValue> mapRepresentation;

    public HTMap() {
        this.mapRepresentation = new HashMap<>();
    }

    public HTMap(Map<TKey, TValue> mapInstance) {
        this.mapRepresentation = mapInstance;
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
    public boolean exists(TKey K) {
        return this.mapRepresentation.containsKey(K);
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
