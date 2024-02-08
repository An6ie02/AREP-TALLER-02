package edu.escuelaing.arep.distributed;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ConcurrentHashMap;

import javax.imageio.ImageIO;

import com.google.gson.JsonObject;

/**
 * Class responsible for the creation a socket server that receives requests
 * from
 * a client and returns a response with the information of a movie.
 * 
 * @author Angie Mojica
 * @author Daniel Benavides
 */
public class HttpServer {

    private static ConcurrentHashMap<String, JsonObject> cache = new ConcurrentHashMap<>();
    private static HttpClient httpClient = new HttpClient();
    private static final String PATH_PUBLIC = "target/classes/public";
   
    /**
     * Main method that creates a socket server and initializes the connection with
     * the client.
     * @param args Arguments of the main method.
     * @throws IOException If an input or output exception occurred.
     * @throws URISyntaxException 
     */
    public static void main(String[] args) throws IOException, URISyntaxException {

        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(35000);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 35000.");
            System.exit(1);
        }
        boolean running = true;
        while (running) {
            Socket clientSocket = null;
            try {
                System.out.println("Listo para recibir ...");
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }

            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String inputLine, outputLine;

            boolean firstLine = true;
            String uriStr = "";

            while ((inputLine = in.readLine()) != null) {
                if (firstLine) {
                    uriStr = inputLine.split(" ")[1];
                    firstLine = false;
                }
                System.out.println("Received: " + inputLine);
                if (!in.ready()) {
                    break;
                }
            }

            URI requesUri = new URI(uriStr);
            byte[] content = null;

            try {
                if(requesUri.getQuery() != null) {
                    JsonObject jsonResponse = getDataMovie(uriStr);
                    content = jsonResponse.toString().getBytes();
                    outputLine = contentHeader("text/plain", content);

                }else {
                    String path = PATH_PUBLIC + requesUri.getPath();
                    String format = getFileFormat(path);
                    content = responseBody(format, path);
                    outputLine = contentHeader(format, content);
                }
            } catch (IOException e) {
                content = responseBody("text/html", PATH_PUBLIC + "/error.html");
                outputLine = contentHeader("text/html", content);
            }

            try (OutputStream os = clientSocket.getOutputStream()) {
                os.write(outputLine.getBytes());
                os.write(content);                
            } catch (IOException e) {
                System.out.println("Error: " + e.getMessage());
            }

            out.close();
            in.close();
            clientSocket.close();
        }
        serverSocket.close();

    }

    /**
     * Method that returns the content of a file in bytes.
     * @param format ContentType of the File.
     * @param path Path of the file.
     * @return Byte array with the content of the file.
     * @throws IOException
     */
    private static byte[] responseBody(String format, String path) throws IOException {
        if(format.startsWith("image")) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            File file = new File(path);
            ImageIO.write(ImageIO.read(file), "png", baos);
            return baos.toByteArray();
        }
        return Files.readAllBytes(Paths.get(path));
    }

    /**
     * Method that returns the information of a movie from the cache or from the
     * API.
     * 
     * @param uriStr URI of the request.
     * @return JsonObject with the information of the movie.
     */
    public static JsonObject getDataMovie(String uriStr) {
        String nameMovie = uriStr.split("=")[1];
        nameMovie = nameMovie.replace("%20", " ");
        JsonObject jsonResponse = null;
        if (cache.containsKey(nameMovie)) {
            jsonResponse = cache.get(nameMovie);
        } else {
            try {
                jsonResponse = httpClient.getMovieData(uriStr);
                cache.put(nameMovie, jsonResponse);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return jsonResponse;
    }

    /**
     * Method that returns the header of the response.
     * 
     * @param type    ContentType of the response.
     * @param content Content of the response.
     * @return String with the header of the response.
     */
    public static String contentHeader(String type, byte[] content) {
        return "HTTP/1.1 200 OK\r\n" +
                "Content-Type: " + type + "\r\n" +
                "Content-Length: " + content.length + "\r\n" +
                "\r\n";
        
    }
    
    /**
     * Method that returns the ContentType of a file.
     * 
     * @param uriStr URI of the request.
     * @return String with the ContentType of the file.
     */
    public static String getFileFormat(String uriStr) {
        String format = "";
        if (uriStr.endsWith(".html")) {
            format = "text/html";
        } else if (uriStr.endsWith(".css")) {
            format = "text/css";
        } else if (uriStr.endsWith(".js")) {
            format = "application/javascript";
        } else if (uriStr.endsWith(".png")) {
            format = "image/png";
        } else {
            format = "text/plain";
        }
        return format;
    }
}

