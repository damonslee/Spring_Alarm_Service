package com.hanaset.sky.entity;

import lombok.Builder;
import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@Entity
@Builder
@Table(name = "TB_SK_MSG_LOG")
public class SkyMsgLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "msg_type")
    private String msgType;

    private String code;

    @Column(name = "req_time")
    private Timestamp reqTime;

    @Column(name = "comp_time")
    private Timestamp compTime;

    private String result;

    private String address;

    private String param;
}
