package com.hanaset.sky.MsgPool;

import com.hanaset.sky.entitiy.SkyKakaoTemplateEntity;
import lombok.Data;

@Data
public class MsgItemKakao{

    String msg;
    SkyKakaoTemplateEntity entity;
    String num;

    MsgItemKakao(String msg, SkyKakaoTemplateEntity entity, String num){
        this.msg = msg;
        this.entity = entity;
        this.num = num;
    }
}
