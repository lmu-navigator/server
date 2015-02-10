<%-------------------------------------------------------------------%>
<%-- Copyright 2013 Code Strategies                                --%>
<%-- This code may be freely used and distributed in any project.  --%>
<%-- However, please do not remove this credit if you publish this --%>
<%-- code in paper or electronic form, such as on a web site.      --%>
<%-------------------------------------------------------------------%>

<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Upload Page</title>
</head>
<body>

<strong>01_Stadt.csv</strong> (<span style="text-decoration: underline">Stadtcode;Stadt</span>;Dateiname)

<form name="form1" id="form1" action="city" method="post" enctype="multipart/form-data">
	<select name="action">
		<option value="add">Add</option>
		<option value="dif">Modify</option>
		<option value="del">Delete</option>
	</select>
	<input type="file" size="50" name="csvFile">
	<input type="submit" value="Upload">
</form>


<br />
<strong>02_Strasse.csv</strong> (<span style="text-decoration: underline">Straßencode;Stadtcode;Straße</span>;Dateiname)

<form name="form1" id="form2" action="street" method="post" enctype="multipart/form-data">
	<select name="action">
		<option value="add">Add</option>
		<option value="dif">Modify</option>
		<option value="del">Delete</option>
	</select>
	<input type="file" size="50" name="csvFile">
	<input type="submit" value="Upload">
</form>
	

<br />
<strong>03_Bauwerk.csv</strong> (<span style="text-decoration: underline">BWCode</span>;Stadtcode;<span style="text-decoration: underline">Straßencode;Benennung</span>)

<form name="form1" id="form3" action="building" method="post" enctype="multipart/form-data">
	<select name="action">
		<option value="add">Add</option>
		<option value="dif">Modify</option>
		<option value="del">Delete</option>
	</select>
	<input type="file" size="50" name="csvFile">
	<input type="submit" value="Upload">
</form>


<br />
<strong>04_BauteilHaus.csv</strong> (<span style="text-decoration: underline">BTCode</span>;Stadtcode;Straßencode;<span style="text-decoration: underline">BWCode;BauteilHaus</span>)

<form name="form1" id="form4" action="buildingpart" method="post" enctype="multipart/form-data">
	<select name="action">
		<option value="add">Add</option>
		<option value="dif">Modify</option>
		<option value="del">Delete</option>
	</select>
	<input type="file" size="50" name="csvFile">
	<input type="submit" value="Upload">
</form>
	

<br />
<strong>05_Geschoss.csv</strong> (<span style="text-decoration: underline">GCode</span>;Stadtcode;Straßencode;BWCode;<span style="text-decoration: underline">BTCode;Geschoss;Benennung;Dateiname</span>)

<form name="form1" id="form5" action="floor" method="post" enctype="multipart/form-data">
	<select name="action">
		<option value="add">Add</option>
		<option value="dif">Modify</option>
		<option value="del">Delete</option>
	</select>
	<input type="file" size="50" name="csvFile">
	<input type="submit" value="Upload">
</form>
	

<br />
<strong>06_Raum.csv</strong>  (<span style="text-decoration: underline">GCode</span>;Stadtcode;Straßencode;BWCode;BTCode;Geschoss;Benennung;<span style="text-decoration: underline">Raumnummer;RCode</span>;Dateiname)

<form name="form1" id="form6" action="room" method="post" enctype="multipart/form-data">
	<select name="action">
		<option value="add">Add</option>
		<option value="dif">Modify</option>
		<option value="del">Delete</option>
	</select>
	<input type="file" size="50" name="csvFile">
	<input type="submit" value="Upload">
</form>
	
<br />


</body>
</html>