package com.hanaset.sky.cache;

import com.hanaset.sky.entitiy.SkyEmailTemplateEntity;
import com.hanaset.sky.repository.SkyEmailTemplateRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
@Data
public class EmailCache {

    @Autowired
    SkyEmailTemplateRepository skyEmailTemplateRepository;

    List<SkyEmailTemplateEntity> skyEmailTemplateEntityList;

    public SkyEmailTemplateEntity findForTemplate(String code){

        for(SkyEmailTemplateEntity entity : skyEmailTemplateEntityList){

            if(entity.getId().equals(code)){
                return entity;
            }
        }

        return null;
    }

    @PostConstruct
    private void update(){
        skyEmailTemplateEntityList = skyEmailTemplateRepository.findAll();
    }
}
