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
                String clientResponse = "";

                if (client_socket != null) {
                    BufferedReader bufferReader = Utils.getReader(client_socket);
                    String request_data = bufferReader.readLine();

                    if (data.containsKey(request_data)) {
                        clientResponse = data.get(request_data).getDefinitionStr();
                    } else {
                        HttpClient httpClient = new DefaultHttpClient();
                        HttpGet httpGet = new HttpGet("https://api.dictionaryapi.dev/api/v2/entries/en/" + request_data);
                        HttpResponse httpResponse = httpClient.execute(httpGet);
                        HttpEntity httpEntity = httpResponse.getEntity();
                        if (httpEntity == null) {
                            Log.e("Eroare", "Null response from server");
                        }

                        String response = EntityUtils.toString(httpEntity);


                        JSONArray content = new JSONArray(response);
                        JSONObject contentObj = content.getJSONObject(0);
                        JSONArray meaningsArr = contentObj.getJSONArray("meanings");
                        JSONObject meaningsObject = meaningsArr.getJSONObject(0);
                        JSONArray defArray = meaningsObject.getJSONArray("definitions");
                        JSONObject definitionsObject = defArray.getJSONObject(0);
                        String definition = definitionsObject.get("definition").toString();

                        GenericResults newRes = new GenericResults();
                        newRes.setDefinitionStr(definition);
                        this.data.put(request_data, newRes);
                        clientResponse = data.get(request_data).getDefinitionStr();

                    }

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
