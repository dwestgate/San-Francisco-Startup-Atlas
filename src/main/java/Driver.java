/**
 * Contains the main method for running the web application
 */
public class Driver {

    // private static final Logger log = LogManager.getLogger(Driver.class.getName());

    /**
     * The main method here just starts the web server
     * @param args
     */
    public static void main(String[] args) {

        int port = 8080;

        WebServer server = null;
        try {
            server = new WebServer(port);
        } catch (Exception e) {
            System.out.printf("Unable to start web interface");
            // log.debug("Unable to start web interface");
            // log.catching(Level.DEBUG, e);
        }

        server.startServer();
    }

}
