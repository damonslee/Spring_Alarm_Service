package com.hanaset.sky.entitiy;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "TB_SK_SMS_TEMPLATE")
public class SkySmsTemplateEntity {

    @Id
    private String id;

    private String template;
}
