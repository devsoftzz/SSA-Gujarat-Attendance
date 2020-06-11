package com.devsoftzz.onlineattendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.SslErrorHandler;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class WebActivity extends AppCompatActivity {

    private WebView mWebView;
    private String url;
    private String js;
    private ProgressDialog pd;
    private String mime, ur, content, agent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        pd = new ProgressDialog(WebActivity.this);
        pd.setMessage("Loading...");
        pd.create();
        pd.setCancelable(false);
        pd.show();

        SharedPreferences mStorage = getSharedPreferences(MainActivity.PREFERENCE_NAME, MODE_PRIVATE);
        mWebView = findViewById(R.id.webview);

        url = "https://www.schoolattendancegujarat.org/";
        String user = mStorage.getString(MainActivity.USERNAME_KEY, "");
        String pass = mStorage.getString(MainActivity.PASSWORD_KEY, "");
        js = "javascript:" +
                "document.getElementById('Password').value = '" + pass + "';" +
                "document.getElementById('UserName').value = '" + user + "';";

        configWebView();
    }

    private void configWebView() {

        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setLoadsImagesAutomatically(true);
        mWebView.getSettings().setDisplayZoomControls(false);
        mWebView.getSettings().setSupportZoom(true);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setAllowFileAccess(true);
        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {

                String rurl = request.getUrl().toString();
                if (rurl.equals("https://www.schoolattendancegujarat.org/")) {
                    finish();
                }
                pd.show();
                return super.shouldOverrideUrlLoading(view, request);
            }

            public void onPageFinished(WebView view, final String url) {
                super.onPageFinished(view, url);
                view.evaluateJavascript(js, new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String s) {
                        Log.d("myLogs", "Webview : " + s);
                    }
                });
                try {
                    pd.dismiss();
                } catch (Exception e) {
                }
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                super.onReceivedSslError(view, handler, error);
                handler.proceed();
            }
        });

        mWebView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                ur = url;
                mime = mimetype;
                agent = userAgent;
                content = contentDisposition;
                ActivityCompat.requestPermissions(WebActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

            }
        });
        mWebView.loadUrl(url);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(ur));
                    request.setMimeType(mime);
                    String cookies = CookieManager.getInstance().getCookie(ur);
                    request.addRequestHeader("cookie", cookies);
                    request.addRequestHeader("User-Agent", agent);
                    request.setDescription("Downloading file...");
                    request.setTitle(URLUtil.guessFileName(ur, content, mime));
                    request.allowScanningByMediaScanner();
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(url, content, mime));
                    DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                    dm.enqueue(request);
                    Toast.makeText(getApplicationContext(), "Downloading File", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

}
