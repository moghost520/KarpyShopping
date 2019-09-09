<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>登出</title>




</head>
<body>
	<!-- 先將使用者名稱取出 -->
	<c:set var="managerName" value="${ memberLoginok.name }" />
	<!-- 移除放在session物件內的屬性物件 -->
	<c:remove var="memberLoginOK" scope="session" />
	<!-- 下列敘述設定變數funcName的值為OUT，top.jsp 會用到此變數 -->
	<c:set var="funcName" value="OUT" scope="session" />
	<!-- 引入共同的頁首 -->
	<!-- 下列六行敘述設定登出後要顯示的感謝訊息 -->
	<c:set var="logoutMessage" scope="request" />
	<font color='blue'><BR> 會員${ memberName }，感謝您使用本系統。<BR>
		您已經登出<BR></font>


	<p>
		<a href="<spring:url value='index1' />" class="btn btn-default"> 回首頁 </a>
	</p>
</body>
</html>