package com.example.colocviu2;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class ServerThread extends Thread{

    private int port;
    private ServerSocket serverSocket;
    private HashMap<String, GenericResults> data;

    public ServerThread(int port) {
        this.port = port;
        try {

            this.serverSocket = new ServerSocket(port);

        } catch (IOException e) {
            Log.e("Eroare", "E busit serverul " + port);
            e.printStackTrace();
        }

        this.data = new HashMap<>();
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Socket client_socket = serverSocket.accept();

                /* TODO, Try to run the following code on a separate thread */

                /* Two options, either create a new thread and run the following code or not */
                /* CLIENT: Bucharest */
                String clientResponse = "";

                if (client_socket != null) {
                    BufferedReader bufferReader = Utils.getReader(client_socket);
                    String request_data = bufferReader.readLine();

                    /* Request to server */

                    if (data.containsKey(request_data)) {
                        clientResponse = data.get(request_data).getRes1() + ";" + data.get(request_data).getRes2();

                    } else {
                        /* Fie e prin HTTP, fie o sa fie prin sockets */
                        // TODO: prepare socket connect

                        // TODO: prepare a post skeleton
                        HttpClient httpClient = new DefaultHttpClient();
                        HttpGet httpGet = new HttpGet("https://api.openweathermap.org/data/2.5/weather?" + "q=" + request_data + "&" + "appid=e03c3b32cfb5a6f7069f2ef29237d87e");
                        HttpResponse httpResponse = httpClient.execute(httpGet);
                        HttpEntity httpEntity = httpResponse.getEntity();
                        if (httpEntity == null) {
                            Log.e("Eroare", "Null response from server");
                        }


                        /* PArse response and add to hashmap */
                        String response = EntityUtils.toString(httpEntity);
                        /* {"coord":{"lon":26.1063,"lat":44.4323},"weather":[{"id":800,"main":"Clear","description":"clear sky","icon":"01d"}],"base":"stations","main":{"temp":293.07,"feels_like":292.08,"temp_min":292.05,"temp_max":293.63,"pressure":1023,"humidity":37},"visibility":10000,"wind":{"speed":4.02,"deg":260},"clouds":{"all":0},"dt":1652954151,"sys":{"type":2,"id":2032494,"country":"RO","sunrise":1652928268,"sunset":1652981992},"timezone":10800,"id":683506,"name":"Bucharest","cod":200} */
                        JSONObject content = new JSONObject(response);

                        JSONObject mainObject = content.getJSONObject("main");

                        Double temperatura = mainObject.getDouble("temp");
                        Integer humidity = mainObject.getInt("humidity");


                        GenericResults newRes = new GenericResults();
                        newRes.setRes1(temperatura.toString());
                        newRes.setRes2(humidity.toString());
                        this.data.put(request_data, newRes);
                        clientResponse = data.get(request_data).getRes1() + ";" + data.get(request_data).getRes2();

                    }

                    /* Write to client socket the reponse */
                    PrintWriter printWriter = Utils.getWriter(client_socket);
                    printWriter.println(clientResponse);

                    client_socket.close();


                } else {
                    Log.e("Eroare", "Null client socket");
                }

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
