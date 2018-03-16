package cs682;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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
            System.out.println("Purchase ticket");
            //purchaseTicket(request);
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
            //list one event detail
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
                String jsonResponse = createJsonResponseNewEvent(id);
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

    private String createJsonResponseNewEvent(int id){
        JSONObject json = new JSONObject();
        json.put("eventid",id);
        return json.toString();
    }

    private void listAllEvents(HttpServletResponse response) {
        String jsonEventsList = createJsonRespEventsList();
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
    private String createJsonRespEventsList() {
        EventData eventData = new EventData();
        eventData.printEventList(); // erase
        List<Event> eventsList = eventData.getEventsList();
        JSONArray jsonArray = new JSONArray();
        for ( Event e: eventsList) {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("eventid", e.getId());
            jsonObj.put("eventname",e.getName());
            jsonObj.put("userid", e.getUserId());
            jsonObj.put("avail", e.getAvail());
            jsonObj.put("purchased", e.getPurchased());
            jsonArray.add(jsonObj);
        }
        System.out.println(jsonArray.toString());
        return jsonArray.toString();
    }
}
