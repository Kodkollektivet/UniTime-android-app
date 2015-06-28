package com.jotto.unitime.sessionhandler;

import android.content.Context;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jotto.unitime.models.*;
import com.jotto.unitime.util.ServerConstants;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by otto on 2015-06-19.
 */
public class SessionHandler {

    // Empty contructor
    public SessionHandler() {
    }

    public String getCourse(HashMap<String, String> postDataParams) {

        URL url;
        StringBuilder response = new StringBuilder();
        try {
            Course[] courseList;
            ObjectMapper mapper = new ObjectMapper();
            url = new URL(ServerConstants.SERVER_REST_URL+ServerConstants.COURSE_PATH);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);


            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(postDataParams));

            writer.flush();
            writer.close();
            os.close();
            int responseCode=conn.getResponseCode();
            String line;

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line=br.readLine()) != null) {
                    response.append(line);
                }
                courseList = mapper.readValue(response.toString(), Course[].class);
                for (Course c : courseList) {
                    c.save();
                }
                return "true";
            }
            else if (responseCode == HttpsURLConnection.HTTP_NOT_ACCEPTABLE) {
                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                while ((line=br.readLine()) != null) {
                    response.append(line);
                }
                JSONObject error = new JSONObject(response.toString());
                return error.getString("message");
            }
            // Should not be able to reach this state
            else {
                return "false";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Should not be able to reach this state
        return "false";
    }

    public void getEventsFromCourse(String courseCode) {

        HttpClient httpClient = new DefaultHttpClient();

        try {
            Event[] eventList;
            String urlName = ServerConstants.SERVER_REST_URL+ServerConstants.EVENT_PATH;
            ObjectMapper mapper = new ObjectMapper();
            String params = "course=" + courseCode;

            HttpPost request = new HttpPost(urlName);

            request.addHeader("User-Agent", "UniTime-Android-Client");
            request.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
            request.addHeader("Content-Type", "application/x-www-form-urlencoded");
            request.setEntity(new StringEntity(params));
            HttpResponse response = httpClient.execute(request);
            HttpEntity entity = response.getEntity();

            String content = EntityUtils.toString(entity);
            //String content = EntityUtils.toString(entity);
            //System.out.println(content);

            Course course = Course.find(Course.class, "COURSECODE = ?", courseCode.toUpperCase()).get(0);

            eventList = mapper.readValue(content, Event[].class);

            for (Event e : eventList) {
                e.setCourse_code(courseCode.toUpperCase());
                e.setCourse_name(course.getName_sv());
                e.save();
            }
        } catch (IOException ev) {
            ev.printStackTrace();
        }


    }

    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
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
}