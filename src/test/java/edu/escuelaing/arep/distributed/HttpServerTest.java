package edu.escuelaing.arep.distributed;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import org.junit.Test;

import com.google.gson.JsonObject;

public class HttpServerTest {

    @Test
    public void testGetDataMovie() {
        HttpClient httpClient = new HttpClient();
        try {
            JsonObject jsonResponse = httpClient.getMovieData("http://www.omdbapi.com/?t=Armero&apikey=e3abde5f");
            assertNotNull(jsonResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetCorrectDataMovie() {
        HttpClient httpClient = new HttpClient();
        try {
            JsonObject jsonResponse = httpClient.getMovieData("http://www.omdbapi.com/?t=Armero&apikey=e3abde5f");
            assertTrue(jsonResponse.get("Title").getAsString().equals("Armero"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testNotFoundMovie() {
        HttpClient httpClient = new HttpClient();
        try {
            JsonObject jsonResponse = httpClient.getMovieData("http://www.omdbapi.com/?t=Armeroooooo&apikey=e3abde5f");
            assertTrue(jsonResponse.get("Response").getAsString().equals("False"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
