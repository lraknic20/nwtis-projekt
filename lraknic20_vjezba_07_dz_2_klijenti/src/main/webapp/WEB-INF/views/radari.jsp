<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>REST MVC - Radari</title>
</head>
<body>
	<h1>REST MVC - Radari</h1>
	<%@ include file="navigacija.jsp" %>
	<h2>Pregled aktivnih radara</h2>
	<form method="post"
		action="${pageContext.servletContext.contextPath}/mvc/radari/sviRadari">
		<input type="submit" value=" Dohvati radare ">
	</form>
	<h2>Reset radara</h2>
	<form method="get"
		action="${pageContext.servletContext.contextPath}/mvc/radari/resetRadara">
		<input type="submit" value=" Reset radara ">
	</form>
	<p>${porukaReset}</p>
	<h2>Pregled radara po id</h2>
	<form method="post"
		action="${pageContext.servletContext.contextPath}/mvc/radari/pretrazivanjeRadaraPoId">
		<table>
			<tr>
				<td>Id:</td>
				<td><input name="id" /> <input type="hidden"
					name="${mvc.csrf.name}" value="${mvc.csrf.token}" /></td>
			</tr>
			<tr>
				<td>&nbsp;</td>
				<td><input type="submit" value=" Dohvati radar "></td>
			</tr>
		</table>
	</form>
	<h2>Provjera radara po id</h2>
	<form method="post"
		action="${pageContext.servletContext.contextPath}/mvc/radari/provjeriRadaraPoId">
		<table>
			<tr>
				<td>Id:</td>
				<td><input name="id" /> <input type="hidden"
					name="${mvc.csrf.name}" value="${mvc.csrf.token}" /></td>
			</tr>
			<tr>
				<td>&nbsp;</td>
				<td><input type="submit" value=" Provjeri radar "></td>
			</tr>
		</table>
	</form>
	<p>${porukaRadarAktivan}</p>
	<h2>Brisanje svih radara</h2>
	<form method="post"
		action="${pageContext.servletContext.contextPath}/mvc/radari/obrisiSveRadare">
		<input type="submit" value=" Obriši radare ">
	</form>
	<p>${porukaObrisiRadare}</p>
	<h2>Brisanje radara po id</h2>
	<form method="post"
		action="${pageContext.servletContext.contextPath}/mvc/radari/obrisiRadaraPoId">
		<table>
			<tr>
				<td>Id:</td>
				<td><input name="id" /> <input type="hidden"
					name="${mvc.csrf.name}" value="${mvc.csrf.token}" /></td>
			</tr>
			<tr>
				<td>&nbsp;</td>
				<td><input type="submit" value=" Obriši radar "></td>
			</tr>
		</table>
	</form>
	<p>${porukaObrisiRadar}</p>
</body>
</html>
