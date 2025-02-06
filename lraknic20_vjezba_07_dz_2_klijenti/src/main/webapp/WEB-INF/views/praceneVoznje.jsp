<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>REST MVC - Praćene vožnje</title>
</head>
<body>
	<h1>REST MVC - Praćene vožnje</h1>
	<%@ include file="navigacija.jsp" %>
	<h2>Pretraživanje praćenih vožnji u intervalu</h2>
	<form method="post"
		action="${pageContext.servletContext.contextPath}/mvc/praceneVoznje/pretrazivanjeVoznji">
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
				<td><input type="submit" value=" Dohvati praćene vožnje "></td>
			</tr>
		</table>
	</form>
	<h2>Pretraživanje praćenih vožnji po id-u vozila</h2>
	<form method="post"
		action="${pageContext.servletContext.contextPath}/mvc/praceneVoznje/pretrazivanjePracenihVoznjiPoId">
		<table>
			<tr>
				<td>Id:</td>
				<td><input name="id" /> <input type="hidden"
					name="${mvc.csrf.name}" value="${mvc.csrf.token}" /></td>
			</tr>
			<tr>
				<td>&nbsp;</td>
				<td><input type="submit" value=" Dohvati praćene vožnje "></td>
			</tr>
		</table>
	</form>
	<h2>Pretraživanje praćenih vožnji po id-u vozila u intervalu</h2>
	<form method="post"
		action="${pageContext.servletContext.contextPath}/mvc/praceneVoznje/pretrazivanjePracenihVoznjiPoIdOdDo">
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
				<td><input type="submit" value=" Dohvati praćene vožnje "></td>
			</tr>
		</table>
	</form>
	<h2>Pokretanje praćenja za vozilo s id</h2>
	<form method="post"
		action="${pageContext.servletContext.contextPath}/mvc/praceneVoznje/voziloStart">
		<table>
			<tr>
				<td>Id:</td>
				<td><input name="id" /> <input type="hidden"
					name="${mvc.csrf.name}" value="${mvc.csrf.token}" /></td>
			</tr>
			<tr>
				<td>&nbsp;</td>
				<td><input type="submit" value=" Pokreni praćenje "></td>
			</tr>
		</table>
	</form>
	<p>${porukaStart}</p>
	<h2>Prekidanje praćenja za vozilo s id</h2>
	<form method="post"
		action="${pageContext.servletContext.contextPath}/mvc/praceneVoznje/voziloStop">
		<table>
			<tr>
				<td>Id:</td>
				<td><input name="id" /> <input type="hidden"
					name="${mvc.csrf.name}" value="${mvc.csrf.token}" /></td>
			</tr>
			<tr>
				<td>&nbsp;</td>
				<td><input type="submit" value=" Zaustavi praćenje "></td>
			</tr>
		</table>
	</form>
	<p>${porukaStop}</p>
	<h2>Dodavanje novog praćenja vožnje za e-vozilo</h2>
	<form method="post" action="${pageContext.servletContext.contextPath}/mvc/praceneVoznje/voziloSpremi">
	    <table>
	        <tr>
	            <td>Id:</td>
				<td><input name="id" /> <input type="hidden"
					name="${mvc.csrf.name}" value="${mvc.csrf.token}" /></td>
	        </tr>
	        <tr>
	            <td>Broj:</td>
	            <td><input name="broj" /></td>
	        </tr>
	        <tr>
	            <td>Vrijeme:</td>
	            <td><input name="vrijeme" /></td>
	        </tr>
	        <tr>
	            <td>Brzina:</td>
	            <td><input name="brzina" /></td>
	        </tr>
	        <tr>
	            <td>Snaga:</td>
	            <td><input name="snaga" /></td>
	        </tr>
	        <tr>
	            <td>Struja:</td>
	            <td><input name="struja" /></td>
	        </tr>
	        <tr>
	            <td>Visina:</td>
	            <td><input name="visina" /></td>
	        </tr>
	        <tr>
	            <td>GPS Brzina:</td>
	            <td><input name="gpsBrzina" /></td>
	        </tr>
	        <tr>
	            <td>Temperatura Vozila:</td>
	            <td><input name="tempVozila" /></td>
	        </tr>
	        <tr>
	            <td>Postotak Baterije:</td>
	            <td><input name="postotakBaterija" /></td>
	        </tr>
	        <tr>
	            <td>Napon Baterije:</td>
	            <td><input name="naponBaterija" /></td>
	        </tr>
	        <tr>
	            <td>Kapacitet Baterije:</td>
	            <td><input name="kapacitetBaterija" /></td>
	        </tr>
	        <tr>
	            <td>Temperatura Baterije:</td>
	            <td><input name="tempBaterija" /></td>
	        </tr>
	        <tr>
	            <td>Preostalo Km:</td>
	            <td><input name="preostaloKm" /></td>
	        </tr>
	        <tr>
	            <td>Ukupno Km:</td>
	            <td><input name="ukupnoKm" /></td>
	        </tr>
	        <tr>
	            <td>GPS Širina:</td>
	            <td><input name="gpsSirina" /></td>
	        </tr>
	        <tr>
	            <td>GPS Dužina:</td>
	            <td><input name="gpsDuzina" /></td>
	        </tr>
	        <tr>
	            <td>&nbsp;</td>
	            <td><input type="submit" value=" Spremi praćenu vožnju "></td>
	        </tr>
	    </table>
	</form>
	<p>${porukaSpremi}</p>
</body>
</html>
