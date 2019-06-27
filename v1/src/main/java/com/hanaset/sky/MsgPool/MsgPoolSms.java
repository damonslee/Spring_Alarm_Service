package com.hanaset.sky.MsgPool;

import com.hanaset.sky.entitiy.SkySmsTemplateEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.Deque;

@Component
public class MsgPoolSms {

    private Deque<MsgItemSms> msgPool;

    MsgPoolSms(){ msgPool = new ArrayDeque<>(); }

    public void push_back(String num, SkySmsTemplateEntity entity, String msg){
        MsgItemSms item = new MsgItemSms(num, entity, msg);
        msgPool.addLast(item);
    }

    public MsgItemSms pop_front(){
        return msgPool.pollFirst();
    }

    public int size(){
        return msgPool.size();
    }


}
