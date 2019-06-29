package com.hanaset.sky.repository;

import com.hanaset.sky.entity.SkyEmailTemplateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SkyEmailTemplateRepository extends JpaRepository<SkyEmailTemplateEntity, String> {
}
