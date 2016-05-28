<%@ page language="java" import="java.util.*" pageEncoding="utf-8" %>
<%@ page import="java.io.File" %>
<%@ page import="top.wuhaojie.constant.Constant" %>
<%@ page import="top.wuhaojie.utils.ConfigUtils" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
</head>
<body>
<img src="imgs/backgroud.png">
<h1>文件上传</h1>
<hr>
<form id="form" action="/upload" method="post" enctype="multipart/form-data">
    <input id="file" type="file" name="file1"/>
</form>

<form action="#" name="mainForm">
    <fieldset>
        <legend>请选择文件类型</legend>
        <p>
            <label><input type="radio" name="option[]" value="kml"/>Kml格式</label>
            <label><input type="radio" name="option[]" value="gpx"/>Gpx格式</label>
            <label><input type="radio" name="option[]" value="xml"/>Xml格式</label>
        </p>

    </fieldset>
</form>

<button onclick="upload()">上传</button>
<button onclick="download()">下载</button>
<script type="application/javascript">

    function download() {
        <% System.out.println("download()调用");%>
        var fileType = document.getElementsByName('option[]');
        var addrPostfix = "download-kml";
        if (fileType[0].checked) {
            addrPostfix = "download-kml";
        } else if (fileType[1].checked) {
            addrPostfix = "download-gpx";
        } else if (fileType[2].checked) {
            addrPostfix = "download-xml";
        } else {
            alert("请选择需要下载的文件类型!");
            return;
        }


        <%
        File file = new File(Constant.FILE_PATH);
        boolean fileExists = file.exists();
        boolean finished = Constant.ATTR_TRUE.equals(ConfigUtils.readConfig(Constant.CONFIG_FINISHED));
        if(fileExists && finished){
        %>
        window.location.href = "${pageContext.servletContext.contextPath}/" + addrPostfix;
        <% } else { %>
        alert("请先上传文件");
        <% } %>

    }
    function upload() {
        <% System.out.println("upload() 调用"); %>
        if (document.getElementById("file").value == "") {
            alert("请先选择文件");
            return;
        }
        document.getElementById("form").submit();
    }

</script>
</body>
</html>