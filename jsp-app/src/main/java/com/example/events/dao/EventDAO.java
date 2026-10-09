package com.example.events.dao;

import com.example.events.db.MongoUtil;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

/** All database work lives here: Create, Read, Update, Delete. */
public class EventDAO {

    private MongoCollection<Document> col() { return MongoUtil.events(); }

    private static Bson byId(String id) { return Filters.eq("_id", new ObjectId(id)); }

    // ---------- CREATE : insertOne ----------
    public String create(String title, String venue, Date date, int capacity, String description) {
        Document doc = new Document("title", title)
                .append("venue", venue)
                .append("date", date)
                .append("capacity", capacity)
                .append("description", description)
                .append("attendees", new ArrayList<Document>())
                .append("createdAt", new Date())
                .append("source", "jsp");
        col().insertOne(doc);
        return doc.getObjectId("_id").toHexString();
    }

    // ---------- READ all : find ----------
    public List<Document> findAll(String search) {
        Bson filter = (search == null || search.isBlank())
                ? new Document()
                : Filters.regex("title", Pattern.quote(search), "i");
        return col().find(filter).sort(Sorts.ascending("date")).into(new ArrayList<>());
    }

    // ---------- READ one : find().first() ----------
    public Document findById(String id) {
        if (!ObjectId.isValid(id)) return null;
        return col().find(byId(id)).first();
    }

    // ---------- UPDATE : updateOne with $set ----------
    public boolean update(String id, String title, String venue, Date date, int capacity, String description) {
        UpdateResult r = col().updateOne(byId(id), Updates.combine(
                Updates.set("title", title),
                Updates.set("venue", venue),
                Updates.set("date", date),
                Updates.set("capacity", capacity),
                Updates.set("description", description),
                Updates.set("updatedAt", new Date())));
        return r.getMatchedCount() == 1;
    }

    // ---------- DELETE : deleteOne ----------
    public boolean delete(String id) {
        return col().deleteOne(byId(id)).getDeletedCount() == 1;
    }

    // ---------- Register attendee : updateOne with $push ----------
    public boolean register(String id, String name, String email) {
        Bson filter = Filters.and(
                byId(id),
                Filters.ne("attendees.email", email),                       // no duplicates
                Filters.expr(new Document("$lt", List.of(                  // capacity check
                        new Document("$size", "$attendees"), "$capacity"))));
        Document attendee = new Document("name", name).append("email", email)
                .append("registeredAt", new Date());
        return col().updateOne(filter, Updates.push("attendees", attendee)).getModifiedCount() == 1;
    }

    // ---------- Cancel registration : updateOne with $pull ----------
    public void unregister(String id, String email) {
        col().updateOne(byId(id), Updates.pull("attendees", new Document("email", email)));
    }
}
