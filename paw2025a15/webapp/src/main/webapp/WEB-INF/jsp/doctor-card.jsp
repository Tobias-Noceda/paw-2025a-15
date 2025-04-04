<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<html>
<head>
    <title>Title</title>
    <link rel="stylesheet" href="<c:url value="css/landing-page.css"/>">
</head>
<body>
<%-- userCard.jsp --%>
<div class="doctorLanding-card">
    <a href="#" class="clickable-card">
        <h3>${param.doctorName}</h3>
        <p>${param.speciality}</p>
        <p>${fn:replace(fn:replace(param.workingEnsurances, "[", ""), "]", "")}</p>

    </a>
</div>

</body>
</html>
