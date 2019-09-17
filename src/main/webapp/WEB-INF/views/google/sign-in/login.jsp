<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Google Sign In</title>
<meta name="google-signin-scope" content="profile email">
<meta name="google-signin-client_id"
	content="56544827833-d9qmm0ik4ukn3s8g8aplpco391bfjco0.apps.googleusercontent.com">
<script src="https://apis.google.com/js/platform.js" async defer></script>
<script src="https://apis.google.com/js/platform.js?onload=renderButton"
	async defer></script>
<script
	src="http://ajax.googleapis.com/ajax/libs/jquery/1.8.2/jquery.min.js">
	
</script>
<script src="https://apis.google.com/js/client:platform.js?onload=start"
	async defer>
	
</script>
<script src="<c:url value='/js/jquery_3_4_1.js'/>"></script>


<style>
#customBtn {
	display: block;
	background: white;
	color: #444;
	box-shadow: 1px 1px 1px grey;
	white-space: nowrap;
	vertical-align: middle;
	padding-left: 42px;
	padding-right: 42px;
	font-size: 14px;
	font-weight: bold;
	border-radius: 5px;
	border: thin solid #888;
}

#customBtn:hover {
	cursor: pointer;
}
</style>


<script>
	function onSuccess(googleUser) {
		var profile = googleUser.getBasicProfile();
		var id_token = googleUser.getAuthResponse().id_token;

		$.ajax({
			type : 'POST',
			url : "http://localhost:8080/KarpyShopping/googleVerify",
			data : "idtokenstr=" + id_token,
			dataType : "text",
			success : function(response) {
				console.log("Logging as: " + profile.getName());
				$("#my-signin2").css("display", "none");
				$("#customBtn").css("display", "inline-block");
				$(".data").css("display", "block");
				$("#name").text(profile.getName());
				$("#pic").attr('src', profile.getImageUrl());
				$("#email").text(profile.getEmail());
			},
			error : function(jqXHR, textStatus, errorThrown) {
				console.log("jqXHR: " + jqXHR);
				console.log("textStatus: " + textStatus);
				console.log("errorThrown: " + errorThrown);

			},
		});
	}

	// 		var xhr = new XMLHttpRequest();
	// 		xhr.open('POST', 'googleVerify');
	// 		xhr.setRequestHeader('Content-Type',
	// 				'application/x-www-form-urlencoded');
	// 		xhr.send('idtokenstr=' + id_token);
	// 		xhr.onload = function() {
	// 			console.log("Logging as: " + profile.getName());
	// 			$("#my-signin2").css("display", "none");
	// 			$("#customBtn").css("display", "inline-block");
	// 			$(".data").css("display", "block");
	// 			$("#name").text(profile.getName());
	// 			$("#pic").attr('src', profile.getImageUrl());
	// 			$("#email").text(profile.getEmail());
	// 		};

	// 		location.href = "http://localhost:8080/KarpyShopping/home";
	// 	}

	function onFailure(error) {
		console.log(error);
	}
	function renderButton() {
		gapi.signin2.render('my-signin2', {
			'scope' : 'profile email',
			'width' : 240,
			'height' : 50,
			'longtitle' : true,
			'theme' : 'dark',
			'onsuccess' : onSuccess,
			'onfailure' : onFailure
		});

	}

	function signOut() {
		var auth2 = gapi.auth2.getAuthInstance();
		auth2.signOut().then(function() {
			console.log('User signed out.');
			alert("You have been successfully signed out");
			$("#my-signin2").css("display", "block");
			$("#customBtn").css("display", "none");
			$(".data").css("display", "none");
		});
	}

	// 	function onSignIn(googleUser) {
	// 		var profile = googleUser.getBasicProfile();
	// 		console.log('ID: ' + profile.getId()); // Do not send to your backend! Use an ID token instead.
	// 		console.log('Name: ' + profile.getName());
	// 		console.log('Image URL: ' + profile.getImageUrl());
	// 		console.log('Email: ' + profile.getEmail()); // This is null if the 'email' scope is not present.
	// 		$(".g-signin2").css("display", "none");
	// 		$(".data").css("display", "block");
	// 		$("#pic").attr('src', profile.getImageUrl());
	// 		$("#email").text(profile.getEmail());
	// 	}
</script>

<style>
#my-signin2 {
	display: block;
}

.data {
	display: none;
}
</style>
</head>

<body>
	<div id="my-signin2"></div>
	<!-- 	<div class="g-signin2" data-onsuccess="onSignIn"></div> -->


	<div class="data">
		<p>Profile Detail</p>
		Name:<span id="name"></span>
		<p>Image:</p>
		<p>
			<img id="pic" width="100px" />
		</p>

		Email Address: <span id="email"></span>
	</div>
	<br>
	<button id="customBtn" onclick="signOut();">Sign out</button>

</body>
</html>
