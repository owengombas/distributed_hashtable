package org.ht;

import org.ht.distributed.HashtableNodeREST;
import org.ht.hashtable.HashtableMap;

public class MainDistributed {
    public static void main(String[] args) {
        HashtableNodeREST htNode1 = new HashtableNodeREST(7070, new HashtableMap<>(), "A");
        HashtableNodeREST htNode2 = new HashtableNodeREST(7071, new HashtableMap<>(), "B");

        htNode2.getCacheSystem().put("TestKey", "TestValue");

        htNode2.join(htNode1.getAddress());

        htNode1.get("TestKey");
        htNode1.get("TestKeyDoNotExists");

        htNode1.put("a", "Test");
        htNode1.put("b", "Test");
        htNode1.put("c", "Test");

        htNode1.get("a");
        htNode1.get("b");
        htNode1.get("c");
    }
}
