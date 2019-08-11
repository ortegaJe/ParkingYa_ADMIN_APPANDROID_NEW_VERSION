package e.exaple.parkingyaadmin;


import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;

import cz.msebera.android.httpclient.Header;

public class http {
    RequestParams params = new RequestParams();
    interfaceListener interfaceListener;
    String ipserver = "192.168.1.58:8080";


    public http(interfaceListener interfaceListener){
        this.interfaceListener = interfaceListener;
    }

    public void getParking(){
        final AsyncHttpClient client = new AsyncHttpClient();
        client.setMaxRetriesAndTimeout(0, 10000);
        client.setConnectTimeout(10000);
        client.setTimeout(10000);
        client.post("http://"+ipserver+"/backendParkingya/getSedes.php", new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {
                    try {
                        JSONArray jsonArray = new JSONArray(new String(responseBody));
                        interfaceListener.onPlacesListener(2, LoginMain.class, jsonArray);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                JSONArray jsonArray = new JSONArray();
                jsonArray.put(1);
                interfaceListener.onPlacesListener(0, LoginMain.class, jsonArray);
            }
        });
    }

    public void getCredentials(String email, String pass,String idsede){
        final AsyncHttpClient client = new AsyncHttpClient();
        params.put("email", email);
        params.put("pass", pass);
        params.put("idsede", idsede);
        client.setMaxRetriesAndTimeout(0, 10000);
        client.setConnectTimeout(10000);
        client.setTimeout(10000);
        client.post("http://"+ipserver+"/backendParkingya/LoginAdmin.php",params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {
                    try {
                        JSONArray jsonArray = new JSONArray(new String(responseBody));
                        interfaceListener.onPlacesListener(1, Dashboard.class, jsonArray);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                JSONArray jsonArray = new JSONArray();
                jsonArray.put(1);
                interfaceListener.onPlacesListener(0, LoginMain.class, jsonArray);
            }
        });
    }

    public void getReservations(String parking_id){
        final AsyncHttpClient client = new AsyncHttpClient();
        params.put("idsede", parking_id);
        Log.i("id---", parking_id);
        client.setMaxRetriesAndTimeout(0, 10000);
        client.setConnectTimeout(10000);
        client.setTimeout(10000);
        client.post("http://"+ipserver+"/backendParkingya/Reservations.php",params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {
                    try {
                        JSONArray jsonArray = new JSONArray(new String(responseBody));
                        interfaceListener.onPlacesListener(1, Dashboard.class, jsonArray);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                JSONArray jsonArray = new JSONArray();
                jsonArray.put(1);
                interfaceListener.onPlacesListener(0, LoginMain.class, jsonArray);
            }
        });
    }
}
