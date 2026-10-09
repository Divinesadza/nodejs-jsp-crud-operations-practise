require('dotenv').config();
const { MongoClient } = require('mongodb');

const client = new MongoClient(process.env.MONGODB_URI);
let db;

async function connect() {
  if (db) return db;
  await client.connect();                       // opens the connection pool to Atlas
  db = client.db(process.env.DB_NAME || 'event_registration');
  await db.command({ ping: 1 });                // confirms the connection works
  console.log('Connected to MongoDB Atlas:', db.databaseName);
  return db;
}

const events = () => db.collection('events');

module.exports = { connect, events, client };
