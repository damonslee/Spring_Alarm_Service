package com.hanaset.sky.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "TB_SK_SMS_TEMPLATE")
public class SkySmsTemplateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String template;

    private String code;

    @OneToOne
    @JoinColumn(name = "param_id")
    private SkyParamEntity param;

}
