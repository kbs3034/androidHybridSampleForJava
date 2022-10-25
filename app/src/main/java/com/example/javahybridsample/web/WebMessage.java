package com.example.javahybridsample.web;

import java.util.Map;

public class WebMessage {
    public String group;
    public String function;
    public String callback;
    public Map<String, Object> args;

    @Override
    public String toString() {
        return "WebMessage{" +
                "group='" + group + '\'' +
                ", function='" + function + '\'' +
                ", callback='" + callback + '\'' +
                ", args=" + args.toString() +
                '}';
    }
}
