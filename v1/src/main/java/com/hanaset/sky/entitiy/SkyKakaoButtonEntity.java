package com.hanaset.sky.entitiy;

import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Data
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "TB_SK_KAKAO_BUTTON")
public class SkyKakaoButtonEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "button_id")
    private String id;

    private String name;

    private String type;

    private String url_pc;

    private String url_mobile;
}
