package org.sber.storage;

import java.sql.*;

public class DatabaseStorage implements Storage {
    private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS cache (id SERIAL PRIMARY KEY, name TEXT UNIQUE NOT NULL, result TEXT NOT NULL)";
    private static final String CREATE_INDEX = "CREATE INDEX IF NOT EXISTS cache_index ON cache (name)";
    private static final String INSERT_INTO_CACHE = "INSERT INTO cache (name, result) VALUES (?, ?) ON CONFLICT DO NOTHING";
    private static final String SELECT_FROM_CACHE = "SELECT result FROM cache WHERE name = ?";

    private final Connection connection;

    public DatabaseStorage(Connection connection) {
        this.connection = connection;
        createTable();
    }

    private void createTable() {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(CREATE_TABLE);
            stmt.execute(CREATE_INDEX);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object get(Object key) {
        try (PreparedStatement stmt = connection.prepareStatement(SELECT_FROM_CACHE)) {
            stmt.setString(1, Converter.toString(key));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Converter.fromString(rs.getString("result"));
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void store(Object key, Object value) {
        try (PreparedStatement stmt = connection.prepareStatement(INSERT_INTO_CACHE)) {
            stmt.setString(1, Converter.toString(key));
            stmt.setString(2, Converter.toString(value));
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}