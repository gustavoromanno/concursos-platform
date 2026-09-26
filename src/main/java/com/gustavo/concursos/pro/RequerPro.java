package com.gustavo.concursos.pro;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marca um controller (ou metodo) como exclusivo do plano Pro.
 * Quem nao e Pro recebe 402 (Payment Required) com mensagem explicativa;
 * o frontend reconhece o 402 e oferece os planos.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequerPro {

    /** Nome do recurso, usado na mensagem ("Revisao espacada e um recurso do plano Pro."). */
    String value();
}
