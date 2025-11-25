<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html>
  <head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>
      <c:choose>
        <c:when test="${isEdit}"><spring:message code="admin.insurance.form.title.edit"/></c:when>
        <c:otherwise><spring:message code="admin.insurance.form.title.new"/></c:otherwise>
      </c:choose>
    </title>
    <link rel="icon" type="image/png" href="<c:url value='/favicon.ico'/>" />
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="<c:url value='/css/base.css'/>">
    <link rel="stylesheet" href="<c:url value='/css/insurance.css'/>">
    <style>


    </style>
  </head>
  <body>
    <jsp:include page="../components/header.jsp"/>
    <div class="page-container">
      <!-- Cabecera de página -->
      <div class="page-header">
        <h1>
          <c:choose>
            <c:when test="${isEdit}"><spring:message code="admin.insurance.form.title.edit"/></c:when>
            <c:otherwise><spring:message code="admin.insurance.form.title.new"/></c:otherwise>
          </c:choose>
        </h1>
      </div>

      <!-- Formulario -->
      <div class="insurance-form-container">
        <c:set var="formURL">
          <c:choose>
            <c:when test='${isEdit}'><c:url value='/admin/insurances/edit/${insurance.id}'/></c:when>
            <c:otherwise><c:url value='/admin/insurances/new'/></c:otherwise>
          </c:choose>
        </c:set>
        <form:form
          action="${formURL}" 
          method="POST" 
          enctype="multipart/form-data"
          modelAttribute="insuranceForm"
        >
          
          <!-- Campo nombre -->
          <div class="form-group">
            <c:set var="currentName">
              <c:if test='${isEdit}'><c:out value='${insurance.name}' escapeXml='true'/></c:if>
            </c:set>
            <form:label path="name"><spring:message code="admin.insurance.form.name"/></form:label>
            <c:set var="placeholderText">
              <spring:message code='admin.insurance.form.name.placeholder'/>
            </c:set>
            <form:input
              type="text" 
              id="name"
              name="name" 
              path="name"
              class="form-control" 
              value="${currentName}"
              maxlength="100"
              placeholder="${placeholderText}"
            />
          </div>

          <!-- Campo imagen -->
          <div class="form-group">
            <form:label path="picture"><spring:message code='admin.insurance.form.picture'/></form:label>
            <form:input 
              type="file" 
              id="picture" 
              name="picture" 
              class="form-control" 
              accept=".png,.jpg,.jpeg"
              path="picture"
            />
            <small style="color: #666; font-size: 14px; margin-top: 5px; display: block;">
              <spring:message code="admin.insurance.form.picture.allowedTypes"/>
              <c:if test="${!isEdit}">
                <spring:message code="admin.insurance.form.picture.default"/>
              </c:if>
            </small>
              
            <!-- Mostrar imagen actual si es edición -->
            <c:if test="${isEdit && insurance.picture != null}">
              <div class="current-image">
                <p style="margin: 0 0 10px 0; font-weight: 600;"><spring:message code="admin.insurance.form.picture.current"/></p>
                <img
                  src="<c:url value='/supersecret/insurance-picture/${insurance.id}'/>" 
                  alt="<spring:message code='admin.insurance.alt' arguments="${insurance.name}"/>"
                >
                <p style="margin: 10px 0 0 0; font-size: 14px; color: #666;">
                  <spring:message code="admin.insurance.form.picture.update"/>
                </p>
              </div>
            </c:if>
          </div>

          <!-- Botones de acción -->
          <div class="form-actions">
            <button type="submit" class="save-button">
              <c:choose>
                <c:when test="${isEdit}"><spring:message code="admin.insurance.form.button.save"/></c:when>
                <c:otherwise><spring:message code="admin.insurance.form.button.create"/></c:otherwise>
              </c:choose>
            </button>
            <a href="<c:url value='/admin/home'/>" class="btn btn-secondary">
              <spring:message code="admin.insurance.form.button.cancel"/>
            </a>
          </div>
        </form:form>
      </div>
    </div>

    <script>
        // Validación del formulario
        document.querySelector('form').addEventListener('submit', function(e) {
            const name = document.getElementById('name').value.trim();
            
            if (!name) {
                e.preventDefault();
                alert('El nombre de la obra social es obligatorio.');
                document.getElementById('name').focus();
                return false;
            }
            
            if (name.length > 100) {
                e.preventDefault();
                alert('El nombre de la obra social no puede exceder los 100 caracteres.');
                document.getElementById('name').focus();
                return false;
            }
            
            // Validar archivo si se seleccionó
            const fileInput = document.getElementById('picture');
            if (fileInput.files.length > 0) {
                const file = fileInput.files[0];
                const maxSize = 5 * 1024 * 1024; // 5MB
                const allowedTypes = ['image/png', 'image/jpeg', 'image/jpg', 'image/gif'];
                
                if (file.size > maxSize) {
                    e.preventDefault();
                    alert('El archivo es demasiado grande. El tamaño máximo permitido es 5MB.');
                    return false;
                }
                
                if (!allowedTypes.includes(file.type)) {
                    e.preventDefault();
                    alert('Formato de archivo no válido. Solo se permiten PNG, JPG, JPEG y GIF.');
                    return false;
                }
            }
            
            return true;
        });
    </script>
  </body>
</html> 