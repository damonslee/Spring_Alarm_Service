package com.hanaset.sky.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "TB_SK_EMAIL_TEMPLATE")
public class SkyEmailTemplateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String code;

    private String subject;

    @Column(name = "file_name")
    private String fileName;

    @OneToOne
    @JoinColumn(name = "param_id")
    private SkyParamEntity param;
}
