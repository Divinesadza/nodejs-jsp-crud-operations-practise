<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html>
<html><head><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1">
<title>${empty event ? 'New' : 'Edit'} Event</title><link rel="stylesheet" href="style.css"></head>
<body><div class="wrap" style="max-width:640px">
<a href="events">&larr; Back to events</a>
<header class="hero"><h1>${empty event ? 'New event' : 'Edit event'}</h1><p>Fill in the event details.</p></header>
<form class="panel" method="post" action="events">
  <input type="hidden" name="action" value="${empty event ? 'create' : 'update'}">
  <c:if test="${not empty event}"><input type="hidden" name="id" value="${event._id}"></c:if>
  <label>Event name <input name="title" required value="<c:out value='${event.title}'/>" placeholder="Friday Rooftop Jam"></label>
  <label>Venue <input name="venue" required value="<c:out value='${event.venue}'/>" placeholder="Harare"></label>
  <div class="row">
    <label style="flex:1">Date <input type="date" name="date" required value="${dateValue}"></label>
    <label style="flex:1">Capacity <input type="number" min="1" name="capacity" required value="${empty event ? 50 : event.capacity}"></label>
  </div>
  <label>Description <textarea name="description" rows="4"><c:out value="${event.description}"/></textarea></label>
  <button>${empty event ? 'Create event' : 'Save changes'}</button>
  <a class="btn ghost" href="events">Cancel</a>
</form>
</div></body></html>
