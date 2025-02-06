<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>REST MVC - Kazne</title>
</head>
<body>
	<h1>REST MVC - Kazne</h1>
	<%@ include file="navigacija.jsp" %>
	<h2>Ispis svih kazni</h2>
	<form
		action="${pageContext.servletContext.contextPath}/mvc/kazne/ispisKazni"
		method="get">
		<button type="submit">Prikaži sve kazne</button>
	</form>
	<h2>Pretraživanje kazni po rednom broju</h2>
	<form method="post"
		action="${pageContext.servletContext.contextPath}/mvc/kazne/pretrazivanjeKazniPoRb">
		<table>
			<tr>
				<td>Redni broj:</td>
				<td><input name="rb" /> <input type="hidden"
					name="${mvc.csrf.name}" value="${mvc.csrf.token}" /></td>
			</tr>
			<tr>
				<td>&nbsp;</td>
				<td><input type="submit" value="Dohvati kazne"></td>
			</tr>
		</table>
	</form>
	<h2>Pretraživanje kazni u intervalu</h2>
	<form method="post"
		action="${pageContext.servletContext.contextPath}/mvc/kazne/pretrazivanjeKazni">
		<table>
			<tr>
				<td>Od vremena:</td>
				<td><input name="odVremena" /> <input type="hidden"
					name="${mvc.csrf.name}" value="${mvc.csrf.token}" /></td>
			</tr>
			<tr>
				<td>Do vremena:</td>
				<td><input name="doVremena" />
			</tr>
			<tr>
				<td>&nbsp;</td>
				<td><input type="submit" value=" Dohvati kazne "></td>
			</tr>
		</table>
	</form>
	<h2>Pretraživanje kazni po id-u vozila</h2>
	<form method="post"
		action="${pageContext.servletContext.contextPath}/mvc/kazne/pretrazivanjeKazniPoId">
		<table>
			<tr>
				<td>Id:</td>
				<td><input name="id" /> <input type="hidden"
					name="${mvc.csrf.name}" value="${mvc.csrf.token}" /></td>
			</tr>
			<tr>
				<td>&nbsp;</td>
				<td><input type="submit" value="Dohvati kazne"></td>
			</tr>
		</table>
	</form>
	<h2>Pretraživanje kazni po id-u vozila u intervalu</h2>
	<form method="post"
		action="${pageContext.servletContext.contextPath}/mvc/kazne/pretrazivanjeKazniPoIdOdDo">
		<table>
			<tr>
				<td>Id:</td>
				<td><input name="id" /> <input type="hidden"
					name="${mvc.csrf.name}" value="${mvc.csrf.token}" /></td>
			</tr>
			<tr>
				<td>Od vremena:</td>
				<td><input name="odVremena" />
			</tr>
			<tr>
				<td>Do vremena:</td>
				<td><input name="doVremena" />
			</tr>
			<tr>
				<td>&nbsp;</td>
				<td><input type="submit" value="Dohvati kazne"></td>
			</tr>
		</table>
	</form>
	<h2>Provjera radi li poslužitelj kazni</h2>
	<form method="post"
		action="${pageContext.servletContext.contextPath}/mvc/kazne/provjeriPosluzitelja">
		<input type="submit" value=" Provjeri poslužitelja ">
	</form>
	<p>${porukaPosluzitelj}</p>
</body>
</html>
