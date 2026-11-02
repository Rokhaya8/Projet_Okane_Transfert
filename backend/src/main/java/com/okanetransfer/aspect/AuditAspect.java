package com.okanetransfer.aspect;

import com.okanetransfer.annotation.Auditable;
import com.okanetransfer.entity.User;
import com.okanetransfer.service.AuditLogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.StringJoiner;

@Aspect
@Component
@RequiredArgsConstructor
public class AuditAspect {

    private final AuditLogService auditLogService;

    @AfterReturning(pointcut = "@annotation(auditable)", returning = "returnValue")
    public void audit(JoinPoint joinPoint, Auditable auditable, Object returnValue) {

        // 1. Récupérer l'utilisateur connecté
        User currentUser = getCurrentUser();

        // 2. Récupérer l'IP
        String ipAddress = getIpAddress();

        // 3. Construire details à partir des paramètres
        String details = buildDetails(joinPoint);

        // 4. Récupérer entityId si un param s'appelle "id" ou "entityId"
        Long entityId = extractEntityId(joinPoint);

        auditLogService.log(
                currentUser,
                auditable.action(),
                auditable.entityType(),
                entityId,
                details,
                ipAddress
        );
    }

    // ── Helpers ──────────────────────────────────────────

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof User user) {
            return user;
        }
        return null; // avant auth : actions anonymes
    }

    private String getIpAddress() {
        try {
            HttpServletRequest request = ((ServletRequestAttributes)
                    RequestContextHolder.currentRequestAttributes()).getRequest();
            String ip = request.getHeader("X-Forwarded-For");
            return (ip != null && !ip.isEmpty()) ? ip.split(",")[0] : request.getRemoteAddr();
        } catch (Exception e) {
            return "unknown";
        }
    }

    private String buildDetails(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Parameter[] parameters = signature.getMethod().getParameters();
        Object[] args = joinPoint.getArgs();

        StringJoiner sj = new StringJoiner(", ");
        for (int i = 0; i < parameters.length; i++) {
            sj.add(parameters[i].getName() + "=" + args[i]);
        }
        return sj.toString();
    }

    private Long extractEntityId(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Parameter[] parameters = signature.getMethod().getParameters();
        Object[] args = joinPoint.getArgs();

        for (int i = 0; i < parameters.length; i++) {
            String name = parameters[i].getName().toLowerCase();
            if ((name.equals("id") || name.equals("entityid")) && args[i] instanceof Long l) {
                return l;
            }
        }
        return null;
    }
}