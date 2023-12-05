package org.ht;

import org.ht.distributed.HashtableNodeRest;
import org.ht.hashtable.HashtableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainDistributed {
    public static void main(String[] args) {
        HashtableNodeRest A = new HashtableNodeRest(7070, new HashtableMap<>());
        HashtableNodeRest B = new HashtableNodeRest(7071, new HashtableMap<>());
        HashtableNodeRest C = new HashtableNodeRest(7072, new HashtableMap<>());
        HashtableNodeRest D = new HashtableNodeRest(7073, new HashtableMap<>());

        Logger l = LoggerFactory.getLogger(MainDistributed.class);

        B.getCacheSystem().put("TestKey", "TestValue");

        A.join(A.getAddress()); // Bootstrap node
        B.join(A.getAddress());
        C.join(A.getAddress());
        D.join(A.getAddress());

        l.info("====================================");

        A.get("TestKey");
        A.get("TestKeyDoNotExists");

        l.info("====================================");

        A.put("a", "av");
        B.put("b", "bv");
        A.put("c", "cv");
        A.put("d", "dv");
        A.put("e", "ev");
        A.put("f", "fv");
        A.put("g", "gv");
        A.put("h", "hv");

        l.info("====================================");

        A.get("a");
        A.get("b");
        A.get("c");
        A.get("d");
        A.get("e");
        A.get("f");
        A.get("g");
        A.get("h");

        l.info("====================================");

        B.leave(false);
        A.get("a");

        l.info("====================================");

        B.join(A.getAddress(), 1);
        A.get("a");

        l.info("====================================");

        B.leave(true);
        A.get("a");

        l.info("====================================");

        C.leave(true);
        A.get("c");

        l.info("====================================");

        D.leave(true);
        A.get("a");
        A.get("b");
        A.get("c");
        A.get("d");
        A.get("e");
        A.get("f");
        A.get("g");
        A.get("h");
    }
}
