package com.example.javahybridsample;

import android.os.Bundle;
import android.webkit.WebChromeClient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;


import com.example.javahybridsample.databinding.ActivitySampleWebviewBinding;
import com.example.javahybridsample.web.WebBridge;
import com.example.javahybridsample.web.handler.SampleHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class SampleWebviewActivity extends AppCompatActivity {
    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    private ActivitySampleWebviewBinding binding;
    private SampleViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySampleWebviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String url = getIntent().getStringExtra("url");

        viewModel = new ViewModelProvider(this).get(SampleViewModel.class);

        WebBridge webBridge = new WebBridge(binding.webView, this);
        webBridge.sampleHandler = new SampleHandler() {
            @Override
            public void test(Map<String, Object> args) {
                App.INSTANCE.debugToast("test bridge :: " + args.toString());
            }
        };

        //base webview로 공통화 작업필요
        binding.webView.getSettings().setJavaScriptEnabled(true);
        binding.webView.setWebContentsDebuggingEnabled(true);
        binding.webView.setWebChromeClient(new WebChromeClient());
        binding.webView.addJavascriptInterface(webBridge, WebBridge.DEFAULT_WEB_BRIDGE_NAME);
        binding.webView.loadUrl(url);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding.webView.destroy();
    }
}
