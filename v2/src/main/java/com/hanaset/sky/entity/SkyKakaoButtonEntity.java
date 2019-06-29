package com.hanaset.sky.entity;

import lombok.Data;
import javax.persistence.*;

@Data
@Entity
@Table(name = "TB_SK_KAKAO_BUTTON")
public class SkyKakaoButtonEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    private String type;

    private String url_pc;

    private String url_mobile;
}
