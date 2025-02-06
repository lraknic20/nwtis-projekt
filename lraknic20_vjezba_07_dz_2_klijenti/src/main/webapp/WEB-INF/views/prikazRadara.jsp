<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page
	import="java.util.List, edu.unizg.foi.nwtis.lraknic20.vjezba_07_dz_2.podaci.Radar"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>REST MVC - Pregled radara</title>
<!-- jQuery DataTables -->
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<link rel="stylesheet" type="text/css"
	href="https://cdn.datatables.net/1.11.5/css/jquery.dataTables.min.css">
<script type="text/javascript"
	src="https://cdn.datatables.net/1.11.5/js/jquery.dataTables.min.js"></script>
</head>
<body>
	<h1>REST MVC - Pregled radara</h1>
	<%@ include file="navigacija.jsp" %>
	<br />
	<table id="tablica" class="display" style="width: 100%">
		<thead>
			<tr>
				<th>Id
				<th>Adresa</th>
				<th>Mrežna vrata</th>
				<th>GPS širina</th>
				<th>GPS dužina</th>
				<th>Maks udaljenost</th>
			</tr>
		</thead>
		<%
		List<Radar> radari = (List<Radar>) request.getAttribute("radari");
		for (Radar r : radari) {
		%>
		<tr>
			<td class="desno"><%=r.getId()%></td>
			<td><%=r.getAdresaRadara()%></td>
			<td><%=r.getMreznaVrataRadara()%></td>
			<td><%=r.getMaksUdaljenost()%></td>
			<td><%=r.getGpsSirina()%></td>
			<td><%=r.getGpsDuzina()%></td>
		</tr>
		<%
		}
		%>
	</table>
	<script type="text/javascript">
		$(document).ready(function() {
			$('#tablica').DataTable();
		});
	</script>
</body>
</html>
