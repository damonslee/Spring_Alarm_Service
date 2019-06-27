package com.hanaset.sky.entitiy;

import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Data
@Entity
@DynamicInsert
@DynamicUpdate
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
