package com.hanaset.sky.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateSettings;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jmx.export.MBeanExporter;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.util.Map;

@Configuration
@ComponentScan({
        "com.hanaset.sky.repository"
})
@EnableJpaRepositories(
        basePackages = {"com.hanaset.sky.repository"},
        entityManagerFactoryRef = "EntityManagerFactory",
        transactionManagerRef = "TransactionManager"
)
@PropertySource("classpath:properties/database/${spring.profiles.active}.properties")
public class DatabaseConfig {

    private final MBeanExporter mbeanExporter;

    public DatabaseConfig(MBeanExporter mbeanExporter) {
        this.mbeanExporter = mbeanExporter;
    }

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "gdac.jpa")
    public JpaProperties gdacJpaProperties() {
        return new JpaProperties();
    }

    @Bean
    @Primary
    public HibernateSettings gdacHibernateSettings() {
        return new HibernateSettings();
    }

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "gdac")
    public HikariConfig gdacHikariConfig() {
        return new HikariConfig();
    }

    @Bean
    @Primary
    public DataSource gdacDataSource() {
        HikariDataSource dataSource = new HikariDataSource(gdacHikariConfig());
        mbeanExporter.addExcludedBean("gdacDataSource");
        return dataSource;
    }


    @Bean
    @Primary
    public LocalContainerEntityManagerFactoryBean gdacEntityManagerFactory(EntityManagerFactoryBuilder builder) {

        return builder
                .dataSource(gdacDataSource())
                .packages("com.hanaset.sky.entity")
                .persistenceUnit("gdacPersistenceUnit")
                .properties(getVendorProperties())
                .build();
    }

    private Map<String, Object> getVendorProperties() {

        HibernateProperties hibernateProperties = new HibernateProperties();
        //hibernateProperties.setDdlAuto("update");
        hibernateProperties.getNaming()
                .setImplicitStrategy("org.hibernate.boot.model.naming.ImplicitNamingStrategyLegacyJpaImpl");
        hibernateProperties.getNaming()
                .setPhysicalStrategy("org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl");
        Map<String, Object> properties = hibernateProperties.
                determineHibernateProperties(gdacJpaProperties().getProperties(), gdacHibernateSettings());

        return properties;

        //return gdacJpaProperties().getHibernateProperties(gdacHibernateSettings());
    }


    @Bean(name = "gdacJdbcTemplate")
    public JdbcTemplate jdbcTemplate(@Qualifier("gdacDataSource") DataSource dataSource){
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return jdbcTemplate;
    }


    @Bean
    @Primary
    @SuppressWarnings("ConstantConditions")
    public PlatformTransactionManager gdacTransactionManager(EntityManagerFactoryBuilder builder) {
        return new JpaTransactionManager(gdacEntityManagerFactory(builder).getObject());
    }


    @Bean
    @Primary
    public TransactionTemplate gdacTransactionTemplate(EntityManagerFactoryBuilder builder) {
        return new TransactionTemplate(gdacTransactionManager(builder));
    }


}
