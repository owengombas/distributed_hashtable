package org.ht;

import org.ht.distributed.HashtableNodeRest;
import org.ht.hashtable.HashtableMap;

public class MainDistributed {
    public static void main(String[] args) {
        HashtableNodeRest A = new HashtableNodeRest(7070, new HashtableMap<>(), "A");
        HashtableNodeRest B = new HashtableNodeRest(7071, new HashtableMap<>(), "B");
        HashtableNodeRest C = new HashtableNodeRest(7072, new HashtableMap<>(), "C");

        B.getCacheSystem().put("TestKey", "TestValue");

        A.join(A.getAddress());
        B.join(A.getAddress());
        C.join(A.getAddress());

        A.get("TestKey");
        A.get("TestKeyDoNotExists");

        A.put("a", "Test");
        B.put("b", "Test");
        A.put("c", "Test");

        A.get("a");
        A.get("b");
        A.get("c");

        B.leave();
        A.get("b");
    }
}
