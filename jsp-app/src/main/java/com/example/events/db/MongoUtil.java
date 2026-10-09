package com.example.events.db;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public final class MongoUtil {
    private static MongoClient client;
    private static MongoDatabase database;

    private MongoUtil() {}

    public static synchronized MongoDatabase getDatabase() {
        if (database == null) {
            String uri = setting("MONGODB_URI");
            if (uri == null || uri.isBlank()) {
                throw new IllegalStateException("MONGODB_URI not found. Put it in node-app/.env or set it as an environment variable.");
            }
            String dbName = setting("DB_NAME");
            if (dbName == null || dbName.isBlank()) dbName = "event_registration";
            client = MongoClients.create(uri);
            database = client.getDatabase(dbName);
            database.runCommand(new Document("ping", 1));   // confirms the connection
            System.out.println("Connected to MongoDB Atlas: " + dbName);
        }
        return database;
    }

    private static String setting(String key) {
        String v = System.getenv(key);
        if (v != null && !v.isBlank()) return v;
        String file = System.getProperty("env.file");
        if (file == null) return null;
        try {
            for (String line : java.nio.file.Files.readAllLines(java.nio.file.Path.of(file))) {
                line = line.trim();
                if (line.startsWith(key + "=")) return line.substring(key.length() + 1).trim();
            }
        } catch (java.io.IOException e) {
            System.err.println("Could not read " + file + ": " + e.getMessage());
        }
        return null;
    }

    public static MongoCollection<Document> events() {
        return getDatabase().getCollection("events");
    }

    public static synchronized void close() {
        if (client != null) { client.close(); client = null; database = null; }
    }
}
