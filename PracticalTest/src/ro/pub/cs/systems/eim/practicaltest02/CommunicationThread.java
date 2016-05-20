package ro.pub.cs.systems.eim.practicaltest02;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;

import android.util.Log;

public class CommunicationThread extends Thread{

	private ServerThread serverThread;
    private Socket socket;
	final public static String TAG = "[PracticalTest02]";

    final public static boolean DEBUG = true;

    final public static String WEB_SERVICE_ADDRESS = "https://www.wunderground.com/cgi-bin/findweather/getForecast";

    final public static String TEMPERATURE = "temperature";
    final public static String WIND_SPEED = "wind_speed";
    final public static String CONDITION = "condition";
    final public static String PRESSURE = "pressure";
    final public static String HUMIDITY = "humidity";
    final public static String ALL = "all";

    final public static String EMPTY_STRING = "";

    final public static String QUERY_ATTRIBUTE = "query";

    final public static String SCRIPT_TAG = "script";
    final public static String SEARCH_KEY = "wui.api_data =\n";

    final public static String CURRENT_OBSERVATION = "current_observation";
	
	public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.serverThread = serverThread;
        this.socket = socket;
    }

	@Override
    public void run() {
        if (socket != null) {
            try {
                BufferedReader bufferedReader = Utilities.getReader(socket);
                PrintWriter printWriter = Utilities.getWriter(socket);
                if (bufferedReader != null && printWriter != null) {
                    Log.i(TAG, "[COMMUNICATION THREAD] Waiting for parameters from client (city / information type)!");
                    String city = bufferedReader.readLine();
                    String informationType = bufferedReader.readLine();
                    HashMap<String, WeatherForecastInformation> data = serverThread.getData();
                    WeatherForecastInformation weatherForecastInformation = null;
                    if (city != null && !city.isEmpty() && informationType != null && !informationType.isEmpty()) {
                        if (data.containsKey(city)) {
                            Log.i(TAG, "[COMMUNICATION THREAD] Getting the information from the cache...");
                            weatherForecastInformation = data.get(city);
                        } else {
                            Log.i(TAG, "[COMMUNICATION THREAD] Getting the information from the webservice...");
                            HttpClient httpClient = new DefaultHttpClient();
                            HttpPost httpPost = new HttpPost(WEB_SERVICE_ADDRESS);
                            List<NameValuePair> params = new ArrayList<NameValuePair>();
                            params.add(new BasicNameValuePair(QUERY_ATTRIBUTE, city));
                            UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
                            httpPost.setEntity(urlEncodedFormEntity);
                            ResponseHandler<String> responseHandler = new BasicResponseHandler();
                            String pageSourceCode = httpClient.execute(httpPost, responseHandler);
                            if (pageSourceCode != null) {
                                Document document = Jsoup.parse(pageSourceCode);
                                Element element = document.child(0);
                                Elements scripts = element.getElementsByTag(SCRIPT_TAG);
                                for (Element script : scripts) {

                                    String scriptData = script.data();

                                    if (scriptData.contains(SEARCH_KEY)) {
                                        int position = scriptData.indexOf(SEARCH_KEY) + SEARCH_KEY.length();
                                        scriptData = scriptData.substring(position);

                                        JSONObject content = new JSONObject(scriptData);

                                        JSONObject currentObservation = content.getJSONObject(CURRENT_OBSERVATION);
                                        String temperature = currentObservation.getString(TEMPERATURE);
                                        String windSpeed = currentObservation.getString(WIND_SPEED);
                                        String condition = currentObservation.getString(CONDITION);
                                        String pressure = currentObservation.getString(PRESSURE);
                                        String humidity = currentObservation.getString(HUMIDITY);

                                        weatherForecastInformation = new WeatherForecastInformation(
                                                temperature,
                                                windSpeed,
                                                condition,
                                                pressure,
                                                humidity);

                                        serverThread.setData(city, weatherForecastInformation);
                                        break;
                                    }
                                }
                            } else {
                                Log.e(TAG, "[COMMUNICATION THREAD] Error getting the information from the webservice!");
                            }
                        }

                        if (weatherForecastInformation != null) {
                            String result = null;
                            if (ALL.equals(informationType)) {
                                result = weatherForecastInformation.toString();
                            } else if (TEMPERATURE.equals(informationType)) {
                                result = weatherForecastInformation.getTemperature();
                            } else if (WIND_SPEED.equals(informationType)) {
                                result = weatherForecastInformation.getWindSpeed();
                            } else if (CONDITION.equals(informationType)) {
                                result = weatherForecastInformation.getCondition();
                            } else if (HUMIDITY.equals(informationType)) {
                                result = weatherForecastInformation.getHumidity();
                            } else if (PRESSURE.equals(informationType)) {
                                result = weatherForecastInformation.getPressure();
                            } else {
                                result = "Wrong information type (all / temperature / wind_speed / condition / humidity / pressure)!";
                            }
                            printWriter.println(result);
                            printWriter.flush();
                        } else {
                            Log.e(TAG, "[COMMUNICATION THREAD] Weather Forecast information is null!");
                        }

                    } else {
                        Log.e(TAG, "[COMMUNICATION THREAD] Error receiving parameters from client (city / information type)!");
                    }
                } else {
                    Log.e(TAG, "[COMMUNICATION THREAD] BufferedReader / PrintWriter are null!");
                }
                socket.close();
            } catch (IOException ioException) {
                Log.e(TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
                if (DEBUG) {
                    ioException.printStackTrace();
                }
            } catch (JSONException jsonException) {
                Log.e(TAG, "[COMMUNICATION THREAD] An exception has occurred: " + jsonException.getMessage());
                if (DEBUG) {
                    jsonException.printStackTrace();
                }
            }
        } else {
            Log.e(TAG, "[COMMUNICATION THREAD] Socket is null!");
        }
    }
}
