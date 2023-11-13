package org.ht.distributed;

import org.ht.hashtable.Hashtable;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HashtableNodeRest implements HashtableNode<String, String> {
    private final Hashtable<String, String> cacheSystem;
    private final HttpClient client;
    private final Javalin server;
    private final Logger log;
    private final Vector<String> knownHosts = new Vector<>();
    private final String id;

    public HashtableNodeRest(int port, Hashtable<String, String> cacheSystem, String id) {
        this.id = id;
        this.cacheSystem = cacheSystem;
        this.client = HttpClient.newHttpClient();
        this.server = Javalin.create().start("127.0.0.1", port);
        this.log = LoggerFactory.getLogger(String.format("%s@%s", this.getId() , this.getAddress()));

        this.initializeRestApi();
    }

    private void initializeRestApi() {
        this.server.post("/join", this::onJoin);
        this.server.post("/get", this::onGet);
        this.server.post("/put", this::onPut);
    }

    private void onJoin(Context context) {
        String newNodeAddress = context.body();
        this.knownHosts.add(newNodeAddress);
        this.log.info("{} joined {} successfully", newNodeAddress, this.getAddress());
    }

    private void onPut(Context context) {
        String[] keyValue = context.body().split(":", 2);

        String result = "";
        if (keyValue.length != 2) {
            this.log.info("The key value pair '{}' isn't formatted correctly", context.body());
        } else {
            result = this.getAddress();
            this.getCacheSystem().put(keyValue[0], keyValue[1]);
            this.log.info("{} successfully received stored the key value pair ('{}', '{}')", this.getAddress(), keyValue[0], keyValue[1]);
        }

        context.result(result);
    }

    private void onGet(Context context) {
        String key = context.body();
        String result = this.getCacheSystem().get(key).orElse("");

        this.log.info("{} received a ask request for the key '{}' and answered '{}'", this.getAddress(), key, result);
        context.result(result);
    }

    private Optional<String> parseAnswer(String answer) {
        if (answer == null) return Optional.empty();
        if (answer.isEmpty()) return Optional.empty();
        return Optional.of(answer);
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public Vector<String> getKnownHosts() {
        return this.knownHosts;
    }

    @Override
    public String getAddress() {
        return String.format("http://%s:%s", this.server.jettyServer().getServerHost(), this.server.jettyServer().getServerPort());
    }

    @Override
    public String join(String nodeToJoinAddress) {
        this.log.info("{} asks to join {}", this.getAddress(), nodeToJoinAddress);
        return this.sendRequest(nodeToJoinAddress, "/join", this.getAddress());
    }

    @Override
    public Optional<String> ask(String address, String key) {
        this.log.info("{} sent a ask request to {} for the key '{}'", this.getAddress(), address, key);

        String answerString = this.sendRequest(address, "/get", String.valueOf(key));
        Optional<String> answer = this.parseAnswer(answerString);

        if (answer.isEmpty()) {
            this.log.info("{} sent a ask request to {} for the key '{}' and received no values (doesn't own that key)", this.getAddress(), address, key);

        } else {
            this.log.info("{} sent a ask request to {} for the key '{}' and received '{}'", this.getAddress(), address, key, answer.get());
        }

        return answer;
    }

    @Override
    public Optional<String> askAll(String key) {
        Optional<String> answer = Optional.empty();

        for (String nodeAddress: this.knownHosts) {
            answer = this.ask(nodeAddress, key);
        }

        return answer;
    }

    @Override
    public Optional<String> get(String key) {
        Optional<String> result = this.getCacheSystem().get(key);

        this.log.info("{} got asked key '{}'", this.getAddress(), key);

        if (result.isEmpty()) {
            this.log.info("{} do not have key '{}' -> ask all known hosts", this.getAddress(), key);
            result = this.askAll(key);
        }

        if (result.isEmpty()) {
            this.log.info("The distributed HT doesn't own the key {}", key);
        }

        return result;
    }

    @Override
    public String put(String key, String value) {
        String destinationNodeAddress = this.getPutNodeAddress(key);

        this.log.info("{} attends to store the key '{}' at {}", this.getAddress(), key, destinationNodeAddress);

        if (this.getAddress().equals(destinationNodeAddress)) {
            this.getCacheSystem().put(key, value);
            return this.getAddress();
        }

        String answerString = this.sendRequest(destinationNodeAddress, "/put", String.format("%s:%s", key, value));

        this.log.info("the key '{}' got successfully stored at {}", key, destinationNodeAddress);

        return answerString;
    }

    @Override
    public String getPutNodeAddress(String key) {
        int index = key.hashCode() % (this.getKnownHosts().size() + 1) - 1;

        if (index == -1) {
            return this.getAddress();
        }

        return this.getKnownHosts().get(index);
    }

    @Override
    public String leave() {
        return null;
    }

    @Override
    public Hashtable<String, String> getCacheSystem() {
        return this.cacheSystem;
    }

    private String sendRequest(String address, String path, String data) {
        try {
            var request = HttpRequest.newBuilder(URI.create(String.format("%s%s", address, path)))
                    .header("accept", "text/plain")
                    .POST(HttpRequest.BodyPublishers.ofString(String.format("%s", data, this.getAddress())))
                    .build();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
