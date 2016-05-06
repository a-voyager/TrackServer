<%@ page language="java" import="java.util.*" pageEncoding="utf-8" %>
<%@ page import="java.io.File" %>
<%@ page import="top.wuhaojie.constant.Constant" %>
<%@ page import="top.wuhaojie.utils.ConfigUtils" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
</head>
<body>
<h1>文件上传</h1>
<hr>
<form id="form" action="/upload" method="post" enctype="multipart/form-data">
    <input id="file" type="file" name="file1"/>
</form>
<button onclick="upload()">上传</button>
<button onclick="download()">下载</button>
<script>
    function download() {
        <%
        File file = new File(Constant.FILE_PATH);
        boolean fileExists = file.exists();
        boolean finished = Constant.ATTR_TRUE.equals(ConfigUtils.readConfig(Constant.CONFIG_FINISHED));
        if(fileExists && finished){
        %>
        window.location.href = "${pageContext.servletContext.contextPath}/download";
        <% } else { %>
        alert("请先上传文件");
        <% } %>

    }
    function upload() {
        if (document.getElementById("file").value == "") {
            alert("请先选择文件");
            return;
        }
        document.getElementById("form").submit();
    }

</script>

</html>