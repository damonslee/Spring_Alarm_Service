package com.hanaset.sky.MsgPool;

import com.hanaset.sky.entitiy.SkyEmailTemplateEntity;
import lombok.Data;

@Data
public class MsgItemEmail {

    private String to;
    private SkyEmailTemplateEntity emailTemplateEntity;
    private String msg;

    MsgItemEmail(String to, SkyEmailTemplateEntity emailTemplateEntity, String msg){
        this.to = to;
        this.emailTemplateEntity = emailTemplateEntity;
        this.msg = msg;
    }
}
