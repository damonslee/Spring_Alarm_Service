package com.hanaset.sky.entitiy;

import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Data
@Entity
@DynamicInsert
@DynamicUpdate
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
