package cs682;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import java.io.FileReader;
import java.util.Properties;

/**
 * Class that starts the JettyServer
 */
public class Driver {

    private static int EVENT_SERVICE_PORT;
    public static String EVENT_SERVICE_HOST;
    public static int USER_SERVICE_PORT;
    public static String USER_SERVICE_HOST;

    public static void main(String[] args) {
        Properties config = loadConfig("config.properties");
        setServiceConnections(config);

        Server jettyHttpServer = new Server(EVENT_SERVICE_PORT);
        System.out.println("EVENT_SERVICE_PORT: "+ EVENT_SERVICE_PORT);
        ServletHandler jettyHandler = new ServletHandler();
        jettyHandler.addServletWithMapping(new ServletHolder(new EventServlet()), "/*");
        jettyHttpServer.setHandler(jettyHandler);
        try {
            jettyHttpServer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * It Loads the properties file with configuration information
     * Ports and Hosts of the different services
     * @param configPath name of the file
     * */
    private static Properties loadConfig(String configPath){
        Properties config = new Properties();
        try {
            config.load(new FileReader(configPath));
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        return config;
    }

    /**
     * It parses the properties file with configuration information
     * and it gets the Ports and Hosts of the different services
     * @param config property object to parse
     * */
    private static void setServiceConnections(Properties config) {
        USER_SERVICE_HOST = "http://" + config.getProperty("userhost");
        USER_SERVICE_PORT = Integer.parseInt(config.getProperty("userport"));
        EVENT_SERVICE_HOST = "http://" + config.getProperty("eventhost");
        EVENT_SERVICE_PORT = Integer.parseInt(config.getProperty("eventport"));
    }
}
