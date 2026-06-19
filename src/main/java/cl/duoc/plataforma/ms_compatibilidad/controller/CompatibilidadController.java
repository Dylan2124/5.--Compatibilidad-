package cl.duoc.plataforma.ms_compatibilidad.controller;

import cl.duoc.plataforma.ms_compatibilidad.dto.EvaluarCompatibilidadRequest;
import cl.duoc.plataforma.ms_compatibilidad.dto.EvaluarCompatibilidadResponse;
import cl.duoc.plataforma.ms_compatibilidad.dto.FuenteRecomendadaResponseDTO;
import cl.duoc.plataforma.ms_compatibilidad.dto.ValidarSocketResponseDTO;
import cl.duoc.plataforma.ms_compatibilidad.service.CompatibilidadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Compatibilidad", description = "Motor de validación de compatibilidad física (Sockets) y de energía para componentes de hardware.")
public class CompatibilidadController {

    private final CompatibilidadService compatibilidadService;

    // GET /api/compatibilidad/validar-socket?tipo=CPU&socket=AM4 → 200 OK
    // @NotBlank dispara validación si el parámetro llega vacío.
    // Si falla → GlobalExceptionHandler → 400 Bad Request.
    @GetMapping("/validar-socket")
    @Operation(summary = "Validar compatibilidad de Socket", description = "Verifica si un tipo de componente de hardware es compatible físicamente con un tipo de socket específico (por ejemplo, verificar si un CPU es compatible con un socket AM4).")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Validación realizada con éxito, retorna si es compatible", 
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = ValidarSocketResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Parámetros de entrada inválidos (vacíos o erróneos)")
    })
    public ResponseEntity<ValidarSocketResponseDTO> validarSocket(
            @RequestParam @NotBlank(message = "El tipo de componente no puede estar vacío") 
            @Parameter(description = "Tipo del componente a validar (ej: CPU, PLACA_MADRE)", example = "CPU") String tipo,
            @RequestParam @NotBlank(message = "El nombre del socket no puede estar vacío") 
            @Parameter(description = "Nombre del socket a validar (ej: AM4, LGA1700)", example = "AM4") String socket) {

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
    @Operation(summary = "Calcular fuente de poder recomendada", description = "Calcula y sugiere la capacidad (en Watts) de la fuente de poder recomendada a partir del consumo eléctrico total del hardware especificado.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cálculo de fuente recomendada exitoso", 
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = FuenteRecomendadaResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Parámetro inválido o no existe regla que cubra el consumo solicitado")
    })
    public ResponseEntity<FuenteRecomendadaResponseDTO> obtenerFuenteRecomendada(
            @RequestParam @Min(value = 1, message = "El consumo en watts debe ser mayor a 0") 
            @Parameter(description = "Consumo eléctrico total estimado en Watts", example = "500") Integer watts) {

        return ResponseEntity.ok(compatibilidadService.calcularFuenteRecomendada(watts));
    }

    // POST /api/compatibilidad/evaluar → 200 OK
    // Recibe una lista de IDs de productos y evalúa si son compatibles entre sí
    @PostMapping("/evaluar")
    @Operation(summary = "Evaluar compatibilidad de lista de productos", description = "Recibe una lista de IDs de productos, consulta sus especificaciones en el catálogo y evalúa la compatibilidad tanto de socket (física) como de fuente de poder (energética) del conjunto.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Evaluación finalizada, retorna la compatibilidad y la lista de errores si los hay", 
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = EvaluarCompatibilidadResponse.class))),
        @ApiResponse(responseCode = "400", description = "Cuerpo del request no válido o error al procesar")
    })
    public ResponseEntity<EvaluarCompatibilidadResponse> evaluarCompatibilidad(
            @RequestBody @Parameter(description = "Lista de identificadores de productos a evaluar") EvaluarCompatibilidadRequest request) {
        EvaluarCompatibilidadResponse response = compatibilidadService.evaluar(request);
        return ResponseEntity.ok(response);
    }
}
