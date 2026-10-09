// server.js - Express app showing all CRUD operations on MongoDB
const express = require('express');
const { ObjectId } = require('mongodb');
const { connect, events, client } = require('./db');

const app = express();
app.set('view engine', 'ejs');
app.use(express.urlencoded({ extended: true }));
app.use(express.static(__dirname + '/public'));   // serves style.css

const toId = (id) => (ObjectId.isValid(id) ? new ObjectId(id) : null);

// ---------- READ (all) : find() ----------
app.get('/', async (req, res) => {
  const filter = req.query.q
    ? { title: { $regex: req.query.q, $options: 'i' } }   // search by title
    : {};
  const list = await events().find(filter).sort({ date: 1 }).toArray();
  res.render('index', { events: list, q: req.query.q || '' });
});

// ---------- CREATE form ----------
app.get('/events/new', (req, res) => res.render('form', { event: null }));

// ---------- CREATE : insertOne() ----------
app.post('/events', async (req, res) => {
  const { title, venue, date, capacity, description } = req.body;
  await events().insertOne({
    title, venue, description,
    date: new Date(date),
    capacity: Number(capacity),
    attendees: [],
    createdAt: new Date(),
    source: 'node'
  });
  res.redirect('/');
});

// ---------- READ (one) : findOne() ----------
app.get('/events/:id', async (req, res) => {
  const event = await events().findOne({ _id: toId(req.params.id) });
  if (!event) return res.status(404).send('Event not found');
  res.render('event', { event, error: req.query.error });
});

// ---------- UPDATE form ----------
app.get('/events/:id/edit', async (req, res) => {
  const event = await events().findOne({ _id: toId(req.params.id) });
  if (!event) return res.status(404).send('Event not found');
  res.render('form', { event });
});

// ---------- UPDATE : updateOne() with $set ----------
app.post('/events/:id/update', async (req, res) => {
  const { title, venue, date, capacity, description } = req.body;
  await events().updateOne(
    { _id: toId(req.params.id) },
    { $set: { title, venue, description, date: new Date(date),
              capacity: Number(capacity), updatedAt: new Date() } }
  );
  res.redirect('/events/' + req.params.id);
});

// ---------- DELETE : deleteOne() ----------
app.post('/events/:id/delete', async (req, res) => {
  await events().deleteOne({ _id: toId(req.params.id) });
  res.redirect('/');
});

// ---------- Register attendee : UPDATE with $push (array) ----------
app.post('/events/:id/register', async (req, res) => {
  const { name, email } = req.body;
  // Only push if capacity not reached and email not already registered
  const result = await events().updateOne(
    {
      _id: toId(req.params.id),
      'attendees.email': { $ne: email },
      $expr: { $lt: [{ $size: '$attendees' }, '$capacity'] }
    },
    { $push: { attendees: { name, email, registeredAt: new Date() } } }
  );
  const error = result.modifiedCount === 0 ? '?error=Event full or email already registered' : '';
  res.redirect('/events/' + req.params.id + error);
});

// ---------- Cancel registration : UPDATE with $pull ----------
app.post('/events/:id/unregister', async (req, res) => {
  await events().updateOne(
    { _id: toId(req.params.id) },
    { $pull: { attendees: { email: req.body.email } } }
  );
  res.redirect('/events/' + req.params.id);
});

const PORT = process.env.PORT || 3000;
connect()
  .then(() => app.listen(PORT, () => console.log(`Node app on http://localhost:${PORT}`)))
  .catch((err) => { console.error('MongoDB connection failed:', err.message); process.exit(1); });

process.on('SIGINT', async () => { await client.close(); process.exit(0); });
