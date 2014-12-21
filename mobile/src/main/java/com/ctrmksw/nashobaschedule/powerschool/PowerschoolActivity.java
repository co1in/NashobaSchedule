package com.ctrmksw.nashobaschedule.powerschool;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.Toast;

import com.ctrmksw.nashobaschedule.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class PowerschoolActivity extends Activity
{

    private WebView webView;

    private final String LOGIN_URL = "https://powerschool.nrsd.net/public/";
    private final String OTHER_LOGIN_URL = "https://powerschool.nrsd.net/public/home.html";
    private final String LOGGED_IN_URL = "https://powerschool.nrsd.net/guardian/home.html";
    private final String MOBILE_URL = "https://powerschool.nrsd.net/guardian/mobile/home.html#_home";
    private View progressLayout;

    private int reloadCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_powerschool);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        webView = (WebView) findViewById(R.id.webview);
        webView.setWebChromeClient(new WebChromeClient());

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);

        progressLayout = findViewById(R.id.progress_layout);

        webView.setWebViewClient(new WebViewClient()
        {

            @Override
            public void onPageFinished(WebView view, String url)
            {
                if(url.equals(LOGIN_URL) || url.equals(OTHER_LOGIN_URL))
                {
                    if(reloadCount < 3)
                    {
                        callJavaScript(view, "console.log(document.getElementsByName('account')[0].value = '" + PowerschoolPrefs.get(PowerschoolActivity.this).getUserName() + "');");
                        callJavaScript(view, "console.log(document.getElementsByName('pw')[0].value = '" + PowerschoolPrefs.get(PowerschoolActivity.this).getPassword() + "');");
                        callJavaScript(view, "console.log(document.getElementById('btn-enter').click());");
                    }
                    else
                    {
                        Toast.makeText(PowerschoolActivity.this, "Error Logging In, please check credentials", Toast.LENGTH_SHORT).show();
                        showLoginDialog();
                        setWeb(false);
                        setProgressVisibility(false);
                    }

                    reloadCount++;
                }
                else
                {
                    if(!url.contains("powerschool.nrsd.net"))
                    {
                        Toast.makeText(PowerschoolActivity.this, "Error: navigated away from powerschool", Toast.LENGTH_SHORT).show();
                        setWeb(false);
                        setProgressVisibility(false);
                    }
                    else if(url.equals(MOBILE_URL))
                    {
                        setWeb(false);
                        setProgressVisibility(true);
                        callJavaScript(webView, "console.log(document.getElementById('exitlink').click());");
                    }
                    else if(url.equals(LOGGED_IN_URL))
                    {
                        callJavaScript(view, "Android.checkLogin('<html>' + document.getElementsByTagName('html')[0].innerHTML + '</html>');");
                        setProgressVisibility(false);
                    }
                    else
                    {
                        setWeb(true);
                        setProgressVisibility(false);
                    }

                    reloadCount = 0;
                }
                Log.d("PowerSchool Login", "Website Loaded: '" + url + "'");
                //Toast.makeText(PowerschoolActivity.this, url, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon)
            {
                if(url.equals(LOGIN_URL) || url.equals(OTHER_LOGIN_URL))
                {
                    setProgressVisibility(true);
                    setWeb(false);
                }
                else if(url.equals(MOBILE_URL))
                {
                    setWeb(false);
                    setProgressVisibility(true);
                }
            }
        });

        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setDisplayZoomControls(false);

        webView.addJavascriptInterface(new WebViewInterface(), "Android");

        if(PowerschoolPrefs.get(this).getUserName().equals("") || PowerschoolPrefs.get(this).getPassword().equals(""))
        {
            showLoginDialog();
        }
        else
            loadLoginPage();
    }

    private void showLoginDialog()
    {
        View root = getLayoutInflater().inflate(R.layout.dialog_powerschool_login, null);

        final EditText userNameField = (EditText)root.findViewById(R.id.powerschool_user_name_field);
        userNameField.setText(PowerschoolPrefs.get(this).getUserName());

        final EditText password = (EditText)root.findViewById(R.id.powerschool_password_field);
        password.setText(PowerschoolPrefs.get(this).getPassword());

        new AlertDialog.Builder(this)
                .setView(root)
                .setPositiveButton("Login", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        PowerschoolPrefs.get(PowerschoolActivity.this).setUserName(userNameField.getText().toString());
                        PowerschoolPrefs.get(PowerschoolActivity.this).setPassword(password.getText().toString());
                        loadLoginPage();
                    }
                })
                .setNegativeButton("Exit", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        finish();
                    }
                })
                .setCancelable(false)
                .show();
    }

    private void setWeb(boolean visible)
    {
        webView.setVisibility((visible)?View.VISIBLE : View.GONE);
    }

    private void setProgressVisibility(boolean visible)
    {
        progressLayout.setVisibility((visible)?View.VISIBLE : View.GONE);
    }

    private void loadLoginPage()
    {
        webView.loadUrl(LOGIN_URL);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK )
        {
            return true;
        }

        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_powerschool, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_delete_credentials)
        {
            PowerschoolPrefs.get(this).setUserName("");
            PowerschoolPrefs.get(this).setPassword("");
            setWeb(false);
            setProgressVisibility(false);
            showLoginDialog();
            return true;
        }
        else if(id == android.R.id.home)
        {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void callJavaScript(WebView view, String command)
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("javascript:try{");
        stringBuilder.append(command);
        stringBuilder.append("}catch(error){Android.onError(error.message);}");
        view.loadUrl(stringBuilder.toString());
    }

    private class WebViewInterface
    {
        @JavascriptInterface
        public void checkLogin(String html)
        {
            Element doc = Jsoup.parse(html);
            Elements possibleErrorBox = doc.getElementsByClass("feedback-alert");
            if(possibleErrorBox.size() > 0)
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        webView.setVisibility(View.INVISIBLE);
                        showLoginDialog();
                        Toast.makeText(PowerschoolActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        webView.setVisibility(View.VISIBLE);
                    }
                });
            }
        }

        @JavascriptInterface
        public void onError(String error){
            throw new IllegalArgumentException(error);
        }
    }



    abstract class ObjectRunnable implements Runnable
    {
        protected Object obj;
        public ObjectRunnable(Object obj)
        {
            this.obj = obj;
        }
    }
}
