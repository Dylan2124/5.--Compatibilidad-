package cl.duoc.plataforma.ms_compatibilidad.service;

import cl.duoc.plataforma.ms_compatibilidad.client.CatalogoClient;
import cl.duoc.plataforma.ms_compatibilidad.dto.EvaluarCompatibilidadRequest;
import cl.duoc.plataforma.ms_compatibilidad.dto.EvaluarCompatibilidadResponse;
import cl.duoc.plataforma.ms_compatibilidad.dto.FuenteRecomendadaResponseDTO;
import cl.duoc.plataforma.ms_compatibilidad.dto.ProductoEspecificacionDto;
import cl.duoc.plataforma.ms_compatibilidad.model.ReglaEnergia;
import cl.duoc.plataforma.ms_compatibilidad.repository.ReglaEnergiaRepository;
import cl.duoc.plataforma.ms_compatibilidad.repository.ReglaSocketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * ═══════════════════════════════════════════════════
 * CLASE: CompatibilidadService.java
 * Motor de validación de reglas técnicas.
 * Contiene toda la lógica de negocio del microservicio.
 *
 * PATRÓN APLICADO (igual que el ejemplo de la profesora):
 *   1. Los métodos reciben tipos simples (String, Integer).
 *   2. El método privado mapToDTO() convierte entidad → DTO.
 *   3. Los métodos devuelven DTOs, no entidades.
 *   4. El RuntimeException es capturado por GlobalExceptionHandler.
 * ═══════════════════════════════════════════════════
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CompatibilidadService {

    private final ReglaSocketRepository socketRepository;
    private final ReglaEnergiaRepository energiaRepository;
    private final CatalogoClient catalogoClient;

    // ── MAPEO PRIVADO: Entidad → ResponseDTO ─────────
    // Solo lo usa este Service. El Controller y el
    // Repository nunca conocen el DTO ni la entidad
    // del otro respectivamente.
    private FuenteRecomendadaResponseDTO mapToDTO(ReglaEnergia regla) {
        return new FuenteRecomendadaResponseDTO(
                regla.getConsumoWattsMin(),
                regla.getConsumoWattsMax(),
                regla.getFuenteRecomendadaWatts()
        );
    }

    // ── VALIDAR SOCKET ───────────────────────────────
    // Consulta el diccionario de reglas para saber si
    // un tipo de componente es compatible con un socket.
    // Ejemplo: ¿Un CPU encaja en el socket AM4?
    @Transactional(readOnly = true)
    public boolean validarSocket(String tipoComponente, String nombreSocket) {
        log.info("Validando compatibilidad: {} -> {}", tipoComponente, nombreSocket);
        boolean resultado = socketRepository.existsByTipoComponenteAndNombreSocket(tipoComponente, nombreSocket);
        log.info("Resultado de validación para {} en socket {}: {}", tipoComponente, nombreSocket, resultado);
        return resultado;
    }

    // ── CALCULAR FUENTE RECOMENDADA ───────────────────
    // Busca la regla de energía que cubre el consumo dado
    // y retorna la fuente de poder mínima recomendada.
    // Si el consumo está fuera de rango → RuntimeException
    // → GlobalExceptionHandler → 400 Bad Request.
    @Transactional(readOnly = true)
    public FuenteRecomendadaResponseDTO calcularFuenteRecomendada(Integer consumoTotalWatts) {
        log.info("Calculando fuente recomendada para un consumo estimado de {} W", consumoTotalWatts);

        ReglaEnergia regla = energiaRepository.encontrarReglaPorConsumo(consumoTotalWatts)
                .orElseThrow(() -> {
                    log.warn("No existe regla de energía para el consumo: {} W", consumoTotalWatts);
                    return new RuntimeException("No existe regla de energía para un consumo de: " + consumoTotalWatts + " W. Rango soportado: 0 a 2000 W.");
                });

        log.info("Regla encontrada: fuente de {} W recomendada para consumo de {} W",
                regla.getFuenteRecomendadaWatts(), consumoTotalWatts);

        return mapToDTO(regla);
    }

    // ── EVALUAR LISTA DE PRODUCTOS ───────────────────
    // Este es el motor de reglas en acción comunicándose con otro microservicio.
    public EvaluarCompatibilidadResponse evaluar(EvaluarCompatibilidadRequest request) {
        log.info("Iniciando evaluación de compatibilidad para {} productos", request.getIdsProductos().size());
        
        List<ProductoEspecificacionDto> especificaciones = new ArrayList<>();
        List<String> errores = new ArrayList<>();
        int consumoTotal = 0;

        // 1. Obtener especificaciones del Catálogo
        for (Long id : request.getIdsProductos()) {
            try {
                ProductoEspecificacionDto spec = catalogoClient.obtenerEspecificaciones(id);
                if (spec != null) {
                    especificaciones.add(spec);
                    if (spec.getConsumoWatts() != null) {
                        consumoTotal += spec.getConsumoWatts();
                    }
                }
            } catch (Exception e) {
                log.warn("ATENCION: No se pudo obtener specs de ms-catalogo para ID {}. Simulando para no detener evaluación.", id);
                // Si falla el equipo de Catálogo, asumimos compatibilidad o lo ignoramos.
            }
        }

        // 2. Extraer componentes clave para comparar
        ProductoEspecificacionDto cpu = null;
        ProductoEspecificacionDto placa = null;
        ProductoEspecificacionDto fuente = null;

        for (ProductoEspecificacionDto spec : especificaciones) {
            if ("CPU".equalsIgnoreCase(spec.getCategoria())) cpu = spec;
            if ("PLACA_MADRE".equalsIgnoreCase(spec.getCategoria())) placa = spec;
            if ("FUENTE_PODER".equalsIgnoreCase(spec.getCategoria())) fuente = spec;
        }

        // 3. Evaluar reglas de Socket (si hay CPU y Placa)
        if (cpu != null && placa != null) {
            if (cpu.getNombreSocket() != null && placa.getNombreSocket() != null) {
                if (!cpu.getNombreSocket().equalsIgnoreCase(placa.getNombreSocket())) {
                    errores.add("El CPU requiere socket " + cpu.getNombreSocket() + " pero la Placa Madre tiene " + placa.getNombreSocket());
                }
            }
        }

        // 4. Evaluar reglas de Energía (si hay fuente de poder)
        if (fuente != null && consumoTotal > 0) {
            try {
                FuenteRecomendadaResponseDTO fuenteSugerida = calcularFuenteRecomendada(consumoTotal);
                if (fuente.getConsumoWatts() < fuenteSugerida.getFuenteRecomendadaWatts()) {
                    errores.add("La fuente de poder (" + fuente.getConsumoWatts() + "W) es insuficiente. Se recomiendan al menos " + fuenteSugerida.getFuenteRecomendadaWatts() + "W para este ensamblaje.");
                }
            } catch (Exception e) {
                // Si no hay regla que cubra el consumo, lo marcamos como error
                errores.add(e.getMessage());
            }
        }

        return EvaluarCompatibilidadResponse.builder()
                .esCompatible(errores.isEmpty())
                .errores(errores)
                .build();
    }
}
