package com.example.colocviu2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {


    Button startService;
    private ServerThread serverThread;
    Button clientStartRequest;
    EditText serverPortEditText;
    EditText clientConnectAddressEditText;
    EditText clientConnectPortEditText;
    EditText clientRequestDataEditText;

    TextView responseTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        responseTextView = findViewById(R.id.resultEditText);
        startService = findViewById(R.id.StarServerButton);
        serverPortEditText = findViewById(R.id.ServerPortEditText);
        clientConnectAddressEditText = findViewById(R.id.ClientConnectAddressEditText);
        clientConnectPortEditText = findViewById(R.id.ClientConnectPortEditText);
        clientRequestDataEditText = findViewById(R.id.ClientRequestDataEditText);

        startService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                serverThread = new ServerThread(Integer.parseInt(String.valueOf(serverPortEditText.getText())));
                serverThread.start();

            }
        });

        clientStartRequest = findViewById(R.id.SendRequestButton);

        clientStartRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClientThread clientThread = new ClientThread(Integer.parseInt(String.valueOf(clientConnectPortEditText.getText())),
                        String.valueOf(clientConnectAddressEditText.getText()),
                        String.valueOf(clientRequestDataEditText.getText()), responseTextView);
                clientThread.start();
            }
        });
    }
}