
 <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
  <head>
    <meta name="viewport" content="initial-scale=1.0, user-scalable=no">
    <meta charset="utf-8">
    <title>Simple Polylines</title>
    <style>
      html, body, #map-canvas {
        height: 100%;
        margin: 0px;
        padding: 0px
      }
    </style>
    <script src="https://maps.googleapis.com/maps/api/js?v=3.exp&sensor=false"></script>
    <script>
// This example creates a 2-pixel-wide red polyline showing
// the path of William Kingsford Smith's first trans-Pacific flight between
// Oakland, CA, and Brisbane, Australia.

function initialize() {
  var mapOptions = {
    zoom: 6,
    center: new google.maps.LatLng(14.0000, 78.0000, true),
    mapTypeId: google.maps.MapTypeId.ROADMAP
  };

  var map = new google.maps.Map(document.getElementById('map-canvas'),
      mapOptions);

  var flightPlanCoordinates = [
           <c:forEach var="c" items="${coordinates}">
           <c:out value="new google.maps.LatLng(${c}),"/>
           </c:forEach>
  ];
  var flightPath = new google.maps.Polyline({
    path: flightPlanCoordinates,
    geodesic: true,
    strokeColor: '#FF0000',
    strokeOpacity: 1.0,
    strokeWeight: 5
  });
  
  var flightPlanCoordinates2 = [
                               
           new google.maps.LatLng(12.970214, 77.56029),
           
           new google.maps.LatLng(11.6500, 78.1600),
           
  ];
  var flightPath2 = new google.maps.Polyline({
    path: flightPlanCoordinates2,
    geodesic: true,
    strokeColor: '#FFFF00',
    strokeOpacity: 1.0,
    strokeWeight: 5
  });

  flightPath.setMap(map);

  flightPath.setMap(map);
  flightPath2.setMap(map);
}

google.maps.event.addDomListener(window, 'load', initialize);

    </script>
  </head>
  <body>
    <div id="map-canvas" style="width: 550px; height:550px" align="center"></div>
  </body>
</html>

