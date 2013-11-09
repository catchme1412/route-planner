<!DOCTYPE html>
<!--[if IE 8]>    <html class="no-js lt-ie9" lang="en"> <![endif]-->
<!--[if gt IE 8]><!-->
<html class="no-js" lang="en">
<!--<![endif]-->
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<head>
<meta charset="utf-8" />
<!-- Set the viewport width to device width for mobile -->
<meta name="viewport" content="width=device-width" />
<title>Map My Route</title>
<link rel="stylesheet" href="css/normalize.css">
<link rel="stylesheet" href="css/foundation.css">
<script src="js/vendor/custom.modernizr.js"></script>
</head>
<body>
	<nav class="top-bar">
		<ul class="title-area">
			<!-- Title Area -->
			<li class="name">
				<h1>
					<a href="#"> Bon Voyage </a>
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
		<div class="large-9 columns">
			<h3>Smarter way to plan your travel</h3>
			<p>
				We'd love to hear from you. You can either reach out to us as a whole and one of our awesome team members will get back to
				you, or if you have a specific question reach out to one of our staff. We love getting email all day <em>all day</em>.
			</p>
			<div class="section-container auto" data-section>
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
				<h3>NOT INTRESTED? TRY OUT OTHER OPTIONS WE HAVE FOR YOU...............</h3>
				<table border="1">
					<tr>
						<td>Alternative Paths</td>
						<td>Comfort</td>
					</tr>
					<c:forEach items="${requestScope.path.comfortMap}" var="comfortcostResults">
						<tr>
							<td>${comfortcostResults.key}</td>
							<td>${comfortcostResults.value}</td>
						</tr>
					</c:forEach>
				</table>
				<table border="1">
					<tr>
						<td>Alternative Paths</td>
						<td>Duration</td>
					</tr>
					<c:forEach items="${requestScope.path.timeMap}" var="timeResults">
						<tr>
							<td>${timeResults.key}</td>
							<td>${timeResults.value}</td>
						</tr>
					</c:forEach>
				</table>
				<table border="1">
					<tr>
						<td>Alternative Paths</td>
						<td>Cost</td>
					</tr>
					<c:forEach items="${requestScope.path.costMap}" var="costResults">
						<tr>
							<td>${costResults.key}</td>
							<td>Rs.${costResults.value}</td>
						</tr>
					</c:forEach>
				</table>
			</div>
		</div>
		<!-- End Contact Details -->
		<!-- Sidebar -->
		<div class="large-3 columns">
			<h5>Map</h5>
			<!-- Clicking this placeholder fires the mapModal Reveal modal -->
			<p>
				<a href="" data-reveal-id="mapModal"><img src="http://placehold.it/400x280"></a><br /> <a href=""
					data-reveal-id="mapModal">View Map</a>
			</p>
			<p>
				123 Awesome St.<br /> Barsoom, MA 95155
			</p>
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
					<p>&copy; Copyright no one at all. Go to town.</p>
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
		<p>
			<img src="http://placehold.it/800x600" />
		</p>
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