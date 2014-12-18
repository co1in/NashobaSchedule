package com.ctrmksw.nashobaschedule.network;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by Colin on 12/14/2014.
 */
public class DeveloperActionRequest extends AsyncTask<String, Void, String>
{
    private Context context;
    public DeveloperActionRequest(Context context)
    {
        this.context = context;
    }
    @Override
    protected String doInBackground(String... params)
    {
        for(int i = 0; i < 3; i++)
        {
            HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, 12 * 1000);

            HttpConnectionParams.setSoTimeout(httpParams, 12*6000);

            HttpClient httpClient = new DefaultHttpClient();
            ((DefaultHttpClient)httpClient).setParams(httpParams);
            HttpPost httpPost = new HttpPost("http://www.centermarksoftware.com/colin/nashobasched.php");
            try
            {
                ArrayList<NameValuePair> nameValuePairs = new ArrayList<>();
                nameValuePairs.add(new BasicNameValuePair("action", params[0]));
                nameValuePairs.add(new BasicNameValuePair("extras", params[1]));
                nameValuePairs.add(new BasicNameValuePair("passcode", params[2]));
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                HttpResponse httpResponse = httpClient.execute(httpPost);

                BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8"));

                String line = reader.readLine();
                reader.close();

                return line;
            }
            catch (IOException e)
            {
                try
                {
                    Thread.sleep(1000);
                } catch (InterruptedException e1)
                {
                }
            }
        }
        return "Timeout Error";
    }

    @Override
    protected void onPostExecute(String result)
    {
        Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
    }
}
