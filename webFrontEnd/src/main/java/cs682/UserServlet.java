package cs682;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import java.net.HttpURLConnection;
import java.net.URL;


public class UserServlet extends HttpServlet {
    //private  String USER_SERVICE_HOST = "http://localhost:4357";

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("In /users/* doPost...");
        String pathInfo = request.getPathInfo();
        System.out.println(pathInfo);

        if (pathInfo == null) {
            System.out.println("Invalid Path Info");
        } else {
            if (pathInfo.equals("/create")) {
                createUser(request);
            } else {
                System.out.println("Ticket transfer");
                //to do: regex get the id and pass it as parameter with the request
                transferTickets(request);
            }
        }
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("In /users/id doGet...");
        String host = Driver.USER_SERVICE_HOST + ":" + String.valueOf(Driver.USER_SERVICE_PORT);
        String path = request.getPathInfo();
        String url = host + path;
        String jsonResponse;
        try {
            URL urlObj = new URL(url);
            HttpURLConnection conn  = (HttpURLConnection) urlObj.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestMethod("GET");
            int responseCode = conn.getResponseCode();
            switch (responseCode) {
                case Driver.OK_STATUS_CODE:
                    jsonResponse = getResponseBody(conn);
                    System.out.println(jsonResponse);
                    break;
                case Driver.BAD_REQUEST_STATUS_CODE:
                    System.out.println("400: User not found");
                    break;
                default:
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createUser(HttpServletRequest request) {
        String requestBody, jsonResponse;
        String path = "/create";
        String host = Driver.USER_SERVICE_HOST + ":" + String.valueOf(Driver.USER_SERVICE_PORT);
        String url = host + path;
        try {
            requestBody = getRequestBody(request);
            System.out.println(requestBody);
            URL urlObj = new URL(url);
            HttpURLConnection conn  = (HttpURLConnection) urlObj.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestMethod("POST");
            int responseCode = conn.getResponseCode();
            switch (responseCode) {
                case Driver.OK_STATUS_CODE:
                    OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
                    out.write(requestBody);
                    out.flush();
                    jsonResponse = getResponseBody(conn);
                    System.out.println(jsonResponse);
                    out.close();
                    break;
                case Driver.BAD_REQUEST_STATUS_CODE:
                    System.out.println("400: User unsuccessfully created");
                    break;
                default:
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void transferTickets(HttpServletRequest request) {

    }

    private String getRequestBody(HttpServletRequest request) throws IOException {
        BufferedReader in;
        String line, body;
        in = new BufferedReader(new InputStreamReader(request.getInputStream()));
        StringBuffer sb = new StringBuffer();
        while ((line = in.readLine()) != null) {
            sb.append(line);
            sb.append(System.lineSeparator());
        }
        body = sb.toString();
        in.close();
        return body;
    }

    private String getResponseBody(HttpURLConnection conn) throws IOException {
        BufferedReader in;
        String line, body;
        in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuffer sb = new StringBuffer();
        while ((line = in.readLine()) != null) {
            sb.append(line);
            sb.append(System.lineSeparator());
        }
        body = sb.toString();
        in.close();
        return body;
    }
}
