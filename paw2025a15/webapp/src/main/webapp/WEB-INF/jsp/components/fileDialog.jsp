<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<html>
  <head>
    <meta charset="UTF-8"/>
    <link rel="stylesheet" href="<c:url value="/css/main.css"/>"/>
  </head>
  <body>
    <dialog id="fileDialog" class="file-dialog">
      <div class="file-dialog-header">
        <h2 class="file-dialog-title"><spring:message code="studies.fileDialog.title"/></h2>
        <button id="closeFileDialog" class="cancel-button"><spring:message code="studies.fileDialog.close"/></button>
      </div>
      <iframe id="fileViewer" src="" frameborder="0" class="file-viewer"></iframe>
    </dialog>

    <script src="<c:url value="/js/fileDialogModal.js"/>"></script>
  </body>
</html>