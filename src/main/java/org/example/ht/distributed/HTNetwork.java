package org.example.ht.distributed;

import org.example.ht.HT;

import java.util.Optional;
import java.util.Vector;

public interface HTNetwork<TKey, TValue> {
    String getId();
    String getAddress();
    Vector<String> getKnownHosts();
    HT<TKey, TValue> getCacheSystem();
    String join(String nodeToJoinAddress);
    Optional<TValue> ask(String address, TKey key);
    Optional<TValue> askAll(TKey key);
    Optional<TValue> get(TKey key);
    String put(TKey key, TValue value);
    String getPutNodeAddress(TKey key);
    String leave();
}
