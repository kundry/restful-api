package cs682;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 * Class that starts the JettyServer
 */
public class Driver {
    public static final int PORT = 8080;
    public static final int OK_STATUS_CODE = 200;
    public static final int BAD_REQUEST_STATUS_CODE = 400;
    public static final String USER_SERVICE_HOST = "http://localhost";
    public static final int USER_SERVICE_PORT = 4357;


    public static void main(String[] args) {
        System.out.println("Hello");
        Server jettyHttpServer = new Server(PORT);
        ServletHandler jettyHandler = new ServletHandler();
        jettyHandler.addServletWithMapping(new ServletHolder(new UserServlet()), "/users/*");
        jettyHttpServer.setHandler(jettyHandler);
        try {
            jettyHttpServer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
