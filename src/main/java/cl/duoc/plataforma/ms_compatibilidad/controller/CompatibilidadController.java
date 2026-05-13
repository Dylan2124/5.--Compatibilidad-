package cl.duoc.plataforma.ms_compatibilidad.controller;

import cl.duoc.plataforma.ms_compatibilidad.dto.ReglaEnergiaDto;
import cl.duoc.plataforma.ms_compatibilidad.dto.ReglaSocketDto;
import cl.duoc.plataforma.ms_compatibilidad.service.CompatibilidadService;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * ═══════════════════════════════════════════════════
 * CLASE: CompatibilidadController.java
 * Controla los endpoints HTTP para consultar reglas de compatibilidad.
 * Se usa @Validated para activar la validación de @RequestParam.
 * ═══════════════════════════════════════════════════
 */
@RestController
@RequestMapping("/api/compatibilidad")
@RequiredArgsConstructor
@Validated
public class CompatibilidadController {

    private final CompatibilidadService compatibilidadService;

    /**
     * Valida si un componente es compatible con un socket específico.
     * Ejemplo GET: /api/compatibilidad/validar-socket?tipo=CPU&socket=AM4
     *
     * @param tipo   El tipo de componente (ej: CPU, PLACA_MADRE). No puede ser vacío.
     * @param socket El nombre del socket (ej: AM4, LGA1700). No puede ser vacío.
     * @return Un DTO con el resultado de la validación.
     */
    @GetMapping("/validar-socket")
    public ResponseEntity<ReglaSocketDto> validarSocket(
            @RequestParam @NotBlank(message = "El tipo de componente no puede estar vacío") String tipo,
            @RequestParam @NotBlank(message = "El nombre del socket no puede estar vacío") String socket) {

        boolean esCompatible = compatibilidadService.validarSocket(tipo, socket);

        // Se reutiliza ReglaSocketDto como objeto de respuesta estructurado
        ReglaSocketDto respuesta = ReglaSocketDto.builder()
                .tipoComponente(tipo)
                .nombreSocket(socket)
                .generacionSoportada(esCompatible ? "Compatible" : "No compatible")
                .build();

        return ResponseEntity.ok(respuesta);
    }

    /**
     * Calcula la fuente de poder mínima recomendada según el consumo total en watts.
     * Ejemplo GET: /api/compatibilidad/fuente-recomendada?watts=450
     *
     * @param watts El consumo total estimado en watts. Debe ser al menos 1.
     * @return Un DTO con el rango de consumo y la fuente recomendada.
     */
    @GetMapping("/fuente-recomendada")
    public ResponseEntity<ReglaEnergiaDto> obtenerFuenteRecomendada(
            @RequestParam @Min(value = 1, message = "El consumo en watts debe ser al menos 1") Integer watts) {

        ReglaEnergiaDto respuesta = compatibilidadService.calcularFuenteRecomendada(watts);

        return ResponseEntity.ok(respuesta);
    }
}
