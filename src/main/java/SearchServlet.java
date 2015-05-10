import org.apache.commons.lang3.StringEscapeUtils;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import javax.servlet.http.HttpServlet;

@SuppressWarnings("serial")
public class SearchServlet extends HttpServlet {

    // protected static Logger log = LogManager.getLogger();

    public SearchServlet() {

    }

    protected static final DatabaseHandler dbhandler = DatabaseHandler.getInstance();

    /**
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int matchingRecords = 0;
        String searchString = request.getParameter("search");

        searchString = searchString == null ? "" : searchString;
        searchString = StringEscapeUtils.escapeHtml4(searchString);

        sendTop(response);

        if (!searchString.isEmpty()) {

            String mode = request.getParameter("mode");

            if (mode.equals("Search on Zipcode")) {
                matchingRecords = sendSearchByStartup(response, dbhandler.searchByZipcode(searchString));
            } else if (mode.equals("Search on Startup Name")) {
                matchingRecords = sendSearchByStartup(response, dbhandler.searchByStartupName(searchString));
            } else if (mode.equals("Search on an Individual")) {
                String[] names = searchString.split(" ");
                if (names.length == 2) {
                    matchingRecords = sendSearchByIndividual(response, dbhandler.searchByIndividual(names[0], names[1]));
                }
            } else if (mode.equals("Search on Investor Name")) {
                matchingRecords = sendSearchByInvestor(response, dbhandler.searchByInvestor(searchString));
            }

        } else {

            sendSearchByStartup(response, null);
        }
        sendMidsection(response);
        sendSearchForm(request, response, matchingRecords);
        sendBottom(response);

    }

    /**
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);

        // log.info("SearchServlet ID " + this.hashCode() + " handling POST request.");

        String searchString = request.getParameter("search");

        searchString = searchString == null ? "" : searchString;
        searchString = StringEscapeUtils.escapeHtml4(searchString);

        response.setStatus(HttpServletResponse.SC_OK);
        response.sendRedirect(request.getServletPath());
    }

    /**
     * Retrieves information from a status error message
     *
     * @param errorName - The name of the error received
     * @return - A string containing the information from the error
     */
    protected String getStatusMessage(String errorName) {
        Status status = null;

        try {
            status = Status.valueOf(errorName);
        } catch (Exception ex) {
            // log.debug(errorName, ex);
            status = Status.ERROR;
        }

        return status.toString();
    }

    /**
     * Checks the status code and returns a status error if a problem is
     * encountered.
     *
     * @param code - The code of the status message
     * @return - A string containing the status associated with the given code
     */
    protected String getStatusMessage(int code) {
        Status status = null;

        try {
            status = Status.values()[code];
        } catch (Exception ex) {
            // log.debug(ex.getMessage(), ex);
            status = Status.ERROR;
        }

        return status.toString();
    }


