package cl.duoc.plataforma.ms_compatibilidad.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ═══════════════════════════════════════════════════
 * CLASE: GlobalExceptionHandler.java
 *
 * ¿Por qué existe este archivo?
 *   Sin él, cuando la validación falla o el service
 *   lanza un error, Spring devuelve un JSON enorme
 *   e ilegible con campos internos.
 *   Con él, el cliente recibe respuestas limpias como:
 *   { "error": "El tipo de componente no puede estar vacío" }
 *
 * @RestControllerAdvice: marca esta clase como manejador
 *   global de excepciones para todos los Controllers.
 *   Es un @Component, Spring lo detecta automáticamente.
 *
 * @ExceptionHandler(Tipo.class): indica qué tipo de
 *   excepción maneja cada método.
 * ═══════════════════════════════════════════════════
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // ── ERROR DE VALIDACIÓN DE @RequestParam ─────────
    // Se dispara cuando un @RequestParam no cumple las
    // restricciones (@NotBlank, @Min, etc.) en conjunto
    // con @Validated en el Controller.
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolation(ConstraintViolationException ex) {
        String mensaje = ex.getConstraintViolations().stream()
                .map(v -> v.getMessage())
                .collect(Collectors.joining(", "));

        Map<String, String> error = new LinkedHashMap<>();
        error.put("error", mensaje);

        log.warn("Parámetro inválido en la URL: {}", mensaje);
        return ResponseEntity.badRequest().body(error);
    }

    // ── ERROR DE PARÁMETRO FALTANTE ──────────────────
    // Se dispara cuando el cliente no envía un @RequestParam
    // que es obligatorio (ej: falta ?tipo= en la URL).
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, String>> handleMissingParams(MissingServletRequestParameterException ex) {
        Map<String, String> error = new LinkedHashMap<>();
        error.put("error", "Falta el parámetro requerido: '" + ex.getParameterName() + "'");

        log.warn("Falta parámetro en la URL: {}", ex.getParameterName());
        return ResponseEntity.badRequest().body(error);
    }

    // ── ERROR DE NEGOCIO (consumo fuera de rango, etc.) ──
    // Se dispara cuando el Service lanza RuntimeException.
    // Usamos 400 y no 500 porque el servidor funcionó
    // correctamente; fue el dato enviado el que causó el problema.
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
        Map<String, String> error = new LinkedHashMap<>();
        error.put("error", ex.getMessage());

        log.error("Excepción de negocio en Compatibilidad: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(error);
    }
}
