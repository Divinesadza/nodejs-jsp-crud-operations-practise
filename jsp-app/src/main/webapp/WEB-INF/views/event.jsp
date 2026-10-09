<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!doctype html>
<html><head><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1">
<title><c:out value="${event.title}"/></title><link rel="stylesheet" href="style.css"></head>
<body><div class="wrap" style="max-width:760px">
<a href="events">&larr; All events</a>
<c:set var="taken" value="${event.attendees.size()}"/>
<header class="hero">
  <h1><c:out value="${event.title}"/></h1>
  <p><c:out value="${event.venue}"/> &nbsp;·&nbsp; <fmt:formatDate value="${event.date}" pattern="EEE dd MMM yyyy"/>
     &nbsp;·&nbsp; <a href="events?action=edit&id=${event._id}">Edit</a></p>
</header>
<div class="panel">
  <p><c:out value="${event.description}"/></p>
  <div class="meter"><i style="width:${taken * 100 / event.capacity}%"></i></div>
  <p class="meta">${taken} / ${event.capacity} going · ${event.capacity - taken} spots left</p>
</div>
<div class="panel">
  <h2 style="margin-top:0">Register</h2>
  <c:if test="${not empty param.error}"><p class="error"><c:out value="${param.error}"/></p></c:if>
  <form class="row" method="post" action="events">
    <input type="hidden" name="action" value="register"><input type="hidden" name="id" value="${event._id}">
    <input name="name" placeholder="Your name" required>
    <input type="email" name="email" placeholder="Email" required>
    <button>Register</button>
  </form>
</div>
<div class="panel">
  <h2 style="margin-top:0">Who's going</h2>
  <c:if test="${taken == 0}"><p class="meta">No one registered yet.</p></c:if>
  <ul class="people">
  <c:forEach var="a" items="${event.attendees}">
    <li><div class="avatar">${fn:toUpperCase(a.name.substring(0,1))}</div>
        <div class="who"><c:out value="${a.name}"/><small><c:out value="${a.email}"/></small></div>
        <form method="post" action="events">
          <input type="hidden" name="action" value="unregister"><input type="hidden" name="id" value="${event._id}">
          <input type="hidden" name="email" value="<c:out value='${a.email}'/>">
          <button class="danger small">Cancel</button></form></li>
  </c:forEach>
  </ul>
</div>
</div></body></html>
