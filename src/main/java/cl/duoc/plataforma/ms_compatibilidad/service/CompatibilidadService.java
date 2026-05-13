package cl.duoc.plataforma.ms_compatibilidad.service;

import cl.duoc.plataforma.ms_compatibilidad.dto.FuenteRecomendadaResponseDTO;
import cl.duoc.plataforma.ms_compatibilidad.model.ReglaEnergia;
import cl.duoc.plataforma.ms_compatibilidad.repository.ReglaEnergiaRepository;
import cl.duoc.plataforma.ms_compatibilidad.repository.ReglaSocketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
