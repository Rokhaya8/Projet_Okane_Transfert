package com.okanetransfer.config;

import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;

public class SecurityWebInitializer extends AbstractSecurityWebApplicationInitializer {
    // Vide volontairement.
    // Le simple fait d'hériter de AbstractSecurityWebApplicationInitializer
    // enregistre le springSecurityFilterChain dans Tomcat,
    // ce qui active le filtre de sécurité (et donc le CORS de SecurityConfig).
}