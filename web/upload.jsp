<%@ page language="java" import="java.util.*" pageEncoding="utf-8" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
</head>
<body>
<h1>文件上传</h1>
<hr>
<form action="${pageContext.request.contextPath }/upload" method="POST" enctype="multipart/form-data">
    描述信息1:<input type="text" name="description1"/>
    描述信息2:<input type="text" name="description2"/>
    <input type="file" name="file1"/>
    <input type="submit" value="上传"/>
</form>
<% String p = request.getHeader("percent");
    out.print(p);
%>
</body>
</html>