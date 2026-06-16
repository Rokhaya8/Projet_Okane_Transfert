package com.okanetransfer.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ComponentScan(basePackages = {"com.okanetransfer.service", "com.okanetransfer.security"})
@EnableJpaRepositories(basePackages = "com.okanetransfer.repository")
@EnableTransactionManagement
@PropertySource("classpath:application.properties")
public class RootConfig {
}
