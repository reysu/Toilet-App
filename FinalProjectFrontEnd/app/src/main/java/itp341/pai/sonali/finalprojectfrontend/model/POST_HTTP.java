package itp341.pai.sonali.finalprojectfrontend.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.net.URLEncoder;
import java.io.BufferedWriter;

/**
 * Created by Ethan Xiong on 11/12/2017.
 */

public class POST_HTTP {
    private HttpURLConnection httpConnection;
    private OutputStream os = null;
    private BufferedWriter writer = null;
    private BufferedReader bufferedReader = null;
    private String bufferedString = null;
    private HashMap<String, String> parameters;
    public POST_HTTP(URL url)
    {
        try {
            URLConnection connection = url.openConnection();
            httpConnection = (HttpURLConnection) connection;
            httpConnection.setRequestMethod("POST");
            httpConnection.setDoInput(true);
            httpConnection.setDoOutput(true);

            os = httpConnection.getOutputStream();
            writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            InputStream inputStream = null;
            if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = httpConnection.getInputStream();
            }
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            bufferedString = "";

        } catch (Exception e){
            System.out.println("Exception in POST_HTTP Constructor");
        }
    };
    public void addParameters(String key, String value)
    {
        parameters.put(key, value);
    }
    public String getPostParameters() throws UnsupportedEncodingException
    {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(HashMap.Entry<String, String> entry : parameters.entrySet()){
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();

    }
    public void connect() throws IOException
    {
        writer.write(getPostParameters());
        writer.flush();
        writer.close();
        os.close();
        httpConnection.connect();
    }

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