    /**
     * Sends to the browser the top of our dynamically-generated web
     * page.
     *
     * @param response - The HTTP response we are writing to
     */
    protected void sendTop(HttpServletResponse response) {
        try {
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();

            out.println("	<!DOCTYPE html>	");
            out.println("	<html lang=\"en\">	");
            out.println("	<head>	");
            out.println("	    <meta charset=\"utf-8\">	");
            out.println("	    <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">	");
            out.println("	    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">	");
            out.println(
                    "	    <!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->	");
            out.println("	    <title>San Francisco Startup Atlas</title>	");
            out.println("		");
            out.println("	    <!-- Bootstrap -->	");
            out.println("	    <link href=\"WWWRoot/bootstrap/css/bootstrap.min.css\" rel=\"stylesheet\">	");
            out.println("		");
            out.println(
                    "	    <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->	");
            out.println("	    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->	");
            out.println("	    <!--[if lt IE 9]>	");
            out.println(
                    "	    <script src=\"https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js\"></script>	");
            out.println("	    <script src=\"https://oss.maxcdn.com/respond/1.4.2/respond.min.js\"></script>	");
            out.println("	    <![endif]-->	");
            out.println("	    <!--Begin Google Maps Code-->	");
            out.println("	    <style>	");
            out.println("	        body, html {	");
            out.println("	            height: 100%;	");
            out.println("	            width: 100%;	");
            out.println("	        }	");
            out.println("		");
            out.println("	        #map-canvas {	");
            out.println("	            position:absolute;	");
            out.println("	            width: 100%;	");
            out.println("	            height: 100%;	");
            out.println("	            margin-top: 65px;	");
            out.println("	            padding: 0;	");
            out.println("	        }	");
            out.println("	        .overlap {	");
            out.println("	            position: relative	");
            out.println("	        }	");
            out.println("	    </style>	");
            out.println(
                    "	    <script src=\"http://maps.googleapis.com/maps/api/js?key=AIzaSyB5qzrkKo4aZgEFddk4eJE1RFUa8DNvrPs\"></script>	");
            out.println("	    <script type=\"text/javascript\">	");
            out.println("	        var map	");
            out.println("		");
            out.println("	        function initialize() {	");
            out.println("		");
            out.println("	            var mapCanvas = document.getElementById('map-canvas');	");
            out.println("	            var googleMapOptions =	");
            out.println("	            {	");
            out.println("	                mapTypeControl: true,	");
            out.println("	                mapTypeControlOptions: {	");
            out.println("	                    style: google.maps.MapTypeControlStyle.HORIZONTAL_BAR,	");
            out.println("	                    position: google.maps.ControlPosition.LEFT_CENTER	");
            out.println("	                },	");
            out.println("	                center: new google.maps.LatLng(37.778083, -122.400195),	");
            out.println("	                zoom: 14,	");
            out.println("	                mapTypeId: google.maps.MapTypeId.ROADMAP	");
            out.println("	            };	");
            out.println("	            map = new google.maps.Map(mapCanvas, googleMapOptions);	");
            out.println("		");


        } catch (IOException ex) {
            // log.warn("Unable to prepare HTTP response.");
            return;
        }
    }

    /**
     * Sends to the browser the section of javascript where map markers are generated - this method
     * is used specifically when searching by individual
     *
     * @param response
     * @param person
     * @return
     */
    protected int sendSearchByIndividual(HttpServletResponse response, Person person) {

        int matchingRecords = 0;

        try {
            PrintWriter out = response.getWriter();

            // BEGIN - map initialization section where markers are added
            if (person != null) {
                out.println("	        var infowindow = new google.maps.InfoWindow;	");
                out.println("	        var marker;	");
                for (Person.Role role : person.getRoles()) {
                    for (Address address : role.getOrganization().getAddresses()) {

                        matchingRecords++;

                        out.println("       marker = new google.maps.Marker({	");
                        out.printf("	        position: new google.maps.LatLng(%f, %f),	", address.getLatitude(),
                                   address.getLongitude());
                        out.println("	        animation: google.maps.Animation.DROP,	");
                        out.println("	        map: map	");
                        out.println("	    });	");
                        out.println("		");
                        out.println("	    google.maps.event.addListener(marker, 'click', (function(marker) {	");
                        out.println("		    return function() {");
                        out.printf("infowindow.setContent('<h5>%s<br>" +
                                   "<small>%s</small>" +
                                   "<p><small>Industry Vertical: <b>%s</b><br>" +
                                   "Date Founded: <b>%s</b></br>" +
                                   "Funding Rounds: <b>%s</b></br>" +
                                   "Total Funding: <b>%s</b></small></p>" +
                                   "<h6>%s %s<br>" +
                                   "<small>%s</small></h6>');	",
                                   role.getOrganization().getName(),
                                   address.getStreetAddress(),
                                   role.getOrganization().getVertical(),
                                   role.getOrganization().getSince(),
                                   role.getOrganization().getFundingRounds(),
                                   role.getOrganization().getTotalFunding(),
                                   person.getFirstName(), person.getLastName(),
                                   role.getRole());
                        out.println("	            infowindow.open(map, marker);	");
                        out.println("	        }	");
                        out.println("	    })(marker));	");
                    }
                }
            }
            // END - map initialization section where markers are added


        } catch (IOException ex) {
            // log.warn("Unable to prepare HTTP response.");
            return 0;
        }
        return matchingRecords;
    }

    /**
     * Sends to the browser the section of javascript where map markers are generated - this method
     * is used specifically when searching by investor name
     *
     * @param response
     * @param investor
     * @return
     */
    protected int sendSearchByInvestor(HttpServletResponse response, Investor investor) {

        int matchingRecords = 0;

        try {
            PrintWriter out = response.getWriter();

            // BEGIN - map initialization section where markers are added
            if (investor != null) {
                out.println("	        var infowindow = new google.maps.InfoWindow;	");
                out.println("	        var marker;	");
                for (Startup startup : investor.getInvestments()) {
                    for (Address address : startup.getAddresses()) {

                        matchingRecords++;

                        out.println("       marker = new google.maps.Marker({	");
                        out.printf("	        position: new google.maps.LatLng(%f, %f),	", address.getLatitude(),
                                   address.getLongitude());
                        out.println("	        animation: google.maps.Animation.DROP,	");
                        out.println("	        map: map	");
                        out.println("	    });	");
                        out.println("		");
                        out.println("	    google.maps.event.addListener(marker, 'click', (function(marker) {	");
                        out.println("		    return function() {");
                        out.printf("infowindow.setContent('<h5>%s<br>" +
                                   "<small>%s</small>" +
                                   "<p><small>Industry Vertical: <b>%s</b><br>" +
                                   "Date Founded: <b>%s</b></br>" +
                                   "Funding Rounds: <b>%s</b></br>" +
                                   "Total Funding: <b>%s</b></small></p>" +
                                   "<h6>%s</h6>');	",
                                   startup.getName(),
                                   address.getStreetAddress(),
                                   startup.getVertical(),
                                   startup.getSince(),
                                   startup.getFundingRounds(),
                                   startup.getTotalFunding(),
                                   investor.getName());
                        out.println("	            infowindow.open(map, marker);	");
                        out.println("	        }	");
                        out.println("	    })(marker));	");
                    }
                }
            }
            // END - map initialization section where markers are added


        } catch (IOException ex) {
            // log.warn("Unable to prepare HTTP response.");
            return 0;
        }

        return matchingRecords;
    }

    /**
     * Sends to the browser the section of javascript where map markers are generated - this method
     * is used specifically when searching by startup name or location
     *
     * @param response
     * @param startups
     * @return
     */
    protected int sendSearchByStartup(HttpServletResponse response, ArrayList<Startup> startups) {

        int matchingRecords = 0;

        try {
            PrintWriter out = response.getWriter();

            // BEGIN - map initialization section where markers are added
            if (startups != null) {
                out.println("	        var infowindow = new google.maps.InfoWindow;	");
                out.println("	        var marker;	");
                for (Startup startup : startups) {
                    for (Address address : startup.getAddresses()) {

                        matchingRecords++;

                        if (matchingRecords < 300) {
                            out.println("       marker = new google.maps.Marker({	");
                            out.printf("	        position: new google.maps.LatLng(%f, %f),	",
                                       address.getLatitude(), address.getLongitude());
                            out.println("	        animation: google.maps.Animation.DROP,	");
                            out.println("	        map: map	");
                            out.println("	    });	");
                            out.println("		");
                            out.println("	    google.maps.event.addListener(marker, 'click', (function(marker) {	");
                            out.println("		    return function() {");
                            out.printf("infowindow.setContent('<h5>%s<br>" +
                                       "<small>%s</small>" +
                                       "<p><small>Industry Vertical: <b>%s</b><br>" +
                                       "Date Founded: <b>%s</b></br>" +
                                       "Funding Rounds: <b>%s</b></br>" +
                                       "Total Funding: <b>%s</b></small></p>');	",
                                       startup.getName(),
                                       address.getStreetAddress(),
                                       startup.getVertical(),
                                       startup.getSince(),
                                       startup.getFundingRounds(),
                                       startup.getTotalFunding());
                            out.println("	            infowindow.open(map, marker);	");
                            out.println("	        }	");
                            out.println("	    })(marker));	");
                        }
                    }
                }
            }
            // END - map initialization section where markers are added



        } catch (IOException ex) {
            // log.warn("Unable to prepare HTTP response.");
            return 0;
        }
        return matchingRecords;
    }

    /**
     * Sends the midesction of the dynamically-generated web page to the
     * browser.
     *
     * @param response - The HTTP response we are writing to
     */
    protected void sendMidsection(HttpServletResponse response) {
        try {
            PrintWriter out = response.getWriter();

            out.println("		");
            out.println("	        }	");
            out.println("		");
            out.println("	        function addMarker(markerTitle, markerLatitude, markerLongitude) {	");
            out.println("		");
            out.println("	            var infowindow = new google.maps.InfoWindow;	");
            out.println("	            var marker;	");
            out.println("		");
            out.println("	            marker = new google.maps.Marker({	");
            out.println("	                position: new google.maps.LatLng(markerLatitude, markerLongitude),	");
            out.println("	                animation: google.maps.Animation.DROP,	");
            out.println("	                map: map	");
            out.println("	            });	");
            out.println("		");
            out.println("	            google.maps.event.addListener(marker, 'click', function() {	");
            out.println("	                    infowindow.setContent(markerTitle);	");
            out.println("	                    infowindow.open(map, marker);	");
            out.println("	            });	");
            out.println("	        }	");
            out.println("		");
            out.println("	        google.maps.event.addDomListener(window, 'load', initialize);	");
            out.println("		");
            out.println("	    </script>	");
            out.println("	</head>	");
            out.println("	<body>	");
            out.println("		");
            out.println("	<div id=\"map-canvas\"></div>	");
            out.println("		");
            out.println("	<div class=\"row\">	");
            out.println("	    <div class=\"col-xs-1\"></div>	");
            out.println(
                    "	    <div class=\"col-xs-10\"><h1><small>The</small>&nbsp;San Francisco&nbsp;<small>Startup Atlas</small></h1></div>	");
            out.println("	<div class=\"col-xs-1\"><iframe src=\"https://ghbtns.com/github-btn.html?user=dwestgate&repo=San-Francisco-Startup-Atlas&type=star&count=true\" frameborder=\"0\" scrolling=\"0\" width=\"170px\" height=\"20px\"></iframe></div>	");
            out.println("	</div>	");
            out.println("		");

        } catch (IOException ex) {
            // log.warn("Unable to prepare HTTP response.");
            return;
        }
    }


    /**
     * Constructs the search form and button and sends it as an HTTP response to
     * the browser.
     * @param request
     * @param response
     * @param matchingRecords
     * @throws IOException
     */
    private static void sendSearchForm(HttpServletRequest request, HttpServletResponse response, int matchingRecords) throws IOException {

        PrintWriter out = response.getWriter();
        String mode = "";

        if (request.getParameter("mode") != null) {
             mode = request.getParameter("mode");
        }

        out.println("	<form method=\"get\" role=\"form\" action=\"/search\">	");
        out.println("		");
        out.println("		");
        out.println("	    <div class=\"row\">	");
        out.println("	        <div class=\"col-xs-12\"></div>	");
        out.println("	    </div>	");
        out.println("		");
        out.println("	    <div class=\"row\">	");
        out.println("	        <div class=\"col-xs-12\"></div>	");
        out.println("	    </div>	");
        out.println("		");
        out.println("	    <div class=\"row\">	");
        out.println("	        <div class=\"col-sm-8\"></div>	");
        out.println("	        <div class=\"col-xs-12 col-sm-3\">	");
        out.println("	        <select class=\"form-control\" name=\"mode\">	");
        out.printf("	            <option %s>Search on Startup Name</option>",
                   ((mode.equals("Search on Startup Name")) ? "selected=\"selected\"" : ""));
        out.printf("	            <option %s>Search on Investor Name</option>",
                   ((mode.equals("Search on Investor Name")) ? "selected=\"selected\"" : ""));
        //        out.println("	            <option>Search on Street Address</option>	");
        out.printf("	            <option %s>Search on Zipcode</option>",
                   ((mode.equals("Search on Zipcode")) ? "selected=\"selected\"" : ""));
        out.printf("	            <option %s>Search on an Individual</option>",
                   ((mode.equals("Search on an Individual")) ? "selected=\"selected\"" : ""));
        out.println("	        </select>	");
        out.println("	    </div>	");
        out.println("	    <div class=\"col-sm-1\"></div>	");
        out.println("	    </div>	");
        out.println("		");
        out.println("	    <div class=\"row\">	");
        out.println("	        <div class=\"col-xs-12\"></div>	");
        out.println("	    </div>	");
        out.println("		");
        out.println("	    <div class=\"row\">	");
        out.println("	        <div class=\"col-sm-8\"></div>	");
        out.println("	        <div class=\"col-xs-12 col-sm-3\">	");
        out.println(
                "	        <input type=\"text\" name=\"search\" class=\"form-control\" id=\"search_box\" placeholder=\"Enter search term here\" autofocus>	");
        out.println("	    </div>	");
        out.println("	    <div class=\"col-sm-1\"></div>	");
        out.println("	    </div>	");
        out.println("		");
        out.println("	    <div class=\"row\">	");
        out.println("	        <div class=\"col-xs-12\"></div>	");
        out.println("	    </div>	");
        out.println("		");
        out.println("	    <div class=\"row\">	");
        out.println("	        <div class=\"col-sm-8\"></div>	");
        out.println("	        <div class=\"col-xs-12 col-sm-1\">	");
        out.printf("	        <button type=\"submit\" class=\"btn btn-primary\">Search</button>	");
        out.println("	    </div>	");
        out.println("	    <div class=\"col-sm-3\"></div>	");
        out.println("	    </div>	");
        out.println("	</form>	");

        out.println("		");
        out.println("	    <div class=\"row\">	");
        out.println("	        <div class=\"col-xs-12\">&nbsp;</div>	");
        out.println("	    </div>	");
        out.println("		");

        if (!mode.isEmpty()) {
            out.println("	    <div class=\"row\">	");
            out.println("	        <div class=\"col-sm-8\"></div>	");
            out.println("	        <div class=\"col-xs-12 col-sm-3\">	");
            out.printf("	            <div class=\"panel panel-%s\">	",
                       (matchingRecords == 0 ? "danger" : "primary"));
            out.println("	              <div class=\"panel-heading\">	");
            out.printf("	                <h5 class=\"panel-title\">Matching Startups: %d</h5>	", matchingRecords);
            out.println("           	  </div>	");
            out.println("	              <div class=\"panel-body\">	");
            out.printf("	            	<p>Search type: <b>%s</b><br>Search string: <b>%s</b></p>",
                       mode, request.getParameter("search"));
            out.println("	              </div>	");
            out.println("	            </div>	");
            out.println("	        </div>	");
            out.println("	        <div class=\"col-sm-1\"></div>	");
            out.println("	    </div>	");
            out.println("		");
        }

    }

    /**
     * Outputs the bootom portion of the dynamically-constructed web pages.
     *
     * @param response - The HTTP response we are writing to
     */
    protected void sendBottom(HttpServletResponse response) {
        try {
            PrintWriter out = response.getWriter();

            out.println("		");
            out.println("		");
            out.println("		");
            out.println("	<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->	");
            out.println(
                    "	<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/1.11.2/jquery.min.js\"></script>	");
            out.println("	<!-- Include all compiled plugins (below), or include individual files as needed -->	");
            out.println("	<script src=\"js/bootstrap.min.js\"></script>	");
            out.println("	</body>	");
            out.println("	</html>	");

            out.flush();

            response.setStatus(HttpServletResponse.SC_OK);
            response.flushBuffer();
        } catch (IOException ex) {
            // log.warn("Unable to finish HTTP response.");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }
    }
}
