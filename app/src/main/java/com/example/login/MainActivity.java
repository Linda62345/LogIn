package com.example.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    EditText username;
    EditText Password;
    Button loginButton;
    String result;
    private String email,password;
    private static Connection myConn;

    public MainActivity() throws SQLException {
        //// The format is: "jdbc:mysql://hostname:port/databaseName", "username", "password"
        try{
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            myConn = DriverManager.getConnection("jdbc:mysql://sql6.freesqldatabase.com:3306/sql6590331","sql6590331"," jQ8VUKlX2R"); //ip, table name,account,password
            Statement myStmt = myConn.createStatement();
        }
        catch (Exception e){
            e.printStackTrace();
            System.out.print("Connection fail");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        email = password = "";
        username = findViewById(R.id.account);
        Password = findViewById(R.id.password);

        TextView register = findViewById(R.id.registerView);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Register.class);
                startActivity(intent);
                finish();
            }
        }
        );
        /*loginButton = findViewById(R.id.button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                //Thread thread = new Thread(mutiThread);
                //thread.start();
            }
        });*/
    }

    private Runnable mutiThread = new Runnable(){
        public void run(){
            try{
                URL url = new URL("http://10.0.2.2/SQL_Connect/SQL_data.php");
                //URL url = new URL("http://10.0.2.2/SQL_Connect/Local_data.php");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                // ????????????????????? POST
                connection.setDoOutput(true); // ????????????
                connection.setDoInput(true); // ????????????
                connection.setUseCaches(false); // ???????????????
                connection.connect(); // ????????????
                int responseCode =
                        connection.getResponseCode();
                // ???????????????????????????
                if(responseCode ==
                        HttpURLConnection.HTTP_OK){
                    // ?????? HTTP ??????????????? OK ???????????? Error
                    InputStream inputStream =
                            connection.getInputStream();
                    // ??????????????????
                    BufferedReader bufReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"), 8);
                    // ???????????????????????????
                    String box = ""; // ?????????????????????
                    String line = null; // ????????????????????????
                    while((line = bufReader.readLine()) != null) {
                        box += line + "\n";
                        // ???????????????????????????????????????????????????
                    }
                    inputStream.close(); // ??????????????????
                    result = box; // ????????????????????????????????????
                }
            }
            catch (Exception e){
                result = e.toString();
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    username.setText(result);
                }
            });
        }
    };

    public void login(View view){
        String url = "http://10.0.2.2/SQL_Connect/login.php";
        email = username.getText().toString().trim();
        password = Password.getText().toString().trim();
        if(!email.equals("") && !password.equals("")) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("res", response);
                    if (response.equals("success")) {
                        Intent intent = new Intent(MainActivity.this, Success.class);
                        startActivity(intent);
                        finish();
                    } else if (response.equals("failure")) {
                        Toast.makeText(MainActivity.this, "Invalid Login Id/Password", Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(MainActivity.this, error.toString().trim(), Toast.LENGTH_SHORT).show();
                }
            }) {
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> data = new HashMap<>();
                    data.put("email", email);
                    data.put("password", password); //php,?????????
                    return data;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(stringRequest);
        }else{
            Toast.makeText(this,"Fields can not be empty!", Toast.LENGTH_SHORT).show();
        }

    }
    public void register(View view) {
        Intent intent = new Intent(this, Register.class);
        startActivity(intent);
        finish();
    }


}