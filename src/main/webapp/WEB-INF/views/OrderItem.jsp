<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<html>
<head>
<meta charset="UTF-8">
<link rel="stylesheet"
    href="//netdna.bootstrapcdn.com/bootstrap/3.0.0/css/bootstrap.min.css">
<title>Products</title>
</head>
<body>
    <section>
        <div>
            <div class="container" style="text-align: center" >
                <h1>訂單項目</h1>
            </div>
        </div>
    </section>
    <hr style="height:1px;border:none;color:#333;background-color:#333;">
    <section class="container">
        <div class="row">
          <c:forEach var='OrderItem' items='${OrderItem}'>
            <div class="col-sm-6 col-md-3" style="width: 360px; height: 360px">
                <div class="thumbnail" style="width: 320px; height: 340px">
                    <div class="caption">
                        <p>
                            <b style='font-size: 16px;'>訂單號: ${OrderItem.orderNo}</b>
                        </p>
                        <p>流水號: ${OrderItem.seqno}</p>
<%--                         <p>${OrderItem.}</p> --%>
<%--                         <p>目前在庫數量: ${OrderItem.}本</p> --%>
<!--                         <p> -->
                        	<a href="<spring:url value='OrderItems?id=${OrderItem.seqno}' />"
    							class="btn btn-primary">
    							<span class="glyphicon-info-sigh glyphicon"></span>詳細資料
 							</a>
<!--                         </p> -->
                    </div>
                </div>
            </div>
          </c:forEach>
        </div>
    </section>
</body>
</html>
    