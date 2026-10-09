# Event Registration using Node.js & JSP with MongoDB Atlas
This was created as a practise project to understand crud operations.
The two apps, one shared database (`event_registration`, collection `events`).
Add an event in one app and it appears in the other.

For MongoDb, you can think of collection(events) as a table, document(event) as a record

## 1. Set up MongoDB Atlas
1. Create a free M0 cluster at cloud.mongodb.com.
2. Database Access → add a user + password.
3. Network Access → add your IP (or 0.0.0.0/0 for testing).
4. Connect → Drivers → copy the `mongodb+srv://...` string copied the legacy connection string.

## Data model (one document per event)
```json
{
  "_id": ObjectId,
  "title": "Tech Meetup", "venue": "Harare ICC", "date": ISODate,
  "capacity": 50, "description": "...",
  "attendees": [ { "name": "Tendai", "email": "t@x.com", "registeredAt": ISODate } ],
  "createdAt": ISODate, "updatedAt": ISODate, "source": "node" | "jsp"
}
```

## CRUD map
| Operation | Node.js (`server.js`) | JSP (`EventDAO.java`) |
|---|---|---|
| Create event | `insertOne()` | `insertOne()` |
| Read all / search | `find()` + `$regex` + `sort` | `find(Filters.regex)` + `Sorts` |
| Read one | `findOne({_id})` | `find(eq("_id")).first()` |
| Update event | `updateOne` + `$set` | `updateOne` + `Updates.set` |
| Delete event | `deleteOne()` | `deleteOne()` |
| Register attendee | `updateOne` + `$push` (with capacity & duplicate check via `$expr`/`$ne`) | `Updates.push` + `Filters.expr` |
| Cancel registration | `updateOne` + `$pull` | `Updates.pull` |

## 2. Run the Node.js app
1. npm install to install required dependencies
2. Added connection string to .env used the legacy connection string
3. npm start - check http://localhost:3000

## 3. Run the JSP app (Java 17+, Maven, Tomcat 10.1+)
1. first downloaded java via winget install EclipseAdoptium.Temurin.21.JDK in the terminal
2. then secondly ran this command run.bat
3. Continuosly checked http://localhost:8080/events


## How the connection works
- **Node:** `db.js` creates one `MongoClient`, calls `connect()` once at startup, pings, then every route reuses `db.collection('events')`.
- **JSP:** `MongoUtil` holds one thread-safe `MongoClient` (built-in connection pool). `MongoLifecycleListener` opens it when Tomcat starts the app and closes it on shutdown. Servlet → DAO → MongoDB, JSP only displays.

