package com.jotto.unitime.sessionhandler;

import android.content.Context;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jotto.unitime.FragmentA;
import com.jotto.unitime.models.*;
import com.jotto.unitime.util.ServerConstants;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.joda.time.LocalDate;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
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

    int coursesLoaded = 0;

    // Empty contructor
    public SessionHandler() {
    }

    public String getCourse(HashMap<String, String> postDataParams) {

        URL url;
        StringBuilder response = new StringBuilder();
        BufferedReader br = null;
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
                br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
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
                br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
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
        }finally {
            if(br != null) try { br.close(); } catch (Exception fe) {
                fe.printStackTrace();
            }
        }
        // Should not be able to reach this state
        return "false";
    }

    public void getEventsFromCourse(String courseCode) {

        HttpClient httpClient = new DefaultHttpClient();
        String urlName = ServerConstants.SERVER_REST_URL+ServerConstants.EVENT_PATH;
        HttpPost request = new HttpPost(urlName);

        try {
            Event[] eventList;

            ObjectMapper mapper = new ObjectMapper();
            String params = "course=" + courseCode;

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
                e.save();
            }
        } catch (IOException ev) {
            ev.printStackTrace();
        }finally {
            request.abort();
        }


    }

    public void getDataForAutocomplete() {

        HttpURLConnection conn = null;
        URL url;
        StringBuilder response = new StringBuilder();
        try {
            CourseDataAC[] courseDataList;
            ObjectMapper mapper = new ObjectMapper();
            url = new URL(ServerConstants.SERVER_REST_URL+ServerConstants.COURSE_PATH);

            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);

            int responseCode=conn.getResponseCode();
            String line;

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line=br.readLine()) != null) {
                    response.append(line);
                }
                courseDataList = mapper.readValue(response.toString(), CourseDataAC[].class);
                for (CourseDataAC cda : courseDataList) {
                    cda.save();
                    coursesLoaded++;
                    FragmentA.updateProgressBar(coursesLoaded);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    public int getHeadInfo() {
        HttpURLConnection urlConnection = null;
        System.setProperty("http.keepAlive", "false");
        try {
            URL url = new URL(ServerConstants.SERVER_REST_URL + ServerConstants.COURSE_PATH);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("HEAD");
            int length = urlConnection.getContentLength();
            Settings settings = Settings.findById(Settings.class, 1L);
            if (length != settings.getContentLength()){
                // If no connection to server
                if (length == -1) {
                    return -1;
                }
                else {
                    settings.setContentLength(length);
                    settings.save();
                    getDataForAutocomplete();
                }
                return length;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return -1;
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