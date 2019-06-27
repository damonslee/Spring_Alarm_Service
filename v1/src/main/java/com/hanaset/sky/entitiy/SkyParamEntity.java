package com.hanaset.sky.entitiy;

import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Data
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "TB_SK_PARAM")
public class SkyParamEntity {

    @Id
    @Column(name = "param_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;

    private String params;
}
