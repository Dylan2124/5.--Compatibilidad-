package cl.duoc.plataforma.ms_compatibilidad.service;

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
     */
    @Transactional(readOnly = true)
    public boolean validarSocket(String tipoComponente, String nombreSocket) {
        log.info("Validando compatibilidad: {} -> {}", tipoComponente, nombreSocket);
        return socketRepository.existsByTipoComponenteAndNombreSocket(tipoComponente, nombreSocket);
    }

    /**
     * Calcula la fuente de poder mínima recomendada según la suma de watts
     * de los componentes seleccionados en el carrito.
     */
    @Transactional(readOnly = true)
    public Integer calcularFuenteRecomendada(Integer consumoTotalWatts) {
        log.info("Calculando fuente recomendada para un consumo estimado de {} W", consumoTotalWatts);
        
        ReglaEnergia regla = energiaRepository.encontrarReglaPorConsumo(consumoTotalWatts)
                .orElseThrow(() -> new RuntimeException("No existe regla de energía para un consumo tan alto o bajo: " + consumoTotalWatts));
        
        return regla.getFuenteRecomendadaWatts();
    }
}
