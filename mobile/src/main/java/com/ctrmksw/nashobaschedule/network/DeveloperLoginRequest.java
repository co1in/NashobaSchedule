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

/**
 * Created by Colin on 12/13/2014.
 */
public class DeveloperLoginRequest extends AsyncTask<String, Void, Boolean>
{
    private AfterLogin afterLogin;
    public DeveloperLoginRequest(AfterLogin afterLogin)
    {
        this.afterLogin = afterLogin;
    }

    @Override
    protected Boolean doInBackground(String... params)
    {
        for(int i = 0; i < 3; i++)
        {
            HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, 12 * 1000);

            HttpConnectionParams.setSoTimeout(httpParams, 12*6000);

            HttpClient httpClient = new DefaultHttpClient();
            ((DefaultHttpClient)httpClient).setParams(httpParams);
            HttpPost httpPost = new HttpPost("http://www.centermarksoftware.com/colin/login.php");
            try
            {
                ArrayList<NameValuePair> nameValuePairs = new ArrayList<>();
                nameValuePairs.add(new BasicNameValuePair("passcode", params[0]));
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                HttpResponse httpResponse = httpClient.execute(httpPost);

                BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8"));

                String line = reader.readLine();
                return line.equals("Yes");
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
        return false;
    }

    @Override
    protected void onPostExecute(Boolean result)
    {
        afterLogin.afterLogin(result);
    }

    public interface AfterLogin
    {
        public void afterLogin(boolean result);
    }
}
