package com.hanaset.sky.MsgPool;

import com.hanaset.sky.entitiy.SkyEmailTemplateEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.Deque;

@Component
public class MsgPoolEmail {

    private Deque<MsgItemEmail> msgPool;

    MsgPoolEmail(){ msgPool = new ArrayDeque<>(); }

    public void push_back(String msg, SkyEmailTemplateEntity entity, String to){
        MsgItemEmail item = new MsgItemEmail(to, entity, msg);
        msgPool.addLast(item);
    }

    public MsgItemEmail pop_front(){
        return msgPool.pollFirst();
    }

    public int size(){
        return msgPool.size();
    }
}
