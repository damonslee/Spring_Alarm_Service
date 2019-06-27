package com.hanaset.sky.item;

import lombok.Builder;
import lombok.Data;
import org.json.simple.JSONObject;

@Data
@Builder
public class ResponseItem {

    private String code;

    private JSONObject data;

}
