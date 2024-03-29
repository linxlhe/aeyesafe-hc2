package com.example.demo;

import org.json.JSONObject;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Scanner;

public class Login {

    public static String loginAndGetToken() {
        String url = "https://qinglanst.com/prod-api/login";
        String username = "aeyesafe@outlook.com";
        String password = "123456";
        String pattern = "monitor";
        String grantType = "password";

        JSONObject body = new JSONObject();
        body.put("username", username);
        body.put("password", password);
        body.put("pattern", pattern);
        body.put("grantType", grantType);

        String loginData = body.toString();
        String token = null;

        try {
            URI loginUri = new URI(url);
            URL loginUrl = loginUri.toURL();
            HttpURLConnection connection = (HttpURLConnection) loginUrl.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(loginData.getBytes());
            outputStream.flush();

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                Scanner scanner = new Scanner(connection.getInputStream());
                StringBuilder response = new StringBuilder();
                while (scanner.hasNextLine()) {
                    response.append(scanner.nextLine());
                }
                scanner.close();

                JSONObject jsonResponse = new JSONObject(response.toString());
                JSONObject jsonData = jsonResponse.getJSONObject("data");
                token = jsonData.getString("access_token");
            } else {
                System.out.println("Error: HTTP status code " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return token;
    }
}
