package rws.sm3.app.CommonUtilites;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONStringer;

import java.util.Map;

public class NetworkHelper {
    HttpResponse response ;
    DefaultHttpClient client;
    HttpPut putRequest;
    HttpPost postRequest;
    HttpGet getRequest;
    String serviceURL;
    String responseString;
    public NetworkHelper(String serviceURL) {
        this.serviceURL = serviceURL;
    }

    public String executePutRequest(){
        return executePutRequest(null);
    }

    public String executePutRequest(JSONStringer putParameters){
        client = new DefaultHttpClient();
        putRequest = new HttpPut(serviceURL);
        putRequest.setHeader("Accept", "application/json");
        putRequest.setHeader("Content-type", "application/json");
        try {
            if (putParameters != null) {
                putRequest.setEntity(new StringEntity(putParameters.toString()));
            }
            //get the response
            response = client.execute(putRequest);
            responseString = EntityUtils.toString(response.getEntity());
        }catch (Exception e){
            //Log.e("Web Service Call", "Network Error", e);
        }
        return responseString;
    }

    public String executePostRequest(){
        return executePostRequest(null);
    }

    public String executePostRequestWithHeaders(JSONStringer postParameters,Map<String,String> headers){
        client = new DefaultHttpClient();
        postRequest = new HttpPost(serviceURL);
        postRequest.setHeader("Accept", "application/json");
        postRequest.setHeader("Content-type", "application/json");
        postRequest.setHeader("Appacitive-ApiKey",headers.get("key"));
        postRequest.setHeader("Appacitive-Environment",headers.get("env"));

        try {
            if (postParameters != null) {
                postRequest.setEntity(new StringEntity(postParameters.toString()));
            }
            //get the response
            response = client.execute(postRequest);
            responseString = EntityUtils.toString(response.getEntity());

        }catch (Exception e){
            //Log.e("Web Service Call", "Network Error", e);
        }

        return responseString;
    }

    public String executePostRequest(JSONStringer postParameters){
//        HttpParams httpParameters = new BasicHttpParams();
//        // Set the timeout in milliseconds until a connection is established.
//        // The default value is zero, that means the timeout is not used.
//        int timeoutConnection = 120000;
//        HttpConnectionParams.setConnectionTimeout(httpParameters,
//                timeoutConnection);
//        // Set the default socket timeout (SO_TIMEOUT)
//        // in milliseconds which is the timeout for waiting for data.
//        int timeoutSocket = 120000;
//        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
       /* HttpParams httpParameters = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters, 10000);
        HttpConnectionParams.setSoTimeout(httpParameters, 10000+12000);


        DefaultHttpClient  client = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(url);
        httpost.setParams(httpParameters);*/ //Working code
        client = new DefaultHttpClient();
        postRequest = new HttpPost(serviceURL);
       /* postRequest.setHeader("Accept", "application/json");
        postRequest.setHeader("Content-type", "application/json");*/
        try {
            if (postParameters != null) {
//                postRequest.setEntity(new UrlEncodedFormEntity(postParameters, "UTF-8"));
                postRequest.setEntity(new StringEntity(postParameters.toString()));
            }
            //get the response
            response = client.execute(postRequest);
            responseString = EntityUtils.toString(response.getEntity());
//            Log.d("Web Service Call", responseString);
        }catch (Exception e){
            Log.e("Web Service Call", "Network Error", e);
            return "";
        }

        return responseString;
    }

    public String executeGetRequest(){
        client = new DefaultHttpClient();
        getRequest = new HttpGet(serviceURL);
        //getRequest.setHeader("Accept", "application/json");
        //getRequest.setHeader("Content-type", "application/json");
        try {
            //get the response
            response = client.execute(getRequest);
            responseString =  EntityUtils.toString(response.getEntity());
            //responseObject = UtilityClass.convertToJSON(response.getEntity().getContent());
        }catch (Exception e){
            // Log.e("Web Service Call", "Network Error", e);
        }
        return responseString;
    }

    public String executeGetRequest(Map<String,String> headers){
        client = new DefaultHttpClient();
        getRequest = new HttpGet(serviceURL);
        getRequest.setHeader("Appacitive-ApiKey",headers.get("key"));
        getRequest.setHeader("Appacitive-Environment",headers.get("env"));
        //getRequest.setHeader("Accept", "application/json");
        //getRequest.setHeader("Content-type", "application/json");
        try {
            //get the response
            response = client.execute(getRequest);
            responseString =  EntityUtils.toString(response.getEntity());
            //responseObject = UtilityClass.convertToJSON(response.getEntity().getContent());
        }catch (Exception e){
            // Log.e("Web Service Call", "Network Error", e);
        }
        return responseString;
    }
}
