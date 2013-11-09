<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<!--[if IE 8]>    <html class="no-js lt-ie9" lang="en"> <![endif]-->
<!--[if gt IE 8]><!--> <html class="no-js" lang="en"> <!--<![endif]-->
<head>
  <meta charset="utf-8" />

  <!-- Set the viewport width to device width for mobile -->
  <meta name="viewport" content="width=device-width" />

  <title>Bon Voyage</title>
<link rel="stylesheet" href="http://code.jquery.com/ui/1.10.3/themes/smoothness/jquery-ui.css" />
<script src="http://code.jquery.com/jquery-1.9.1.js"></script>
<script src="http://code.jquery.com/ui/1.10.3/jquery-ui.js"></script>
<link rel="stylesheet" href="/resources/demos/style.css" />
<link rel="stylesheet" href="css/normalize.css">
<link rel="stylesheet" href="css/foundation.css">
<script src="js/vendor/custom.modernizr.js"></script>
<script>
/* $(document).ready(function(){
	  $("#results").click(function(){
	   alert("The paragraph was clicked.");
	  });
	}); */
$(function() {
var icons = {
header: "ui-icon-circle-arrow-e",
activeHeader: "ui-icon-circle-arrow-s"
};
$( "#accordion" ).accordion({
icons: icons
});
$( "#toggle" ).button().click(function() {
if ( $( "#accordion" ).accordion( "option", "icons" ) ) {
$( "#accordion" ).accordion( "option", "icons", null );
} else {
$( "#accordion" ).accordion( "option", "icons", icons );
}
});
});
</script>
</head>
<body>

 <nav class="top-bar">
    <ul class="title-area">
      <!-- Title Area -->
      <li class="name">
        <h1>
          <a href="#">
            Bon Voyage
          </a>
        </h1>
      </li>
      <li class="toggle-topbar menu-icon"><a href="#"><span>menu</span></a></li>
    </ul>
 
    <section class="top-bar-section">
      <!-- Right Nav Section -->
      <!-- <ul class="right">
        <li class="divider"></li>
        <li class="has-dropdown">
          <a href="#">Main Item 1</a>
          <ul class="dropdown">
            <li><label>Section Name</label></li>
            <li class="has-dropdown">
              <a href="#" class="">Has Dropdown, Level 1</a>
              <ul class="dropdown">
                <li><a href="#">Dropdown Options</a></li>
                <li><a href="#">Dropdown Options</a></li>
                <li><a href="#">Level 2</a></li>
                <li><a href="#">Subdropdown Option</a></li>
                <li><a href="#">Subdropdown Option</a></li>
                <li><a href="#">Subdropdown Option</a></li>
              </ul>
            </li>
            <li><a href="#">Dropdown Option</a></li>
            <li><a href="#">Dropdown Option</a></li>
            <li class="divider"></li>
            <li><label>Section Name</label></li>
            <li><a href="#">Dropdown Option</a></li>
            <li><a href="#">Dropdown Option</a></li>
            <li><a href="#">Dropdown Option</a></li>
            <li class="divider"></li>
            <li><a href="#">See all &rarr;</a></li>
          </ul>
        </li>
        <li class="divider"></li>
        <li><a href="#">Main Item 2</a></li>
        <li class="divider"></li>
        <li class="has-dropdown">
          <a href="#">Main Item 3</a>
          <ul class="dropdown">
            <li><a href="#">Dropdown Option</a></li>
            <li><a href="#">Dropdown Option</a></li>
            <li><a href="#">Dropdown Option</a></li>
            <li class="divider"></li>
            <li><a href="#">See all &rarr;</a></li>
          </ul>
        </li>
      </ul> -->
    </section>
  </nav>
 
  <!-- End Top Bar -->
 
 
  <!-- Main Page Content and Sidebar -->
 
  <div class="row">
 
    <!-- Contact Details -->
    <div class="large-7 columns">
 
      <div class="section-container auto" data-section>
        <section class="section">
          <div id="accordion">
<h3>Direct train</h3>
<div>
<p>
<table border="1">
	<tr>
		<td>Direct Train</td>
		<td>Cost</td>
		<td>Duration</td>
		<td>Comfort</td>
	</tr>
	<tr>
		<td>${requestScope.path.shortestPath}</td>
		<td>Rs.${requestScope.path.shortestPathCost}</td>
		<td>${requestScope.path.shortestPathDuration}</td>
		<td>${requestScope.path.shortestPathComfort}</td>
	</tr>
</table>
</p>
</div>
<h3>Alternate best comfort Paths</h3>
<div style="">

<table border="1">
	<tr>
		<th>Alternative Paths</th>
		<th>Comfort Score</th>
	</tr>
	<c:forEach items="${requestScope.path.comfortMap}" var="comfortcostResults" >
		<tr>
			<td>${comfortcostResults.key}</td>
			<td>${comfortcostResults.value}</td>
		</tr>
	</c:forEach>
</table>
</div>
<h3>Cheapest</h3>
<div>
<p>To be implemented</p>
</div>
</div>
        </section>
          </div>
    </div>
 
    <!-- End Contact Details -->
 
 
    <!-- Sidebar -->
 

    <div class="large-5 columns">
    	<h5>Alternate best comfort path</h5>
        <iframe src="http://localhost:8080/routePlanner/map.jsp" height="450px" width="450px"></iframe>
    </div>
    <!-- End Sidebar -->
  </div>
 
  <!-- End Main Content and Sidebar -->
 
 
  <!-- Footer -->
 
  <footer class="row">
    <div class="large-12 columns">
      <hr />
      <div class="row">
        <div class="large-6 columns">
          <p>&copy; Copyrighted.</p>
        </div>
        <div class="large-6 columns">
          <ul class="inline-list right">
            <li><a href="#">Link 1</a></li>
            <li><a href="#">Link 2</a></li>
            <li><a href="#">Link 3</a></li>
            <li><a href="#">Link 4</a></li>
          </ul>
        </div>
      </div>
    </div>
  </footer>
 
  <!-- End Footer -->
 
 
 
  <!-- Map Modal -->
 
  <div class="reveal-modal" id="mapModal">
    <h4>Where We Are</h4>
    <p><img src="http://placehold.it/800x600" /></p>
 
    <!-- Any anchor with this class will close the modal. This also inherits certain styles, which can be overriden. -->
    <a href="#" class="close-reveal-modal">&times;</a>
  </div>

  <script>
  document.write('<script src=js/vendor/' +
  ('__proto__' in {} ? 'zepto' : 'jquery') +
  '.js><\/script>')
  </script>
  <script src="js/foundation.min.js"></script>
  <script>
    $(document).foundation();
  </script>

</body>
</html>
