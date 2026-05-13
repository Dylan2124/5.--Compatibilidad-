package cl.duoc.plataforma.ms_compatibilidad.controller;

import cl.duoc.plataforma.ms_compatibilidad.service.CompatibilidadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * ═══════════════════════════════════════════════════
 * CLASE: CompatibilidadController.java
 * Controla los endpoints HTTP para consultar reglas de compatibilidad.
 * ═══════════════════════════════════════════════════
 */
@RestController
@RequestMapping("/api/compatibilidad")
@RequiredArgsConstructor
public class CompatibilidadController {

    private final CompatibilidadService compatibilidadService;

    /**
     * Endpoint para validar si un componente cabe en un socket específico.
     * Ejemplo GET: /api/compatibilidad/validar-socket?tipo=CPU&socket=AM4
     */
    @GetMapping("/validar-socket")
    public ResponseEntity<Map<String, Object>> validarSocket(
            @RequestParam String tipo,
            @RequestParam String socket) {
        
        boolean esCompatible = compatibilidadService.validarSocket(tipo, socket);
        
        Map<String, Object> response = new HashMap<>();
        response.put("tipoComponente", tipo);
        response.put("nombreSocket", socket);
        response.put("compatible", esCompatible);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint para obtener la fuente de poder mínima recomendada 
     * según los watts totales del carrito.
     * Ejemplo GET: /api/compatibilidad/fuente-recomendada?watts=450
     */
    @GetMapping("/fuente-recomendada")
    public ResponseEntity<Map<String, Object>> obtenerFuenteRecomendada(
            @RequestParam Integer watts) {
        
        Integer fuente = compatibilidadService.calcularFuenteRecomendada(watts);
        
        Map<String, Object> response = new HashMap<>();
        response.put("consumoEstimadoWatts", watts);
        response.put("fuenteRecomendadaWatts", fuente);
        
        return ResponseEntity.ok(response);
    }
}
