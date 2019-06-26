package com.hanaset.sky.web.rest;

import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface NotifyRestImp {

    @PostMapping()
    String requestSend(@RequestBody List<JSONObject> request);
}
