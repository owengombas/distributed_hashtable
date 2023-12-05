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
    private final HttpClient client;
    private Javalin server;
    private final Logger log;
    private int position = -1;
    private final Hashtable<String, String> cacheSystem;
    private final Map<Integer, String> knownHosts = new HashMap<>();
    private final Map<String, Integer> keyHits = new HashMap<>();
    private final Map<String, Integer> keyMisses = new HashMap<>();

    @Override
    public int getPosition() {
        return this.position;
    }

    /**
     * Get the position of the node at the specified address
     *
     * @param nodeAddress the address of the node
     * @return the position of the node
     */
    public int getPosition(String nodeAddress) {
        String result = this.post(nodeAddress, "/position", "");
        return new JSONObject(result).getInt("position");
    }

    /**
     * Get the address of the node at the specified position
     *
     * @param position the position of the node
     * @return the address of the node
     */
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
        return key.hashCode() % (this.getKnownHosts().size());
    }

    @Override
    public String getAddress() {
        return String.format("http://%s:%s", this.server.jettyServer().getServerHost(), this.server.jettyServer().getServerPort());
    }

    @Override
    public int getKeyHits(String s) {
        return this.keyHits.getOrDefault(s, 0);
    }

    @Override
    public int getKeyMisses(String s) {
        return this.keyMisses.getOrDefault(s, 0);
    }

    public HashtableNodeRest(int port, Hashtable<String, String> cacheSystem) {
        this.cacheSystem = cacheSystem;
        this.client = HttpClient.newHttpClient();
        this.startServer(port);
        this.log = LoggerFactory.getLogger(String.format("%s", this.getAddress()));

        this.initializeRestApi();
    }

    private void initializeRestApi() {
        this.server.post("/position", this::onAskPosition);
        this.server.post("/rebalance", this::onRebalance);
        this.server.post("/join", this::onJoin);
        this.server.post("/ack", this::onAcknowledge);
        this.server.post("/update", this::onUpdate);
        this.server.post("/get", this::onGet);
        this.server.post("/put", this::onPut);
    }

    /**
     * This method is used to get the position of the node, it sends back a JSON object with the position
     *
     * @param context The context of the request
     */
    private void onAskPosition(Context context) {
        context.result(new JSONObject(Map.of("position", this.getPosition())).toString());
    }

    /**
     * Start the HTTP server
     */
    public void startServer(int port) {
        this.server = Javalin.create().start("127.0.0.1", port);
    }

    /**
     * Stop the HTTP server
     */
    public void stopServer() {
        this.server.stop();
    }

    public int removeNodeAtPosition(int position) {
        this.getKnownHosts().remove(position);

        Map<Integer, String> newKnownHosts = new HashMap<>();
        int newPosition = 0;
        for (Integer key : this.getKnownHosts().keySet()) {
            newKnownHosts.put(newPosition, this.getKnownHosts().get(key));
            newPosition++;
        }
        this.getKnownHosts().clear();
        this.getKnownHosts().putAll(newKnownHosts);

        // Find the new position of itself
        newPosition = -1;
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

    /**
     * This method is used to rebalance the node, it receives a JSON object with the new position and the cache
     * that is transferred to the node
     *
     * @param context The context of the request
     */
    private void onRebalance(Context context) {
        int nodePosition = new JSONObject(context.body()).getInt("position");
        boolean updateKnownHosts = new JSONObject(context.body()).getBoolean("updateKnownHosts");

        // Remove the node at the specified position and get the new position of itself
        if (updateKnownHosts) {
            this.position = this.removeNodeAtPosition(nodePosition);
            this.log.info("{} got rebalanced to {}", this.getAddress(), this.getPosition());

            // Update the cache system of the node
            JSONObject jsonCache = new JSONObject(new JSONObject(context.body()).getString("cache"));
            for (String key : jsonCache.keySet()) {
                String value = jsonCache.getString(key);
                this.getCacheSystem().put(key, value);
                this.log.info("the key value pair ('{}', '{}') got rebalanced to {} which is now at position {}", key, value, this.getAddress(), this.getPosition());
            }
        } else {
            this.knownHosts.remove(nodePosition);
        }
    }

    /**
     * This method is used to update the knownHosts of the node, it receives a JSON object with the position and the address of the node
     *
     * @param context The context of the request
     */
    private void onUpdate(Context context) {
        int nodePosition = new JSONObject(context.body()).getInt("position");
        String nodeAddress = new JSONObject(context.body()).getString("address");

        // Update the knownHosts of the node
        this.getKnownHosts().put(nodePosition, nodeAddress);
        this.log.info("for {} the node with position {} ({}) successfully joined", this.getAddress(), nodePosition, nodeAddress);
    }

    /**
     * This is call whenever a node successfully joined the ring, it receives a JSON object with the position of the node
     * and the addresses and positions of the other nodes
     *
     * @param context The context of the request
     */
    private void onAcknowledge(Context context) {
        this.position = new JSONObject(context.body()).getInt("position");
        String from = new JSONObject(context.body()).getString("from");
        JSONObject jsonKnownHosts = new JSONObject(new JSONObject(context.body()).getString("knownHosts"));

        for (String key : jsonKnownHosts.keySet()) {
            String address = jsonKnownHosts.getString(key);
            int position = Integer.parseInt(key);

            // Update the knownHosts of the node
            this.getKnownHosts().put(position, address);

            // Tell these nodes that they have to update their knownHosts
            // only if it's not the node that sent the ack or the node that received the ack
            if (!address.equals(this.getAddress()) && !address.equals(from)) {
                this.post(address, "/update", JSONObject.wrap(Map.of("position", this.getPosition(), "address", this.getAddress())).toString());
            }
        }

        this.log.info("{} receive ack with position {} and it now knows the nodes: {}", this.getAddress(), this.getPosition(), this.getKnownHosts());
    }

    /**
     * This method is called whenever a node asked to join the ring, it receives a JSON object with the address of the node
     * and an optional position (-1 if not specified) that the node wants to have in the ring.
     * It sends back an acknowledgement with the position of the node and the addresses and positions of the other nodes
     *
     * @param context The context of the request
     */
    private void onJoin(Context context) {
        String newNodeAddress = new JSONObject(context.body()).getString("address");
        int position = new JSONObject(context.body()).getInt("position");

        // If the position is not specified, put the node at the end of the ring
        if (position < 0) position = this.getKnownHosts().size();

        // Update the knownHosts of the node
        this.getKnownHosts().put(position, newNodeAddress);

        this.log.info("{} joined {} successfully with position {}", newNodeAddress, this.getAddress(), position);

        // Send back an acknowledgement with the position of the node and the addresses and positions of the other nodes
        this.post(newNodeAddress, "/ack", JSONObject.wrap(Map.of("position", position, "from", this.getAddress(), "knownHosts", JSONObject.wrap(this.getKnownHosts()).toString())).toString());
    }

    /**
     * This method is called whenever a node receives a put request, it receives a JSON object with the key and the value
     * It stores the key value pair in the cache system of the node
     * It sends back the address of the node that stored the key value pair (itself)
     *
     * @param context The context of the request
     */
    private void onPut(Context context) {
        String key = new JSONObject(context.body()).keySet().iterator().next();
        String value = new JSONObject(context.body()).getString(key);

        // Store the key value pair in the local cache system
        this.getCacheSystem().put(key, value);
        this.log.info("{} successfully received stored the key value pair ('{}', '{}')", this.getAddress(), key, value);

        // Send back the address of the node that stored the key value pair
        context.result(new JSONObject(Map.of("address", this.getAddress())).toString());
    }

    /**
     * This method is called whenever a node receives a get request, it receives a JSON object with the key
     * It sends back a JSON object with the result of the get request, it can be null if the key is not present in the cache system
     *
     * @param context The context of the request
     */
    private void onGet(Context context) {
        String key = new JSONObject(context.body()).getString("key");
        String result = this.getCacheSystem().get(key).orElse(null);

        this.log.info("{} received a ask request for the key '{}' and answered {}", this.getAddress(), key, result);

        // Send back the value of the key in the local cache system
        context.result(new JSONObject(Map.of("result", result == null ? JSONObject.NULL : result)).toString());
    }

    @Override
    public String join(String nodeToJoinAddress) {
        return this.join(nodeToJoinAddress, -1);
    }

    @Override
    public String join(String nodeToJoinAddress, int position) {
        // Send a request to the node to join the ring at the specified position to the node that we want to join
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
            // If the key is in the node, increment the keyHits
            this.keyHits.put(key, this.getKeyHits(key) + 1);

            // If the key is in the node, return it directly
            this.log.info("the key '{}' should be present locally ({})", key, this.getAddress());
            result = this.getCacheSystem().get(key);
        } else {
            // If the key is not in the node, increment the keyMisses
            this.keyMisses.put(key, this.getKeyMisses(key) + 1);

            // If the key is not in the node, ask the node that owns the key
            String addressOfKey = this.getAddressOfPosition(keyPosition);
            if (addressOfKey == null) {
                // If the node that owns the key is not in the system anymore, return null (meaning that the key is not in the system)
                this.log.info("The node that used to own the key '{}' (position {}) is not in the system anymore", key, keyPosition);
                result = Optional.empty();
            } else {
                // If the node that owns the key is still in the system, ask it
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
            // If the key is in the node, store it directly in the node
            this.getCacheSystem().put(key, value);
            this.log.info("the key '{}' got successfully stored directly at {}", key, this.getAddress());
            return this.getAddress();
        } else {
            // If the key is not in the node, send it to the node that owns the key
            String addressOfKey = this.getAddressOfPosition(keyPosition);
            if (addressOfKey == null) {
                this.log.info("The node that used to own the key '{}' (position {}) is not in the system anymore", key, keyPosition);
                return null;
            } else {
                this.log.info("{} attends to store the key '{}' at node {} with position {}", this.getAddress(), key, addressOfKey, keyPosition);
                return this.post(addressOfKey, "/put", JSONObject.wrap(Map.of(key, value)).toString());
            }
        }
    }

    @Override
    public void leave(boolean rebalance) {
        this.log.info("{} at position {} is leaving", this.getAddress(), this.getPosition());

        if (rebalance) {
            this.removeNodeAtPosition(this.getPosition());

            // Create a map of the keys that need to be rebalanced
            // The key is the address of the node that will receive the keys
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

            // Send the keys and values to the nodes that will receive them, update their position and cache
            for (String address : this.knownHosts.values()) {
                Map<String, String> keyValues = new HashMap<>();
                Vector<String> keys = keysToRebalance.get(address);
                if (keys != null) {
                    for (String key : keysToRebalance.get(address)) {
                        keyValues.put(key, this.getCacheSystem().get(key).orElse(null));
                    }
                }

                this.post(address, "/rebalance", JSONObject.wrap(Map.of("position", this.getPosition(), "cache", JSONObject.wrap(keyValues).toString(), "updateKnownHosts", rebalance)).toString());
            }
        } else {
            this.knownHosts.remove(this.getPosition());
            for (String address : this.knownHosts.values()) {
                this.post(address, "/rebalance", JSONObject.wrap(Map.of("position", this.getPosition(), "updateKnownHosts", false)).toString());
            }
        }
    }

    /**
     * This method is used to send a POST request to an address with a path and a data
     *
     * @param address the address of the node
     * @param path    the path of the request
     * @param data    the data of the request
     * @return the response of the request as a string
     */
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
