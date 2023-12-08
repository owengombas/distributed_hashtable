package org.stringmatching;

import javafx.util.Pair;
import org.distances.LevensteinDistance;
import org.file.FileUtils;
import org.knn.NearestNeighbours;

import java.io.IOException;
import java.sql.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class DomainsCHExercise {
    private Connection connection;
    private String[] domains;

    public Connection getConnection() {
        return connection;
    }

    public String[] getDomains() {
        return domains;
    }

    public double[][] getDomainsDouble() {
        return domainsDouble;
    }

    private double[][] domainsDouble;

    public DomainsCHExercise() {
    }

    public void connectToDatabase() throws SQLException {
        String url = "jdbc:postgresql://localhost:5432/postgres";
        String user = "postgres";
        String password = "postgres";
        this.connection = DriverManager.getConnection(url, user, password);
    }

    public void initializeDatabase() throws SQLException {
        try (Statement statement = this.connection.createStatement()) {
            statement.execute("DROP SCHEMA IF EXISTS domains CASCADE");
            statement.execute("CREATE SCHEMA domains");
            statement.execute("CREATE TABLE domains.domains (id SERIAL PRIMARY KEY, domain_name VARCHAR(255))");
        }
    }

    public void insertDomains(String filePath, int batchSize) throws IOException, SQLException {
        Set<String> domainSet = new HashSet<>(Arrays.asList(new FileUtils().readLines(filePath)));

        String sql = "INSERT INTO domains.domains (domain_name) VALUES (?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            int count = 0;
            for (String domain : domainSet) {
                statement.setString(1, domain);
                statement.addBatch();
                count++;

                if (count % batchSize == 0 || count == domainSet.size()) {
                    statement.executeBatch(); // Execute every <batchSize> items or at the end
                }
            }
        }

        System.out.println("Number of domains loaded: " + domainSet.size());
    }

    public void loadDomains() throws SQLException {
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM domains.domains")) {
            domains = new String[getDomainCount()];
            domainsDouble = new double[domains.length][];
            int i = 0;
            while (resultSet.next()) {
                domains[i] = resultSet.getString("domain_name");
                domainsDouble[i] = StringToDouble.convertToDouble(domains[i]);
                i++;
            }
        }
    }

    private int getDomainCount() throws SQLException {
        try (Statement countStatement = connection.createStatement();
             ResultSet resultSet = countStatement.executeQuery("SELECT COUNT(*) AS count FROM domains.domains")) {
            return resultSet.next() ? resultSet.getInt("count") : 0;
        }
    }
}
