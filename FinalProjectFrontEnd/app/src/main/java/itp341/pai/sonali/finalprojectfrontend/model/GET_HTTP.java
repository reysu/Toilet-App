package itp341.pai.sonali.finalprojectfrontend.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.net.URL;
import java.io.InputStream;
/**
 * Created by Chuan on 11/11/2017.
 */

public class GET_HTTP {

    private HttpURLConnection httpConnection;
    private BufferedReader bufferedReader;
    private String bufferedString;
    public GET_HTTP(URL url)
    {
        try {
            URLConnection connection = url.openConnection();
            httpConnection = (HttpURLConnection) connection;
            httpConnection.setRequestMethod("GET");
            httpConnection.connect();
            InputStream inputStream = null;
            if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = httpConnection.getInputStream();
            }
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            bufferedString = "";
        } catch (Exception e){
            System.out.println("Exception in GET_HTTP Constructor");
        }


    };
    public String getResponse() throws IOException
    {
        StringBuffer output = null;
        while ((bufferedString = bufferedReader.readLine()) != null)
        {
            output.append(bufferedString);
        }
        return output.toString();
    }
}