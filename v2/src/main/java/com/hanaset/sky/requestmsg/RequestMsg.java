package com.hanaset.sky.requestmsg;

import lombok.Data;
import org.json.simple.JSONObject;

import javax.validation.constraints.NotNull;

@Data
public class RequestMsg {
    @NotNull
    private String code;

    @NotNull
    private String to;
    
    private JSONObject param;
}
