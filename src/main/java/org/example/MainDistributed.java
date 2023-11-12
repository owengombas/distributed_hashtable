package org.example;

import org.example.ht.HTMap;
import org.example.ht.distributed.HTRest;

public class MainDistributed {
    public static void main(String[] args) {
        HTRest<String, String> htNode1 = new HTRest<>(7070, new HTMap<>(), "A");
        HTRest<String, String> htNode2 = new HTRest<>(7071, new HTMap<>(), "B");

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
