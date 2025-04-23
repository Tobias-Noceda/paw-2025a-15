<%@ page isErrorPage="true" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html>
  <head>
    <meta charset="UTF-8">
    <title>
      <spring:message code="error.title" arguments="${param.errorCode}" />
    </title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <style>
      body {
        background-color: #a7dbff;
        display: flex;
        justify-content: center;
        align-items: center;
        height: 100vh;
        width: 100%;
        font-family: 'Segoe UI', sans-serif;
        margin: 0;
      }

      .container {
        display: flex;
        flex-direction: column;
        flex: 1;
        align-items: center;
        justify-content: center;
        padding: 30px;
      }

      img {
        max-width: 250px;
        margin-bottom: 20px;
      }

      h1 {
        font-size: 48px;
        color: #e74c3c;
        margin: 0;
      }

      p {
        font-size: 18px;
        color: #333;
        margin: 10px 0 30px;
      }

      a {
        text-decoration: none;
        color: #fff;
        background-color: #3498db;
        padding: 10px 20px;
        border-radius: 5px;
        transition: background-color 0.3s;
      }

      a:hover {
        background-color: #2980b9;
      }
    </style>
  </head>
  <body>
    <div class="container">
      <c:url value="/resources/errorImage.png" var="errorImage" />
      <img src="${errorImage}" alt="Error Illustration">
          
      <h1>
        <spring:message code="error.title" arguments="${param.errorCode}" />
      </h1>    
      <p>
        <spring:message code="error.${param.errorCode}.message" />
      </p>
      <c:url value="/" var="homeUrl" />
      <c:url value="/" var="loginUrl" />
      <c:choose>
        <c:when test="${param.errorCode == '403'}">
          <a href="${loginUrl}">
            <spring:message code="error.backToHome" />
          </a>
        </c:when>
        <c:otherwise>
          <a href="${homeUrl}">
            <spring:message code="error.backToHome" />
          </a>
        </c:otherwise>
      </c:choose> 
    </div>
  </body>
</html>