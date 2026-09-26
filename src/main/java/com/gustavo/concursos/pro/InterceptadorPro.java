package com.gustavo.concursos.pro;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// Aplica @RequerPro antes de o controller rodar.
@Configuration
public class InterceptadorPro implements HandlerInterceptor, WebMvcConfigurer {

    private final AcessoPro acessoPro;

    public InterceptadorPro(AcessoPro acessoPro) {
        this.acessoPro = acessoPro;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(this);
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod metodo)) return true;
        RequerPro regra = metodo.getMethodAnnotation(RequerPro.class);
        if (regra == null) regra = metodo.getBeanType().getAnnotation(RequerPro.class);
        if (regra != null) acessoPro.exigir(SecurityContextHolder.getContext().getAuthentication(), regra.value());
        return true;
    }
}
