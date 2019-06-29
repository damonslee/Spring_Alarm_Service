package com.hanaset.sky.repository;

import com.hanaset.sky.entity.SkyKakaoTemplateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SkyKakaoTemplateRepository extends JpaRepository<SkyKakaoTemplateEntity, String> {

    Optional<SkyKakaoTemplateEntity> findById(String code);
}
