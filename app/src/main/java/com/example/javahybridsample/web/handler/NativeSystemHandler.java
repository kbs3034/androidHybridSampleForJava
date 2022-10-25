package com.example.javahybridsample.web.handler;

import java.util.Map;

public interface NativeSystemHandler {
    void showToast(Map<String, Object> args);
    String getAppVersion();
}
