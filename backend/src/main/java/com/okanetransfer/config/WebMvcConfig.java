package com.okanetransfer.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "com.okanetransfer")
public class WebMvcConfig implements WebMvcConfigurer {

    // 1. Ta configuration CORS existante (parfaite pour Angular)
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:4200")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    // 2. La fusion de la configuration Jackson (Zéro erreur de Date & Zéro erreur de Proxy Hibernate)
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        ObjectMapper objectMapper = new ObjectMapper();

        // Gère les types LocalDateTime, LocalDate, etc.
        objectMapper.registerModule(new JavaTimeModule());

        // SÉCURITÉ SUPRÊME : Dit à Jackson d'ignorer les proxys Hibernate (ByteBuddy) vides au lieu de planter !
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
        jsonConverter.setObjectMapper(objectMapper);

        // On l'ajoute en première position pour s'assurer que Spring utilise ce convertisseur configuré
        converters.add(0, jsonConverter);
    }
}