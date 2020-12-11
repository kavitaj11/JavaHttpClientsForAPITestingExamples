import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
/*
A simple java class which can be used to make GET and POST requests extremely simple and reliable to be used in Android Applications.

GET Request String getURL = "https://www.somewebsite.com/api/param1=value1&param2=value2"; 
String getResponse = new HTTPURLConnection().GET(getURL);

POST Request String postURL = "https://www.somewebsite.com/api"; 
String postResponse = new HTTPURLConnection().POST(postURL);

POST Request with parameters String postURL = "https://www.somewebsite.com/api"; 
HashMap<String, String> parameters = new HashMap<String, String>(); 
parameters.put("param1", "value1"); parameters.put("param2", "value2"); 
String postResponse = new HTTPURLConnection().POST(postURL, parameters);
*/

public class HTTPURLConnectionUtil {
    private String response = "";
    private URL url;
    public String POST(String url){
        try{
            this.url = new URL(url);
            this.response = "";
            HttpURLConnection connection = (HttpURLConnection)this.url.openConnection();
            connection.setReadTimeout(90000);
            connection.setConnectTimeout(90000);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Authorization", "Bearer " + "YOUR-TOKEN-HERE");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            OutputStream outputStream = connection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter((Writer)new OutputStreamWriter(outputStream,"UTF-8"));
            bufferedWriter.write("");
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();
            if(connection.getResponseCode() == HttpURLConnection.HTTP_OK){
                InputStream is = new BufferedInputStream(connection.getInputStream());
                return readStream(is);
            }
            this.response="";
            return this.response;
        }catch (Exception e){ }
        return this.response;
    }
    public String POST(String url, HashMap<String, String> hashMap){
        try{
            this.url = new URL(url);
            this.response = "";
            HttpURLConnection connection = (HttpURLConnection)this.url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Authorization", "Bearer " + "YOUR-TOKEN-HERE");
            connection.setFixedLengthStreamingMode(getPOSTDataString(hashMap).getBytes().length);
            connection.setReadTimeout(90000);
            connection.setConnectTimeout(90000);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            OutputStream outputStream = connection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter((Writer)new OutputStreamWriter(outputStream,"UTF-8"));
            bufferedWriter.write(getPOSTDataString(hashMap));
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();
            if(connection.getResponseCode() == HttpURLConnection.HTTP_OK){
                return readStream(new BufferedInputStream(connection.getInputStream()));
            }
            this.response="";
            return this.response;
        }catch (Exception e){ }
        return this.response;
    }
    public String GET(String url){
        try{
            this.url = new URL(url);
            this.response = "";
            HttpURLConnection connection = (HttpURLConnection)this.url.openConnection();
            connection.setReadTimeout(90000);
            connection.setConnectTimeout(90000);
            connection.setRequestMethod("GET");
            if(connection.getResponseCode() == HttpURLConnection.HTTP_OK){
                return readStream(connection.getInputStream());
            }
            this.response="";
            return this.response;
        }catch (Exception e){ }
        return this.response;
    }
    private String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuffer displayMessage = new StringBuffer();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                displayMessage.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return displayMessage.toString();
    }
    private String getPOSTDataString(HashMap<String,String> hashMap) throws UnsupportedEncodingException {
        StringBuilder stringBuilder = new StringBuilder();
        boolean b = true;
        Iterator iterator = hashMap.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry entry = (Map.Entry)iterator.next();
            if(b)b=false;
            else stringBuilder.append("&");
            stringBuilder.append(URLEncoder.encode((String)((String)entry.getKey()),(String)"UTF-8"));
            stringBuilder.append("=");
            stringBuilder.append(URLEncoder.encode((String)((String)entry.getValue()),(String)"UTF-8"));
        }
        return  stringBuilder.toString();
    }
}
