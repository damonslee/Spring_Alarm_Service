package com.hanaset.sky.repository;

import com.hanaset.sky.entitiy.SkyMsgLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SkyMsgLogRepository extends JpaRepository<SkyMsgLogEntity, String> {
}
