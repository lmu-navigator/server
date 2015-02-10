<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>LMU Navigator</title>
<script type="text/javascript" src='js/jquery.js'></script>
<script type='text/javascript'>
	$(document).ready(function() {

		$('#UpdateButton').click(function() {
			var MSG = $("#Message").val();
			var dataString = 'message=' + MSG;

			$.ajax({
				type : "POST",
				url : "InsertMessage",
				data : dataString,
				cache : false,
				success : function(data) {
					$("#Message").val('');
					$("#content").prepend(data);
					$("#Message").focus();
				}
			});

			return false;
		});

	});
</script>
</head>
<body>

	<h1>Web Interface for LMU Navigator</h1>

	<a href="rest/">REST API</a>
	<br />
	<a href="upload/">CSV Upload</a>
	<br />
	<a href="data/buildings">Building Coordinates</a>
	<br />
	<a href="data/">Stored Data</a>
	
	
	
</body>
</html>