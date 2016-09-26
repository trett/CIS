package ru.trett.cis.config;

import org.apache.commons.dbcp2.BasicDataSource;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
@ComponentScan({"ru.trett.cis.validators", "ru.trett.cis.services", "ru.trett.cis.DAO"})
public class JPAConfig {

    @Configuration
    @Profile("prod")
    @PropertySource("/WEB-INF/resources/database.properties")
    static class Production{}

    @Configuration
    @Profile("test")
    @PropertySource("/WEB-INF/resources/test.properties")
    static class Test{}

    @Autowired
    private Environment env;

    @Bean
    public DataSource dataSource() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(env.getProperty("hibernate.connection.driverClassName"));
        dataSource.setUrl(env.getProperty("hibernate.connection.url"));
        dataSource.setUsername(env.getProperty("hibernate.connection.username"));
        dataSource.setPassword(env.getProperty("hibernate.connection.password"));
        return dataSource;
    }

    @Bean
    public LocalSessionFactoryBean sessionFactory() {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(dataSource());
        sessionFactory.setPackagesToScan("ru.trett.cis.models");
        sessionFactory.setHibernateProperties(hibernateProperties());
        return sessionFactory;
    }

    @Bean
    @Autowired
    public HibernateTransactionManager transactionManager(SessionFactory sessionFactory) {
        HibernateTransactionManager txManager = new HibernateTransactionManager();
        txManager.setSessionFactory(sessionFactory);
        return txManager;
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

    private Properties hibernateProperties() {
        return new Properties() {{
            setProperty("hibernate.dialect", env.getProperty("hibernate.dialect"));
            setProperty("hibernate.show_sql", env.getProperty("hibernate.show_sql"));
            setProperty("hibernate.hbm2ddl.auto", env.getProperty("hibernate.hbm2ddl"));
            setProperty("hibernate.search.default.directory_provider",
                    env.getProperty("hibernate.search.default.directory_provider"));
            setProperty("hibernate.search.default.indexBase",
                    env.getProperty("hibernate.search.default.indexBase"));
            setProperty("hibernate.cache.use_second_level_cache", "true");
            setProperty("hibernate.cache.region.factory_class",
                    "org.hibernate.cache.ehcache.SingletonEhCacheRegionFactory");
            setProperty("hibernate.cache.use_query_cache", "true");
        }};
    }

}
