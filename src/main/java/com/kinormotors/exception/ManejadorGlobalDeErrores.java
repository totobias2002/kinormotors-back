package com.kinormotors.exception;

import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.LinkedHashMap;
import java.util.Map;

/** Respuestas de error uniformes: {"error": "..."} o, en validaciones, {"campo": "mensaje"}. */
@RestControllerAdvice
public class ManejadorGlobalDeErrores {

    private static final Logger log = LoggerFactory.getLogger(ManejadorGlobalDeErrores.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> noEncontrado(ResourceNotFoundException e) {
        return error(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> pedidoInvalido(IllegalArgumentException e) {
        return error(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class,
            ConstraintViolationException.class})
    public ResponseEntity<Map<String, String>> cuerpoInvalido(Exception e) {
        return error(HttpStatus.BAD_REQUEST, "Pedido invalido");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> validacion(MethodArgumentNotValidException e) {
        Map<String, String> campos = new LinkedHashMap<>();
        e.getBindingResult().getFieldErrors().forEach(f -> campos.putIfAbsent(f.getField(), f.getDefaultMessage()));
        return ResponseEntity.badRequest().body(campos);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, String>> credenciales(BadCredentialsException e) {
        return error(HttpStatus.UNAUTHORIZED, "Email o contraseña incorrectos");
    }

    @ExceptionHandler(DemasiadasSolicitudesException.class)
    public ResponseEntity<Map<String, String>> demasiadas(DemasiadasSolicitudesException e) {
        return error(HttpStatus.TOO_MANY_REQUESTS, e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> inesperado(Exception e) {
        log.error("Error no controlado", e);
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "Ocurrio un error inesperado");
    }

    private ResponseEntity<Map<String, String>> error(HttpStatus estado, String mensaje) {
        return ResponseEntity.status(estado).body(Map.of("error", mensaje));
    }
}
