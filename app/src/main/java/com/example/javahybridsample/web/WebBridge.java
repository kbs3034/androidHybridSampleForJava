package com.example.javahybridsample.web;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.annotation.MainThread;

import com.example.javahybridsample.App;
import com.example.javahybridsample.BuildConfig;
import com.example.javahybridsample.web.handler.NativeSystemHandler;
import com.example.javahybridsample.web.handler.SampleHandler;
import com.google.gson.Gson;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;

public class WebBridge {
    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());
    private Gson gson = new Gson();

    private WebView webView;
    private Activity callerActivity;

    public WebBridge (WebView webView, Activity callerActivity) {
        this.webView = webView;
        this.callerActivity = callerActivity;
    }

    public static final String DEFAULT_WEB_BRIDGE_NAME = "androidWebBridge";

    public SampleHandler sampleHandler = new SampleHandler() {
        @Override
        public void test(Map<String, Object> args) {
            App.INSTANCE.debugToast("test bridge :: " + args.toString());
        }
    };
    
    public NativeSystemHandler nativeSystemHandler = new NativeSystemHandler() {
        @Override
        public void showToast(Map<String, Object> args) {
            String message = (String) args.get("message");
            Toast.makeText(callerActivity, message, Toast.LENGTH_SHORT).show();
        }

        @Override
        public String getAppVersion() {
            try {
                return callerActivity.getPackageManager().getPackageInfo(callerActivity.getPackageName(), 0).versionName;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                return "";
            }
        }
    };

    @JavascriptInterface
    public String postMessage(String message) {
        logger.info(message);
        try {
            String decoded = URLDecoder.decode(message, "utf-8");
            WebMessage webMessage = gson.fromJson(decoded, WebMessage.class);
            if(webMessage.group.isEmpty() || webMessage.group == null) {
                return null;
            }

            Object result = runFunction(webMessage);
            return (result instanceof JSONObject)? ((JSONObject)result).toString() : (String)result;
        } catch (UnsupportedEncodingException e) {
            logger.error("web bridge decode error");
            e.printStackTrace();
            return "";
        }
    }

    @MainThread
    public Object runFunction(WebMessage webMessage) {
        logger.debug("{}.{}({})",webMessage.group,webMessage.function,webMessage.args.toString());
        logger.debug("callback :: {}",webMessage.callback);
        Object result = onWebMessage(webMessage);
        callerActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(webMessage.callback != null && !webMessage.callback.isEmpty()) {
                    logger.debug( "콜백으로 보내는 것 >" + result.toString());
                    String script="javascript:("+webMessage.callback+"('%s'))";
                    webView.loadUrl(String.format(script,  result.toString()  ));
                }
            }
        });

        return result;
    }

    private Object onWebMessage(WebMessage webMessage) {
        Object result = null;
        switch (webMessage.group) {
            case "sample" :  switch (webMessage.function){
                                case "test": sampleHandler.test(webMessage.args); break;
                                default: App.INSTANCE.debugToast("구현 필요:" + webMessage.toString() ); break;
                            } break;
            case "nativeSystem" :   switch(webMessage.function){
                                    case "showToast": nativeSystemHandler.showToast(webMessage.args); break;
                                    case "appVersion": result = nativeSystemHandler.getAppVersion(); break;
                                    default: App.INSTANCE.debugToast("구현 필요:" + webMessage.toString() ); break;
                                } break;

            default: App.INSTANCE.debugToast("구현 필요:" + webMessage.toString() );
                break;
        }
        return (result == null) ? "" : result;
    }

}
