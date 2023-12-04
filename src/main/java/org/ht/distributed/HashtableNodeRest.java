package org.ht.distributed;

import io.javalin.Javalin;
import io.javalin.http.Context;
import org.ht.hashtable.Hashtable;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Vector;

public class HashtableNodeRest implements HashtableNode<String, String> {
    private final Hashtable<String, String> cacheSystem;
    private final HttpClient client;
    private Javalin server;
    private final Logger log;
    private final Map<Integer, String> knownHosts = new HashMap<>();
    private int position = -1;

    public int getPosition() {
        return this.position;
    }

    public int getPosition(String nodeAddress) {
        String result = this.post(nodeAddress, "/position", "");
        int position = new JSONObject(result).getInt("position");
        return position;
    }

    public String getAddressOfPosition(int position) {
        return this.getKnownHosts().get(position);
    }

    @Override
    public Hashtable<String, String> getCacheSystem() {
        return this.cacheSystem;
    }

    @Override
    public Map<Integer, String> getKnownHosts() {
        return this.knownHosts;
    }

    @Override
    public String getBootstrapNodeAddress() {
        return this.getKnownHosts().get(0);
    }

    @Override
    public int getKeyPosition(String key) {
        return key.hashCode() % (this.getKnownHosts().size() + 1) - 1;
    }

    @Override
    public String getAddress() {
        return String.format("http://%s:%s", this.server.jettyServer().getServerHost(), this.server.jettyServer().getServerPort());
    }

    public HashtableNodeRest(int port, Hashtable<String, String> cacheSystem, String id) {
        this.cacheSystem = cacheSystem;
        this.client = HttpClient.newHttpClient();
        this.startServer(port);
        this.log = LoggerFactory.getLogger(String.format("%s", this.getAddress()));

        this.initializeRestApi();
    }

    private void initializeRestApi() {
        this.server.post("/position", this::onPosition);
        this.server.post("/rebalance", this::onRebalance);
        this.server.post("/join", this::onJoin);
        this.server.post("/ack", this::onAck);
        this.server.post("/update", this::onUpdate);
        this.server.post("/get", this::onGet);
        this.server.post("/put", this::onPut);
    }

    private void onPosition(Context context) {
        context.result(new JSONObject(Map.of("position", this.getPosition())).toString());
    }

    public void startServer(int port) {
        this.server = Javalin.create().start("127.0.0.1", port);
    }

    public void stopServer() {
        this.server.stop();
    }

    public int removeNodeAtPosition(int position) {
        this.getKnownHosts().remove(position);

        for (int i = position; i < this.getKnownHosts().size(); i++) {
            this.getKnownHosts().put(i, this.getKnownHosts().get(i + 1));
        }

        // Find the new position of itself
        int newPosition = -1;
        for (int i = 0; i < this.getKnownHosts().size(); i++) {
            String address = this.getKnownHosts().get(i);
            if (address == null) continue;
            if (this.getKnownHosts().get(i).equals(this.getAddress())) {
                newPosition = i;
                break;
            }
        }

        return newPosition;
    }

    private void onRebalance(Context context) {
        int nodePosition = new JSONObject(context.body()).getInt("position");

        int newPosition = this.removeNodeAtPosition(nodePosition);
        this.position = newPosition;
        this.log.info("{} got rebalanced to {}", this.getAddress(), this.getPosition());

        JSONObject jsonCache = new JSONObject(new JSONObject(context.body()).getString("cache"));
        for (String key : jsonCache.keySet()) {
            String value = jsonCache.getString(key);
            this.getCacheSystem().put(key, value);
            this.log.info("the key value pair ('{}', '{}') got rebalanced to {} which is now at position {}", key, value, this.getAddress(), this.getPosition());
        }
    }

    private void onUpdate(Context context) {
        int nodePosition = new JSONObject(context.body()).getInt("position");
        String nodeAddress = new JSONObject(context.body()).getString("address");
        this.getKnownHosts().put(nodePosition, nodeAddress);

        if (this.getKnownHosts().containsKey(nodePosition)) {
            this.log.info("for {} the node with position {} ({}) successfully joined", this.getAddress(), nodePosition, nodeAddress);
        } else {
            this.log.error("for {} the node {} ({}) couldn't join", this.getAddress(), nodePosition, nodeAddress);
        }
    }

    private void onAck(Context context) {
        this.position = new JSONObject(context.body()).getInt("position");
        String from = new JSONObject(context.body()).getString("from");
        JSONObject jsonKnownHosts = new JSONObject(new JSONObject(context.body()).getString("knownHosts"));
        for (String key : jsonKnownHosts.keySet()) {
            String address = jsonKnownHosts.getString(key);
            int position = Integer.parseInt(key);
            this.getKnownHosts().put(position, address);
            // Tell these nodes that they have to update their knownHosts
            // only if it's not the node that sent the ack or the node that received the ack
            if (!address.equals(this.getAddress()) && !address.equals(from)) {
                this.post(address, "/update", JSONObject.wrap(Map.of("position", this.getPosition(), "address", this.getAddress())).toString());
            }
        }

        this.log.info("{} receive ack with position {} and it now knows the nodes: {}", this.getAddress(), this.getPosition(), this.getKnownHosts());
    }

