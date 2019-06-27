package com.hanaset.sky.MsgPool;

import com.hanaset.sky.entitiy.SkySmsTemplateEntity;
import lombok.Data;

@Data
public class MsgItemSms {

    private String num;

    private SkySmsTemplateEntity entity;

    private String msg;

    MsgItemSms(String num, SkySmsTemplateEntity entity, String msg){
        this.num = num;
        this.entity = entity;
        this.msg = msg;
    }
}
