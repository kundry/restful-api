package cs682;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


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
                createUser(request, response);
            } else {
                System.out.println("Transfer tickets");
                Pattern pattern = Pattern.compile("/([\\d]+)/tickets/transfer");
                Matcher match = pattern.matcher(pathInfo);
                if (match.find()) {
                    System.out.println(match.group(1));
                    System.out.println(match.group(0));
                    transferTickets(request, response);
                } else {
                    System.out.println("Invalid Path");
                }
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
                case HttpServletResponse.SC_OK:
                    jsonResponse = getResponseBody(conn);
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.setContentType("application/json;charset=UTF-8");
                    PrintWriter out = response.getWriter();
                    out.write(jsonResponse);
                    out.flush();
                    out.close();
                    System.out.println(jsonResponse); //erase
                    break;
                case HttpServletResponse.SC_BAD_REQUEST:
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    System.out.println("400: User not found"); //erase
                    break;
                default:
                    break;
            }
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            e.printStackTrace();
        }
    }

    private void createUser(HttpServletRequest request, HttpServletResponse response) {
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
            OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
            out.write(requestBody);
            out.flush();
            out.close();
            int responseCode = conn.getResponseCode();
            switch (responseCode) {
                case HttpServletResponse.SC_OK:
                    jsonResponse = getResponseBody(conn);
                    System.out.println(jsonResponse);
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.setContentType("application/json;charset=UTF-8");
                    PrintWriter outResponse = response.getWriter();
                    outResponse.write(jsonResponse);
                    outResponse.flush();
                    outResponse.close();
                    break;
                case HttpServletResponse.SC_BAD_REQUEST:
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    System.out.println("400: User unsuccessfully created");
                    break;
                default:
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    break;
            }
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            e.printStackTrace();
        }
    }

    private void transferTickets(HttpServletRequest request, HttpServletResponse response) {
        String requestBody;
        String path = request.getPathInfo();
        String host = Driver.USER_SERVICE_HOST + ":" + String.valueOf(Driver.USER_SERVICE_PORT);
        String url = host + path;
        try {
            requestBody = getRequestBody(request);
            System.out.println(requestBody);
            URL urlObj = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestMethod("POST");
            OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
            out.write(requestBody);
            out.flush();
            out.close();
            int responseCode = conn.getResponseCode();
            switch (responseCode) {
                case HttpServletResponse.SC_OK:
                    response.setStatus(HttpServletResponse.SC_OK);
                    break;
                case HttpServletResponse.SC_BAD_REQUEST:
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    System.out.println("400: Tickets could not be transferred");
                    break;
                default:
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    break;
            }
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            e.printStackTrace();
        }
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
