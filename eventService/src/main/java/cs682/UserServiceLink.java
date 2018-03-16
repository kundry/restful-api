package cs682;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Class that communicates with the UserService and
 * send requests to validate information about users
 * It is an UserService API for the EventService
 * */
public class UserServiceLink {
    //private  String USER_SERVICE_HOST = "http://localhost:4357";
    /**
     * Method that verifies if a given user id
     * is registered in the UserService by sending a GET Request
     * */
    public boolean isValidUserId(int userId) {
        String host = Driver.USER_SERVICE_HOST + ":" + String.valueOf(Driver.USER_SERVICE_PORT);
        String path = "/" + String.valueOf(userId);
        String url = host + path;
        boolean isValid = false;
        try {
            URL urlObj = new URL(url);
            HttpURLConnection conn  = (HttpURLConnection) urlObj.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestMethod("GET");
            int responseCode = conn.getResponseCode();
            if (responseCode == Driver.OK_STATUS_CODE) isValid = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return isValid;
    }
}
