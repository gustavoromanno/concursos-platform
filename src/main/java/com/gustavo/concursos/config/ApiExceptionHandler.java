package com.gustavo.concursos.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.Map;

/**
 * Traduz erros tecnicos comuns em mensagens que o usuario entende.
 *
 * Sem isto, uma senha curta no cadastro devolvia o texto interno da validacao
 * ("Validation failed for argument [0] in public ...") e o frontend exibia
 * isso em vermelho. Continua devolvendo o status HTTP correto.
 *
 * De proposito NAO ha um tratador generico de Exception: excecoes de
 * autenticacao precisam seguir para o Spring Security (401/403).
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> validacao(MethodArgumentNotValidException e) {
        String mensagem = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .filter(m -> m != null && !m.isBlank())
                .findFirst()
                .orElse("Dados inválidos.");
        return corpo(HttpStatus.BAD_REQUEST, mensagem);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> corpoInvalido(HttpMessageNotReadableException e) {
        return corpo(HttpStatus.BAD_REQUEST, "Requisição inválida.");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> integridade(DataIntegrityViolationException e) {
        log.warn("Violacao de integridade: {}", e.getMostSpecificCause().getMessage());
        return corpo(HttpStatus.CONFLICT, "Esse registro já existe ou está em uso.");
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Map<String, Object>> arquivoGrande(MaxUploadSizeExceededException e) {
        return corpo(HttpStatus.PAYLOAD_TOO_LARGE, "Arquivo grande demais (máximo de 30 MB por PDF).");
    }

    private ResponseEntity<Map<String, Object>> corpo(HttpStatus status, String mensagem) {
        return ResponseEntity.status(status).body(Map.of("status", status.value(), "message", mensagem));
    }
}
