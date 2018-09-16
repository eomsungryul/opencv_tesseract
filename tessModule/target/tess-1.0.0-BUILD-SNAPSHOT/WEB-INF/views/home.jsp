<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page session="false" %>
<html>
<head>
	<title>Home</title>
</head>
<script src="./resources/jquery-3.1.0.js " type="text/javascript" charset="UTF-8"></script>
<script>
$(document).ready(function(){
    if (!('url' in window) && ('webkitURL' in window)) {
        window.URL = window.webkitURL;
    }
 
    $('#camera').change(function(e){
        $('#pic').attr('src', URL.createObjectURL(e.target.files[0]));
    });
});

function getContextPath() {
	var hostIndex = location.href.indexOf( location.host ) + location.host.length;
	return location.href.substring( hostIndex, location.href.indexOf('/', hostIndex + 1) );
};

/**
 *  게시판 등록 
 */
function fnConvert(){
	
		var params = {
						  "userId": "0123456789", //사용자ID
						  "fitnessId": "222132", //헬스장ID
						  "fileLocation": "532EO30FD.jpg", //파일명
						  "apikey" :"dwebss" //발급받은 api키+ 사용자ID 조합한 값
						}
	 jQuery.ajax({
	 url: getContextPath() + "/converter/",
	 type: 'POST',
	 data:params,
	 contentType: 'application/x-www-form-urlencoded; charset=UTF-8', 
	 dataType: 'json',
	 success: function (result) {
	     if (result){
	     }
	 }
	});
		
}


/**
 *  게시판 등록 
 */
function fnInsert(){
	if(!confirm("등록하시겠습니까?")) return;
	document.registFrm.action = getContextPath() + "/converter/";
	document.registFrm.submit();
// 	 var params;

// 	 params = new FormData();
// 	 params.append('file', $('#camera')[0].files[0]);
// 	 params.append('x', $('#x').val());
// 	 params.append('y', $('#y').val());
	

}
</script>
<body>
<h1>
	Hello world!  
</h1>

<P>  The time on the server is ${serverTime}. </P>

<button type="button" class="mbtn btn_purple" onclick="fnInsert(); return false;">등록</button> 

<button type="button" class="mbtn btn_purple" onclick="fnConvert(); return false;">convert 요청</button> 
<form:form modelAttribute="boardMap" id="registFrm" name="registFrm"  enctype="multipart/form-data">

x : <input type="text" id="x" name="x" value="5" />
y : <input type="text" id="y" name="y" value="12" />

<input type="file" id="camera" name="camera" capture="camera" accept="image/*" />
<br />
 
<img id="pic" style="width:100%;" />
</form:form>
 

</body>
</html>
