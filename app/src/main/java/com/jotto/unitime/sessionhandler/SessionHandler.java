package com.jotto.unitime.sessionhandler;

import android.app.Fragment;
import android.content.Context;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jotto.unitime.FragmentA;
import com.jotto.unitime.MainActivity;
import com.jotto.unitime.models.*;
import com.jotto.unitime.util.ServerConstants;

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

/**
 * SessionHandler class, used for making http requests for the course's and event's.
 */
public class SessionHandler {

    int coursesLoaded = 0;

    // Empty contructor
    public SessionHandler() {
    }

    /**
     * Gets the course specified by course code and location of the course in the parameters.
     * @param postDataParams Course code and location
     * @return True or error message
     */
    public String getCourse(HashMap<String, String> postDataParams) {

        URL url;
        StringBuilder response = new StringBuilder();
        BufferedReader br = null;
        try {
            Course[] courseList;
            ObjectMapper mapper = new ObjectMapper().configure(MapperFeature.CAN_OVERRIDE_ACCESS_MODIFIERS, false);
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

    /**
     * Gets the event's for a specific course.
     * @param courseCode Course's course code
     * @param location Course's location
     */
    public void getEventsFromCourse(String courseCode, String location) {

        HttpURLConnection conn = null;
        String urlName = ServerConstants.SERVER_REST_URL+ServerConstants.EVENT_PATH;
        StringBuilder response = new StringBuilder();
        BufferedReader br = null;
        try {
            Event[] eventList;
            ObjectMapper mapper = new ObjectMapper().configure(MapperFeature.CAN_OVERRIDE_ACCESS_MODIFIERS, false);
            URL url = new URL(urlName);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            String params = "course=" + courseCode + "&location=" + location;

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(params);

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
                eventList = mapper.readValue(response.toString(), Event[].class);
                for (Event e : eventList) {
                    e.setCourse_code(courseCode.toUpperCase());
                    e.save();
                }
            }
            else if (responseCode == HttpsURLConnection.HTTP_NOT_ACCEPTABLE) {
                br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                while ((line=br.readLine()) != null) {
                    response.append(line);
                }
                JSONObject error = new JSONObject(response.toString());
                //return error.getString("message");
            }
            // Should not be able to reach this state

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(br != null) try { br.close(); } catch (Exception fe) {
                fe.printStackTrace();
            }
        }

//            request.addHeader("User-Agent", "UniTime-Android-Client");
//            request.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
//            request.addHeader("Content-Type", "application/x-www-form-urlencoded");
//            request.setEntity(new StringEntity(params));
//            HttpResponse response = httpClient.execute(request);
//            HttpEntity entity = response.getEntity();
//
//            String content = EntityUtils.toString(entity);
//            //String content = EntityUtils.toString(entity);
//            //System.out.println(content);
//
//            eventList = mapper.readValue(content, Event[].class);
//
//            for (Event e : eventList) {
//                e.setCourse_code(courseCode.toUpperCase());
//                e.save();
//            }
//        } catch (IOException ev) {
//            ev.printStackTrace();
//        }finally {
//            request.abort();
//        }


    }

    /**
     * Gets the data for the CourseDataAC model and saves them to the database.
     */
    public void getDataForAutocomplete() {

        HttpURLConnection conn = null;
        URL url;
        StringBuilder response = new StringBuilder();
        try {
            CourseDataAC[] courseDataList;
            ObjectMapper mapper = new ObjectMapper().configure(MapperFeature.CAN_OVERRIDE_ACCESS_MODIFIERS, false);
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
                CourseDataAC.deleteAll(CourseDataAC.class);
                CourseDataAC.executeQuery("DELETE FROM SQLITE_SEQUENCE WHERE NAME = 'COURSE_DATA_AC'");
                FragmentA.setProgressBarMax(courseDataList.length);
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

    /**
     * Sends head request to server to check if any new course's for CourseDataAC has been added,
     * if it has it updates CourseDataAC in the database. Returns -1 if it can't connect to the
     * server.
     * @return HTTP Content Length
     */
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
                    FragmentA.fragmentA.showProgressDialogWindow();
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

    /**
     * Helper class that Transforms the url.
     * @param params HashMap of course code and location
     * @return The Transformed HashMap
     * @throws UnsupportedEncodingException
     */
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