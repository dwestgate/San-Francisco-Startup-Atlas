
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;


public class WebServer {

    // private static final Logger log = LogManager.getLogger(Driver.class.getName());
    private Server server;

    /**
     *
     * @param port
     * @throws Exception
     */
    public WebServer(int port) throws Exception {
        server = new Server(port);

        ResourceHandler webContent = new ResourceHandler();
        webContent.setDirectoriesListed(false);
        webContent.setResourceBase("WWWRoot");

        ContextHandler resourceContext = new ContextHandler("/WWWRoot");
        resourceContext.setHandler(webContent);

        ServletContextHandler servletContext = new ServletContextHandler();
        servletContext.setContextPath("/");
        servletContext.addServlet(new ServletHolder(new SearchServlet()),
                "/search");

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[] { resourceContext, servletContext});

        server.setHandler(handlers);
        server.start();
        server.join();
    }

    /**
     *
     * @return
     */
    public boolean startServer() {
        try {
            server.start();
            server.join();
            return true;
        } catch (Exception e) {
            System.out.println("Error starting server.");
            // log.debug("Error starting server");
            // log.catching(Level.DEBUG, e);
        }
        return false;
    }
}