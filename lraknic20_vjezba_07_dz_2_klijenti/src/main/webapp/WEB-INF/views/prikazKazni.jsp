<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.util.List, java.util.Date, java.text.SimpleDateFormat,edu.unizg.foi.nwtis.lraknic20.vjezba_07_dz_2.podaci.Kazna" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>REST MVC - Pregled kazni</title>
        <!-- jQuery DataTables -->
		<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
		<link rel="stylesheet" type="text/css" href="https://cdn.datatables.net/1.11.5/css/jquery.dataTables.min.css">
		<script type="text/javascript" src="https://cdn.datatables.net/1.11.5/js/jquery.dataTables.min.js"></script>
    </head>
    <body>
        <h1>REST MVC - Pregled kazni</h1>
       	<%@ include file="navigacija.jsp" %>
            <br/>       
        <table id="tablica" class="display" style="width:100%">
        <thead><tr><th>R.br. <th>Vozilo</th><th>Vrijeme</th><th>Brzina</th><th>GPS širina</th><th>GPS dužina</th></tr></thead>
	<%
	int i=0;
	SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");
	List<Kazna> kazne = (List<Kazna>) request.getAttribute("kazne");
	for(Kazna k: kazne) {
	  i++;
	  Date vrijeme = new Date(k.getVrijemeKraj());
	  
	  %>
       <tr><td><%= i %></td><td><%= k.getId() %></td><td><%=  sdf.format(vrijeme) %></td><td><%= k.getBrzina() %></td><td><%= k.getGpsSirina() %></td><td><%= k.getGpsDuzinaRadar() %></td></tr>	  
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
