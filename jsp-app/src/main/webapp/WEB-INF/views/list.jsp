<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!doctype html>
<html><head><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1">
<title>Events</title><link rel="stylesheet" href="style.css"></head>
<body><div class="wrap">
<header class="hero">
  <h1>Out <em>tonight</em></h1>
  <p>Upcoming events around town.</span></p>
</header>
<form class="bar" method="get" action="events">
  <input name="q" value="<c:out value='${param.q}'/>" placeholder="Search events...">
  <button class="ghost">Search</button>
  <a class="btn" href="events?action=new">+ New event</a>
</form>
<c:if test="${empty events}"><div class="empty">No events yet.</div></c:if>
<div class="grid">
<c:forEach var="e" items="${events}">
  <c:set var="taken" value="${e.attendees.size()}"/>
  <div class="card">
    <div class="top">
      <div class="date-badge"><b><fmt:formatDate value="${e.date}" pattern="d"/></b><span><fmt:formatDate value="${e.date}" pattern="MMM"/></span></div>
      <h3><a href="events?action=view&id=${e._id}"><c:out value="${e.title}"/></a></h3>
    </div>
    <div class="body">
      <p class="meta"><c:out value="${e.venue}"/></p>
      <div class="meter"><i style="width:${taken * 100 / e.capacity}%"></i></div>
      <p class="meta">${taken} / ${e.capacity} going</p>
      <div class="actions">
        <a class="btn small" href="events?action=view&id=${e._id}">View</a>
        <a class="btn ghost small" href="events?action=edit&id=${e._id}">Edit</a>
        <form method="post" action="events" onsubmit="return confirm('Delete this event?')">
          <input type="hidden" name="action" value="delete"><input type="hidden" name="id" value="${e._id}">
          <button class="danger small">Delete</button></form>
      </div>
    </div>
  </div>
</c:forEach>
</div>
</div></body></html>
