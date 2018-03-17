package cs682;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.*;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

public class EventServlet extends HttpServlet {

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("In doPost...");
        String pathInfo = request.getPathInfo();
        System.out.println(pathInfo);
        if (pathInfo.equals("/create")) {
            createEvent(request, response);
        } else {
            System.out.println("Purchase tickets");
            Pattern pattern = Pattern.compile("/purchase/([\\d]+)");
            Matcher match = pattern.matcher(pathInfo);
            if (match.find()) {
                System.out.println(match.group(1));
                purchaseTicket(request, response);
            } else {
                System.out.println("Invalid Path");
            }
        }
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response){
        System.out.println("In doGet...");
        String pathInfo = request.getPathInfo();
        System.out.println(pathInfo);
        if (pathInfo.equals("/list")) {
            listAllEvents(response);
        } else {
            System.out.println("List One");
            showOneEvent(request, response);
        }
    }

    private void createEvent(HttpServletRequest request, HttpServletResponse response) {
        try {
            String requestBody = getRequestBody(request);
            System.out.println(requestBody);
            JSONParser parser = new JSONParser();
            JSONObject jsonObj = (JSONObject) parser.parse(requestBody);
            int userId = ((Long)jsonObj.get("userid")).intValue();
            String eventName = (String) jsonObj.get("eventname");
            int numTickets = ((Long)jsonObj.get("numtickets")).intValue();
            UserServiceLink userService = new UserServiceLink();
            if (userService.isValidUserId(userId)) {
                EventData eventData = new EventData();
                int id = eventData.getLastEventId() + 1;
                Event event = new Event(id, eventName, userId, numTickets);
                eventData.addEvent(event);
                JSONObject json = createJsonResponseNewEvent(id);
                String jsonResponse = json.toString();
                System.out.println(jsonResponse);
                response.setStatus(HttpServletResponse.SC_OK);
                response.setContentType("application/json;charset=UTF-8");
                PrintWriter out = response.getWriter();
                out.write(jsonResponse);
                out.flush();
                out.close();
                eventData.printEventList();
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
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

    private JSONObject createJsonResponseNewEvent(int id){
        JSONObject json = new JSONObject();
        json.put("eventid",id);
        return json;
    }

    private void listAllEvents(HttpServletResponse response) {
        EventData eventData = new EventData();
        JSONArray array = eventData.createJsonEventsList();
        String jsonEventsList = array.toString();
        try {
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json;charset=UTF-8");
            PrintWriter out = response.getWriter();
            out.write(jsonEventsList);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showOneEvent(HttpServletRequest request, HttpServletResponse response) {
        try {
            String path = request.getPathInfo();
            int eventId = Integer.parseInt(path.substring(1));
            EventData eventData = new EventData();
            if (eventData.isRegistered(eventId)) {
                JSONObject json = eventData.createJsonOneEvent(eventId);
                String jsonResponse  = json.toString();
                response.setStatus(HttpServletResponse.SC_OK);
                response.setContentType("application/json;charset=UTF-8");
                PrintWriter out = response.getWriter();
                out.write(jsonResponse);
                out.flush();
                out.close();
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                }
        } catch (IOException e) {
                e.printStackTrace();
        }
    }

    private void purchaseTicket(HttpServletRequest request, HttpServletResponse response) {
        try {
            String requestBody = getRequestBody(request);
            System.out.println(requestBody);
            JSONParser parser = new JSONParser();
            JSONObject jsonObj=  (JSONObject) parser.parse(requestBody);
            int userId = ((Long)jsonObj.get("userid")).intValue();
            int eventId = ((Long)jsonObj.get("eventid")).intValue();
            int tickets = ((Long)jsonObj.get("tickets")).intValue();
            //
            UserServiceLink userService = new UserServiceLink();
            EventData eventData = new EventData();
            if (userService.isValidUserId(userId) && eventData.isRegistered(eventId)) {
                boolean ticketsUpdatedSuccessfully = eventData.updateNumTickets(eventId, tickets);
                boolean ticketsAddedSuccessfully = userService.addTicketsToUser(userId, eventId, tickets);
                if (ticketsUpdatedSuccessfully && ticketsAddedSuccessfully) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    eventData.printEventList(); //erase
                } else {
                    if (!ticketsAddedSuccessfully) {
                        eventData.undoUpdateNumTickets(eventId, tickets);
                    } else {
                        // undoAddTicketsToUser();
                    }
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                }
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
            //
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
