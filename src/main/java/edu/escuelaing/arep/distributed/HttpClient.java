package edu.escuelaing.arep.distributed;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * This class is in charge of making the request to the API and obtaining the data of the movie.
 * @author 
 * Daniel Benavides
 * Angie Mojica
 */
public class HttpClient {
    
    private static final String USER_AGENT = "Mozilla/5.0";
    private static final String BASE_URL = "http://www.omdbapi.com/?t=";
    private static final String API_KEY = "e3abde5f";

    /**
     * This method is in charge of making the request to the API and obtaining the data of the movie.
     * @param uriStr URI of the movie to search.
     * @return The data of the movie in a JSON format.
     * @throws IOException Exception id trown if an input or output exception occurred.
     */
    public JsonObject getMovieData(String uriStr) throws IOException {
        String nameMovie = uriStr.split("=")[1];
        String GET_URL = BASE_URL + nameMovie + "&apikey=" + API_KEY;
        System.out.println("GET URL: " + GET_URL);
        URL obj = new URL(GET_URL);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);

        // The following invocation perform the connection implicitly before getting the
        int responseCode = con.getResponseCode();
        System.out.println("GET Response Code :: " + responseCode);

        if (responseCode == HttpURLConnection.HTTP_OK) { 
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            JsonObject jsonResponse = JsonParser.parseString(response.toString()).getAsJsonObject();
            return jsonResponse;
        } else {
            System.out.println("GET request not worked");
        }
        System.out.println("GET DONE");
        return null;
    }
}