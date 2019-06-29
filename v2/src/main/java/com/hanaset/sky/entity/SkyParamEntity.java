package com.hanaset.sky.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "TC_SK_PARAM")
public class SkyParamEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String params;
}
