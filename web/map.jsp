

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
    center: new google.maps.LatLng(12, 78),
    mapTypeId: google.maps.MapTypeId.TERRAIN
  };

  var map = new google.maps.Map(document.getElementById('map-canvas'),
      mapOptions);

  var tillSalem = [
                               new google.maps.LatLng(12.9715987,77.5945627),
                               new google.maps.LatLng(12.733027, 77.83015999999999),
                               new google.maps.LatLng(12.74631,78.34414699999999),
                               new google.maps.LatLng(12.489381,78.56792399999999),
                               new google.maps.LatLng(11.664325,78.1460142),
];
  var flightPlanCoordinates = [
                               new google.maps.LatLng(12.9715987,77.5945627),
                               new google.maps.LatLng(12.733027, 77.83015999999999),
                               new google.maps.LatLng(12.74631,78.34414699999999),
                               new google.maps.LatLng(12.489381,78.56792399999999),
                               new google.maps.LatLng(11.664325,78.1460142),
                               new google.maps.LatLng(11.342235,77.7274769),
                               new google.maps.LatLng(11.1085242,77.3410656),
                               new google.maps.LatLng(11.0168445,76.9558321),
                               new google.maps.LatLng(10.7867303,76.6547932),
                               new google.maps.LatLng(10.5276416,76.2144349),
                               new google.maps.LatLng(10.1049398,76.35119449999999),
                               new google.maps.LatLng(9.98,76.28),
  ];
  var flightPath = new google.maps.Polyline({
    path: flightPlanCoordinates,
    geodesic: true,
    strokeColor: '#FF0000',
    strokeOpacity: 1.0,
    strokeWeight: 5
  });
  
  var flightPathTillSalem = new google.maps.Polyline({
	    path: tillSalem,
	    geodesic: true,
	    strokeColor: '#0000FF',
	    strokeOpacity: 1.0,
	    strokeWeight: 5
	  });

  flightPath.setMap(map);
  flightPathTillSalem.setMap(map);
}

google.maps.event.addDomListener(window, 'load', initialize);

    </script>
  </head>
  <body>
    <div id="map-canvas"></div>
  </body>
</html>