    private void onJoin(Context context) {
        String newNodeAddress = new JSONObject(context.body()).getString("address");
        int position = new JSONObject(context.body()).getInt("position");
        if (position < 0) position = this.getKnownHosts().size();
        this.getKnownHosts().put(position, newNodeAddress);

        this.log.info("{} joined {} successfully with position {}", newNodeAddress, this.getAddress(), position);

        this.post(newNodeAddress, "/ack", JSONObject.wrap(
                Map.of(
                        "position", position,
                        "from", this.getAddress(),
                        "knownHosts", JSONObject.wrap(this.getKnownHosts()).toString()
                )
        ).toString());
    }

    private void onPut(Context context) {
        String key = new JSONObject(context.body()).keySet().iterator().next();
        String value = new JSONObject(context.body()).getString(key);

        this.getCacheSystem().put(key, value);
        this.log.info("{} successfully received stored the key value pair ('{}', '{}')", this.getAddress(), key, value);

        context.result(new JSONObject(Map.of("address", this.getAddress())).toString());
    }

    private void onGet(Context context) {
        String key = new JSONObject(context.body()).getString("key");
        String result = this.getCacheSystem().get(key).orElse(null);

        this.log.info("{} received a ask request for the key '{}' and answered {}", this.getAddress(), key, result);

        context.result(new JSONObject(Map.of("result", result == null ? JSONObject.NULL : result)).toString());
    }

    @Override
    public String join(String nodeToJoinAddress) {
        return this.join(nodeToJoinAddress, -1);
    }

    @Override
    public String join(String nodeToJoinAddress, int position) {
        int destinationPosition = this.getPosition(nodeToJoinAddress);
        this.log.info("{} asks to join {} which has position {}", this.getAddress(), nodeToJoinAddress, destinationPosition);
        return this.post(nodeToJoinAddress, "/join", JSONObject.wrap(Map.of("address", this.getAddress(), "position", position)).toString());
    }

    @Override
    public Optional<String> get(String key) {
        int keyPosition = this.getKeyPosition(key);

        this.log.info("{} got asked key '{}'", this.getAddress(), key);

        Optional<String> result;

        if (this.getPosition() == keyPosition) {
            this.log.info("the key '{}' should be present locally ({})", key, this.getAddress());
            result = this.getCacheSystem().get(key);
        } else {
            String addressOfKey = this.getAddressOfPosition(keyPosition);
            if (addressOfKey == null) {
                this.log.info("The node that used to own the key '{}' (position {}) is not in the system anymore", key, keyPosition);
                result = Optional.empty();
            } else {
                this.log.info("{} do not have key '{}' -> ask node at {} position {}", this.getAddress(), key, addressOfKey, keyPosition);
                String answerString = this.post(addressOfKey, "/get", JSONObject.wrap(Map.of("key", key)).toString());
                JSONObject answer = new JSONObject(answerString);
                result = Optional.ofNullable(answer.isNull("result") ? null : answer.getString("result"));
            }
        }

        if (result.isEmpty()) {
            this.log.info("The distributed HT doesn't own the key {}", key);
        } else {
            this.log.info("The distributed HT return the value of the key '{}': '{}'", key, result.get());
        }

        return result;
    }

    @Override
    public String put(String key, String value) {
        int keyPosition = this.getKeyPosition(key);

        this.log.info("{} got asked to put {}:{}", this.getAddress(), key, value);

        if (this.getPosition() == keyPosition) {
            this.getCacheSystem().put(key, value);
            this.log.info("the key '{}' got successfully stored directly at {}", key, this.getAddress());
            return this.getAddress();
        } else {
            String addressOfKey = this.getAddressOfPosition(keyPosition);
            this.log.info("{} attends to store the key '{}' at node {} with position {}", this.getAddress(), key, addressOfKey, keyPosition);
            return this.post(addressOfKey, "/put", JSONObject.wrap(Map.of(key, value)).toString());
        }
    }

    @Override
    public void leave() {
        this.log.info("{} at position {} is leaving", this.getAddress(), this.getPosition());

        this.removeNodeAtPosition(this.getPosition());

        Map<String, Vector<String>> keysToRebalance = new HashMap<>();
        for (String key : this.getCacheSystem().keySet()) {
            int keyPosition = this.getKeyPosition(key);
            String addressOfKey = this.getAddressOfPosition(keyPosition);
            if (keysToRebalance.containsKey(addressOfKey)) {
                keysToRebalance.get(addressOfKey).add(key);
            } else {
                keysToRebalance.put(addressOfKey, new Vector<>());
                keysToRebalance.get(addressOfKey).add(key);
            }
        }

        for (String address : keysToRebalance.keySet()) {
            Map<String, String> keyValues = new HashMap<>();
            for (String key : keysToRebalance.get(address)) {
                keyValues.put(key, this.getCacheSystem().get(key).orElse(null));
            }
            this.post(address, "/rebalance", JSONObject.wrap(Map.of(
                    "position", this.getPosition(),
                    "cache", JSONObject.wrap(keyValues).toString()
            )).toString());
        }
    }

    private String post(String address, String path, String data) {
        try {
            var request = HttpRequest.newBuilder(URI.create(String.format("%s%s", address, path))).header("accept", "application/json").POST(HttpRequest.BodyPublishers.ofString(data)).build();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
