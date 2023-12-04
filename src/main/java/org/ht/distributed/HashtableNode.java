package org.ht.distributed;

import org.ht.hashtable.Hashtable;

import java.util.Map;
import java.util.Optional;

public interface HashtableNode<TKey, TValue> {
    String getAddress();
    Map<Integer, String> getKnownHosts();
    String getBootstrapNodeAddress();
    Hashtable<TKey, TValue> getCacheSystem();
    String join(String nodeToJoinAddress);
    String join(String nodeToJoinAddress, int position);
    Optional<TValue> get(TKey key);
    String put(TKey key, TValue value);
    int getKeyPosition(TKey key);
    void leave();
}
