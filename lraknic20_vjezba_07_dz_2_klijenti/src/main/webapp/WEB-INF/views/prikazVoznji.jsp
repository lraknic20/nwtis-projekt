<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.util.List, java.util.Date, java.text.SimpleDateFormat,edu.unizg.foi.nwtis.lraknic20.vjezba_07_dz_2.podaci.Vozilo" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>REST MVC - Pregled vožnji</title>
		<!-- jQuery DataTables -->
		<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
		<link rel="stylesheet" type="text/css" href="https://cdn.datatables.net/1.11.5/css/jquery.dataTables.min.css">
		<script type="text/javascript" src="https://cdn.datatables.net/1.11.5/js/jquery.dataTables.min.js"></script>
    </head>
    <body>
        <h1>REST MVC - Pregled vožnji</h1>
        <%@ include file="navigacija.jsp" %>
            <br/>       
        <table id="tablica" class="display" style="width:100%">
        <thead><tr><th>R.br. <th>Broj</th><th>Vrijeme</th><th>Brzina</th><th>Snaga</th><th>Struja</th><th>Visina</th><th>GPS brzina</th><th>Temp. vozila</th><th>Postotak baterije</th><th>Napon baterije</th><th>Kapacitet baterije</th><th>Temp. baterije</th><th>Preostalo km</th><th>Ukupno km</th><th>GPS širina</th><th>GPS dužina</th></tr></thead>
	<%
	int i=0;
	SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");
	List<Vozilo> vozila = (List<Vozilo>) request.getAttribute("vozila");
	for(Vozilo v: vozila) {
	  i++;
	  Date vrijeme = new Date(v.getVrijeme());
	  
	  %>
       <tr><td class="desno"><%= i %></td><td><%= v.getBroj() %></td><td><%=  sdf.format(vrijeme) %></td><td><%= v.getBrzina() %></td><td><%= v.getSnaga() %></td><td><%= v.getStruja() %></td><td><%= v.getVisina() %></td><td><%= v.getGpsBrzina() %></td><td><%= v.getTempVozila() %></td><td><%= v.getPostotakBaterija() %></td><td><%= v.getNaponBaterija() %></td><td><%= v.getKapacitetBaterija() %></td><td><%= v.getTempBaterija() %></td><td><%= v.getPreostaloKm() %></td><td><%= v.getUkupnoKm() %></td><td><%= v.getGpsSirina() %></td><td><%= v.getGpsDuzina() %></td></tr>	  
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
