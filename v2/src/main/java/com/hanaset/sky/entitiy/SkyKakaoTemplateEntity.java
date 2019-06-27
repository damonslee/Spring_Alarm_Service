package com.hanaset.sky.entitiy;

import javax.persistence.*;

import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Data
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "TB_SK_KAKAO_TEMPLATE")
public class SkyKakaoTemplateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String code;

    private String name;

    private String context;

    @OneToOne
    @JoinColumn(name = "button_id")
    private SkyKakaoButtonEntity button;

    @OneToOne
    @JoinColumn(name = "param_id")
    private SkyParamEntity param;

}


