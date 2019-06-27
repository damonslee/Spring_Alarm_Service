package com.hanaset.sky.entitiy;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@Entity
@Table(name = "TB_SK_MSG_LOG")
public class SkyMsgLogEntity {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "msg_type")
    private String msgType;

    private String code;

    private Timestamp time;

    private String result;

    private String address;
}
