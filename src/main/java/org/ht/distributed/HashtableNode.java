package org.ht.distributed;

import org.ht.hashtable.Hashtable;

import java.util.Map;
import java.util.Optional;

public interface HashtableNode<TKey, TValue> {
    /**
     * Get the address of the node
     *
     * @return the address of the node
     */
    String getAddress();

    /**
     * Retrieve the how many times a key was hit, meaning that it was retrieved from the cache system
     * it doesn't matter if a value was assigned to the key or not
     *
     * @param key the key to retrieve the hits
     * @return the how many times a key was hit
     */
    public int getKeyHits(TKey key);


    /**
     * Retrieve the how many times a key was missed, meaning that it had to be retrieved from another node
     * it doesn't matter if a value was assigned to the key or not.
     *
     * @param key the key to retrieve the misses
     * @return the how many times a key was missed
     */
    public int getKeyMisses(TKey key);

    /**
     * Get the known hosts of the node, the key is the position of the node in the ring
     *
     * @return the known hosts of the node
     */
    Map<Integer, String> getKnownHosts();

    /**
     * Get the address of the bootstrap node of the ring
     *
     * @return the address of the bootstrap node
     */
    String getBootstrapNodeAddress();

    /**
     * Get the position of the node in the ring
     *
     * @return the position of the node in the ring
     */
    int getPosition();

    /**
     * Get the cache system of the node
     *
     * @return the cache system of the node
     */
    Hashtable<TKey, TValue> getCacheSystem();

    /**
     * Join the ring
     *
     * @param nodeToJoinAddress the address of the node to join, it should be the address of the bootstrap node
     * @return the address of the node that joined the ring
     */
    String join(String nodeToJoinAddress);

    /**
     * Join the ring
     *
     * @param nodeToJoinAddress the address of the node to join, it should be the address of the bootstrap node
     * @param position          the position of the node in the ring
     * @return the address of the node that joined the ring
     */
    String join(String nodeToJoinAddress, int position);

    /**
     * Get a value of a key in the cache system of the distributed system
     *
     * @param key the key to get the value
     * @return the value of the key
     */
    Optional<TValue> get(TKey key);

    /**
     * Put a value in the cache system of the distributed system
     *
     * @param key   the key to put the value
     * @param value the value to put
     * @return the address of the node that put the value
     */
    String put(TKey key, TValue value);

    /**
     * Get the position of a key in the ring
     *
     * @param key the key to get the position
     * @return the position of the key
     */
    int getKeyPosition(TKey key);

    /**
     * Leave the ring
     */
    void leave(boolean rebalance);

    /**
     * Remove a node in the ring by its position
     *
     * @param position the position of the node to remove
     * @return the new position of the current node in the ring
     */
    int removeNodeAtPosition(int position);
}
