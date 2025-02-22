<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Tailwind CSS with Tomcat</title>
    <link href="${pageContext.request.contextPath}/css/output.css" rel="stylesheet">
</head>
<body>
    <%@ include file="navbar.jsp" %>
    <div class="container mx-auto p-4">
        <h1 class="text-3xl font-bold text-blue-600">
            Hello Tailwind CSS!
        </h1>
    </div>
</body>
</html>