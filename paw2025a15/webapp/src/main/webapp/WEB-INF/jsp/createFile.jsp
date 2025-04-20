<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<html>
<head>
    
</head>
<body>
<h3>hola</h3>
<h1>subi archivo</h1>
<form action="${pageContext.request.contextPath}/supersecret/file" method="post" enctype="multipart/form-data" name>
    <input name="file" type="file" accept=".png, .jpg, .jpeg, .pdf">
    <button type="submit">send</button>
</form>
</body>

</html>