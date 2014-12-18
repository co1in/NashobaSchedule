package com.ctrmksw.nashobaschedule.network;

import android.os.AsyncTask;

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
import java.util.concurrent.TimeoutException;

/**
 * Created by Colin on 12/10/2014.
 */
public class ServerRequest extends AsyncTask<Void, Void, RemoteFile>
{
    private ServerResult resultListener;
    public ServerRequest(ServerResult resultListener)
    {
        this.resultListener = resultListener;
    }

    @Override
    protected RemoteFile doInBackground(Void... params)
    {
        RemoteFile file = new RemoteFile();

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
                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(6);
                nameValuePairs.add(new BasicNameValuePair("action", "getDay"));
                nameValuePairs.add(new BasicNameValuePair("extras", ""));
                nameValuePairs.add(new BasicNameValuePair("passcode", ""));
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                HttpResponse httpResponse = httpClient.execute(httpPost);

                BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8"));

                String line;
                while((line = reader.readLine()) != null)
                {
                    file.add(line);
                }

                file.error = null;
                return file;
            }
            catch (Throwable e)
            {
                file.error = e;
            }
        }
        return file;
    }

    @Override
    protected void onPostExecute(RemoteFile result)
    {
        resultListener.onResult(result);
    }

    public interface ServerResult
    {
        public void onResult(RemoteFile result);
    }
}