
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
</meta>
<meta name="keywords"
	content="DBI-0446224,Morphbank,NSF,BDI,ATOL,biology,digital,images,web,services,taxonomy" />
<title>Morphbank Services : result</title>
<link rel="stylesheet" title="Default"
	href="http://www.morphbank.net/style/morphbank2.css" type="text/css"
	media="screen"></link>
<link rel="shortcut icon"
	href="http://www.morphbank.net/style/webImages/mLogo16.ico" />
<link
	href="http://services.morphbank.net/mbd/request?method=xml&keywords=geranium&objecttype=Image&limit=10&firstResult=05&format=rss"
	rel="alternate" type="application/rss+xml" title="Morphbank RSS Feed"
	id="gallery" />

<script type="text/javascript"
	src="http://www.morphbank.net/js/head.js"></script>

<script language="javascript" type="text/javascript"
	src="http://www.morphbank.net/js/spry/SpryData.js"></script>
<script language="javascript" type="text/javascript"
	src="http://www.morphbank.net/js/spry/SpryEffects.js"></script>


<script type="text/javascript">
		window.name="main";
		var jsDomainName = "http://www.morphbank.net/";
		
		function getWindowHeight() {
			var windowHeight = 0;
			if (typeof(window.innerHeight) == 'number') {
				windowHeight = window.innerHeight;
			}
			else {
				if (document.documentElement && document.documentElement.clientHeight) {
					windowHeight = document.documentElement.clientHeight;
				}
				else {
					if (document.body && document.body.clientHeight) {
						windowHeight = document.body.clientHeight;
					}
				}
			}
			return windowHeight;
		}
		function setFooter() {
			if (document.getElementById) {
				var windowHeight = getWindowHeight();
				if (windowHeight > 0) {
					var contentHeight = document.getElementById('main').offsetHeight;
					var footerElement = document.getElementById('footer');
					var footerHeight  = footerElement.offsetHeight;
					if (windowHeight - (contentHeight + footerHeight) >= 0) {
						footerElement.style.position = 'relative';
						footerElement.style.top = ((windowHeight - (contentHeight + footerHeight)) -0) + 'px';
					}
					else {
						footerElement.style.position = 'static';
					}
				}
				footerElement.style.visibility = 'visible';
			}
		}
		window.onload = function() {
			setFooter();
		}
		window.onresize = function() {
			setFooter();
		}
	</script>
</head>
<body>
	<div id="main">
		<div class="mainHeader">
			<div class="mainHeaderLogo">
				<a href="http://www.morphbank.net/index.php"> <img border="0"
					src="http://www.morphbank.net/style/webImages/mbLogoHeader.png"
					alt="logo" /> </a>&nbsp;
			</div>
			<div class="mainHeaderTitle">Morphbank Test Interface : Results</div>

		</div>
		<div class="mainRibbon"></div>

		<div class="mainGenericContainer" style="width: 760px">
			<p>
				<!--  out.print(request.getAttribute("listOfFiles")); -->
			</p>
		</div>
		<div style="clear: both;">&nbsp;</div>
	</div>
	<!-- end div main -->
	<div id="footer">
		<div id="footerRibbon">&nbsp;</div>
		<div style="float: left;">
			<a href="http://www.nsf.gov"> <img
				src="http://www.morphbank.net/style/webImages/nsf-trans.png"
				width="50" height="50" alt="NSF" title="NSF" /> </a>
		</div>
		<div style="float: right;">
			<a href="http://www.fsu.edu"> <img
				src="http://www.morphbank.net/style/webImages/fsu-trans.png"
				width="50" height="50" alt="FSU" title="FSU" /> </a>
		</div>
		<div class="footerContent">
			<a href="mailto://riccardi@ci.fsu.edu?subject="Contact about Sample WebSite>Maintained by G. Riccardi: riccardi@ci.fsu.edu</a> <br />
			<a href="http://www.morphbank.net/About/Copyright">Copyright
				Policy</a> - <a href="http://www.morphbank.net/About/Team">Contact</a> -
			<a href="http://www.morphbank.net/About/Introduction/">About
				Morphbank</a> <br /> <a
				href="javascript:openPopup('http://www.morphbank.net/About/Manual/');">Online
				User Manual</a> - <a href="http://www.morphbank.net/Help/Documents">Documents</a>
			- <a href="http://www.morphbank.net/Help/feedback">Feedback</a>
		</div>
	</div>

</body>
</html>
