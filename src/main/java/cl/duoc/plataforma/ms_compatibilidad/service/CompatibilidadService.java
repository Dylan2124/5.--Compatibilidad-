package cl.duoc.plataforma.ms_compatibilidad.service;

import cl.duoc.plataforma.ms_compatibilidad.dto.ReglaEnergiaDto;
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
 * ═══════════════════════════════════════════════════
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CompatibilidadService {

    private final ReglaSocketRepository socketRepository;
    private final ReglaEnergiaRepository energiaRepository;

    /**
     * Valida si un componente específico puede conectarse a un socket dado.
     * Ejemplo: Validar que un 'CPU' encaje en 'AM4'.
     *
     * @param tipoComponente Tipo del componente (ej: CPU, PLACA_MADRE)
     * @param nombreSocket   Nombre del socket (ej: AM4, LGA1700)
     * @return true si existe la regla de compatibilidad, false en caso contrario
     */
    @Transactional(readOnly = true)
    public boolean validarSocket(String tipoComponente, String nombreSocket) {
        log.info("Validando compatibilidad: {} -> {}", tipoComponente, nombreSocket);
        boolean resultado = socketRepository.existsByTipoComponenteAndNombreSocket(tipoComponente, nombreSocket);
        log.info("Resultado de validación para {} en socket {}: {}", tipoComponente, nombreSocket, resultado);
        return resultado;
    }

    /**
     * Calcula la fuente de poder mínima recomendada según la suma de watts
     * de los componentes seleccionados. Retorna un DTO con el resultado.
     *
     * @param consumoTotalWatts Consumo total estimado en watts
     * @return DTO con la regla de energía aplicada (rango y fuente recomendada)
     * @throws RuntimeException si el consumo está fuera de todos los rangos definidos
     */
    @Transactional(readOnly = true)
    public ReglaEnergiaDto calcularFuenteRecomendada(Integer consumoTotalWatts) {
        log.info("Calculando fuente recomendada para un consumo estimado de {} W", consumoTotalWatts);

        ReglaEnergia regla = energiaRepository.encontrarReglaPorConsumo(consumoTotalWatts)
                .orElseThrow(() -> {
                    log.warn("No existe regla de energía para el consumo: {} W", consumoTotalWatts);
                    return new RuntimeException("No existe regla de energía para un consumo de: " + consumoTotalWatts + " W. Rango soportado: 0 a 2000 W.");
                });

        log.info("Regla encontrada: fuente de {} W recomendada para consumo de {} W", regla.getFuenteRecomendadaWatts(), consumoTotalWatts);

        // Mapear la entidad al DTO de respuesta
        return ReglaEnergiaDto.builder()
                .id(regla.getId())
                .consumoWattsMin(regla.getConsumoWattsMin())
                .consumoWattsMax(regla.getConsumoWattsMax())
                .fuenteRecomendadaWatts(regla.getFuenteRecomendadaWatts())
                .build();
    }
}
