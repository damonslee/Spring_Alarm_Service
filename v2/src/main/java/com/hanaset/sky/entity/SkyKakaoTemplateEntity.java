package com.hanaset.sky.entity;

import javax.persistence.*;

import lombok.Data;

@Data
@Entity
@Table(name = "TB_SK_KAKAO_TEMPLATE")
public class SkyKakaoTemplateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String code;

    private String name;

    private String context;

    @OneToOne
    @JoinColumn(name = "param_id")
    private SkyParamEntity param;

}


