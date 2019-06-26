package com.hanaset.sky.MsgPool;

import com.hanaset.sky.entitiy.SkyKakaoTemplateEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.Deque;

@Component
public class MsgPoolKakao {

    private Deque<MsgItemKakao> msgPool;

    MsgPoolKakao(){
        msgPool = new ArrayDeque<>();
    }

    public void push_back(String msg, SkyKakaoTemplateEntity entity, String num){
        MsgItemKakao item = new MsgItemKakao(msg, entity, num);
        msgPool.addLast(item);
    }

    public MsgItemKakao pop_front(){

        if(msgPool.size() == 0) {
            return null;
        }
        return msgPool.pollFirst();
    }

    public int size(){
        return msgPool.size();
    }

}
