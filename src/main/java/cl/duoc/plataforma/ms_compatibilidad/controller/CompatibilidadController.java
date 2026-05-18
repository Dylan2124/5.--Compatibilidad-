package cl.duoc.plataforma.ms_compatibilidad.controller;

import cl.duoc.plataforma.ms_compatibilidad.dto.EvaluarCompatibilidadRequest;
import cl.duoc.plataforma.ms_compatibilidad.dto.EvaluarCompatibilidadResponse;
import cl.duoc.plataforma.ms_compatibilidad.dto.FuenteRecomendadaResponseDTO;
import cl.duoc.plataforma.ms_compatibilidad.dto.ValidarSocketResponseDTO;
import cl.duoc.plataforma.ms_compatibilidad.service.CompatibilidadService;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * * ═══════════════════════════════════════════════════
 * * CLASE: CompatibilidadController.java
 * * PATRÓN APLICADO (igual que el ejemplo de la profesora):
 * *   1. No importa ninguna entidad directamente.
 * *   2. Todos los métodos devuelven ResponseDTO.
 * *   3. El controller solo orquesta: recibe la petición,
 * *      llama al service y devuelve el resultado. Nada más.
 * *   4. @Validated activa la validación de @RequestParam.
 * *      Si falla → GlobalExceptionHandler → 400 Bad Request.
 * * ═══════════════════════════════════════════════════*/
@RestController
@RequestMapping("/api/compatibilidad")
@RequiredArgsConstructor
@Validated
public class CompatibilidadController {

    private final CompatibilidadService compatibilidadService;

    // GET /api/compatibilidad/validar-socket?tipo=CPU&socket=AM4 → 200 OK
    // @NotBlank dispara validación si el parámetro llega vacío.
    // Si falla → GlobalExceptionHandler → 400 Bad Request.
    @GetMapping("/validar-socket")
    public ResponseEntity<ValidarSocketResponseDTO> validarSocket(
            @RequestParam @NotBlank(message = "El tipo de componente no puede estar vacío") String tipo,
            @RequestParam @NotBlank(message = "El nombre del socket no puede estar vacío") String socket) {

        boolean esCompatible = compatibilidadService.validarSocket(tipo, socket);

        // El controller arma el DTO de respuesta con el resultado del service
        ValidarSocketResponseDTO respuesta = new ValidarSocketResponseDTO(tipo, socket, esCompatible);

        return ResponseEntity.ok(respuesta);
    }

    // GET /api/compatibilidad/fuente-recomendada?watts=450 → 200 OK
    // @Min(1) dispara validación si el parámetro es 0 o negativo.
    // Si el consumo está fuera de rango → Service lanza RuntimeException
    // → GlobalExceptionHandler → 400 Bad Request.
    @GetMapping("/fuente-recomendada")
    public ResponseEntity<FuenteRecomendadaResponseDTO> obtenerFuenteRecomendada(
            @RequestParam @Min(value = 1, message = "El consumo en watts debe ser mayor a 0") Integer watts) {

        return ResponseEntity.ok(compatibilidadService.calcularFuenteRecomendada(watts));
    }

    // POST /api/compatibilidad/evaluar → 200 OK
    // Recibe una lista de IDs de productos y evalúa si son compatibles entre sí
    @PostMapping("/evaluar")
    public ResponseEntity<EvaluarCompatibilidadResponse> evaluarCompatibilidad(@RequestBody EvaluarCompatibilidadRequest request) {
        EvaluarCompatibilidadResponse response = compatibilidadService.evaluar(request);
        return ResponseEntity.ok(response);
    }
}
