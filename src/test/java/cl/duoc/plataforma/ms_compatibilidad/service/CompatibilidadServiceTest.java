package cl.duoc.plataforma.ms_compatibilidad.service;

import cl.duoc.plataforma.ms_compatibilidad.client.CatalogoClient;
import cl.duoc.plataforma.ms_compatibilidad.dto.EvaluarCompatibilidadRequest;
import cl.duoc.plataforma.ms_compatibilidad.dto.EvaluarCompatibilidadResponse;
import cl.duoc.plataforma.ms_compatibilidad.dto.FuenteRecomendadaResponseDTO;
import cl.duoc.plataforma.ms_compatibilidad.dto.ProductoEspecificacionDto;
import cl.duoc.plataforma.ms_compatibilidad.model.ReglaEnergia;
import cl.duoc.plataforma.ms_compatibilidad.repository.ReglaEnergiaRepository;
import cl.duoc.plataforma.ms_compatibilidad.repository.ReglaSocketRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompatibilidadServiceTest {

    @Mock
    private ReglaSocketRepository socketRepository;

    @Mock
    private ReglaEnergiaRepository energiaRepository;

    @Mock
    private CatalogoClient catalogoClient;

    @InjectMocks
    private CompatibilidadService compatibilidadService;

    // ── PRUEBAS: validarSocket ──────────────────────────────────────────────

    @Test
    void testValidarSocket_Compatible_ShouldReturnTrue() {
        // Dado
        String tipoComponente = "CPU";
        String nombreSocket = "AM4";
        when(socketRepository.existsByTipoComponenteAndNombreSocket(tipoComponente, nombreSocket)).thenReturn(true);

        // Cuando
        boolean result = compatibilidadService.validarSocket(tipoComponente, nombreSocket);

        // Entonces
        assertTrue(result);
        verify(socketRepository, times(1)).existsByTipoComponenteAndNombreSocket(tipoComponente, nombreSocket);
    }

    @Test
    void testValidarSocket_Incompatible_ShouldReturnFalse() {
        // Dado
        String tipoComponente = "CPU";
        String nombreSocket = "LGA1200";
        when(socketRepository.existsByTipoComponenteAndNombreSocket(tipoComponente, nombreSocket)).thenReturn(false);

        // Cuando
        boolean result = compatibilidadService.validarSocket(tipoComponente, nombreSocket);

        // Entonces
        assertFalse(result);
        verify(socketRepository, times(1)).existsByTipoComponenteAndNombreSocket(tipoComponente, nombreSocket);
    }

    // ── PRUEBAS: calcularFuenteRecomendada ───────────────────────────────────

    @Test
    void testCalcularFuenteRecomendada_RuleExists_ShouldReturnDto() {
        // Dado
        Integer consumoTotalWatts = 350;
        ReglaEnergia regla = new ReglaEnergia();
        regla.setConsumoWattsMin(300);
        regla.setConsumoWattsMax(400);
        regla.setFuenteRecomendadaWatts(500);

        when(energiaRepository.encontrarReglaPorConsumo(consumoTotalWatts)).thenReturn(Optional.of(regla));

        // Cuando
        FuenteRecomendadaResponseDTO response = compatibilidadService.calcularFuenteRecomendada(consumoTotalWatts);

        // Entonces
        assertNotNull(response);
        assertEquals(300, response.getConsumoWattsMin());
        assertEquals(400, response.getConsumoWattsMax());
        assertEquals(500, response.getFuenteRecomendadaWatts());
        verify(energiaRepository, times(1)).encontrarReglaPorConsumo(consumoTotalWatts);
    }

    @Test
    void testCalcularFuenteRecomendada_RuleDoesNotExist_ShouldThrowException() {
        // Dado
        Integer consumoTotalWatts = 2500;
        when(energiaRepository.encontrarReglaPorConsumo(consumoTotalWatts)).thenReturn(Optional.empty());

        // Cuando y Entonces
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            compatibilidadService.calcularFuenteRecomendada(consumoTotalWatts);
        });

        assertTrue(exception.getMessage().contains("No existe regla de energía"));
        verify(energiaRepository, times(1)).encontrarReglaPorConsumo(consumoTotalWatts);
    }

    // ── PRUEBAS: evaluar ─────────────────────────────────────────────────────

    @Test
    void testEvaluar_AllCompatible_ShouldReturnCompatible() {
        // Dado
        EvaluarCompatibilidadRequest request = new EvaluarCompatibilidadRequest();
        request.setIdsProductos(Arrays.asList(1L, 2L, 3L));

        ProductoEspecificacionDto cpuSpec = new ProductoEspecificacionDto();
        cpuSpec.setCategoria("CPU");
        cpuSpec.setNombreSocket("AM4");
        cpuSpec.setConsumoWatts(65);

        ProductoEspecificacionDto placaSpec = new ProductoEspecificacionDto();
        placaSpec.setCategoria("PLACA_MADRE");
        placaSpec.setNombreSocket("AM4");
        placaSpec.setConsumoWatts(50);

        ProductoEspecificacionDto fuenteSpec = new ProductoEspecificacionDto();
        fuenteSpec.setCategoria("FUENTE_PODER");
        fuenteSpec.setConsumoWatts(600);

        when(catalogoClient.obtenerEspecificaciones(1L)).thenReturn(cpuSpec);
        when(catalogoClient.obtenerEspecificaciones(2L)).thenReturn(placaSpec);
        when(catalogoClient.obtenerEspecificaciones(3L)).thenReturn(fuenteSpec);

        ReglaEnergia regla = new ReglaEnergia();
        regla.setConsumoWattsMin(100);
        regla.setConsumoWattsMax(200);
        regla.setFuenteRecomendadaWatts(500);
        when(energiaRepository.encontrarReglaPorConsumo(715)).thenReturn(Optional.of(regla));

        // Cuando
        EvaluarCompatibilidadResponse response = compatibilidadService.evaluar(request);

        // Entonces
        assertNotNull(response);
        assertTrue(response.isEsCompatible());
        assertTrue(response.getErrores().isEmpty());
    }

    @Test
    void testEvaluar_SocketMismatch_ShouldReturnIncompatible() {
        // Dado
        EvaluarCompatibilidadRequest request = new EvaluarCompatibilidadRequest();
        request.setIdsProductos(Arrays.asList(1L, 2L, 3L));

        ProductoEspecificacionDto cpuSpec = new ProductoEspecificacionDto();
        cpuSpec.setCategoria("CPU");
        cpuSpec.setNombreSocket("AM4");
        cpuSpec.setConsumoWatts(65);

        ProductoEspecificacionDto placaSpec = new ProductoEspecificacionDto();
        placaSpec.setCategoria("PLACA_MADRE");
        placaSpec.setNombreSocket("LGA1200");
        placaSpec.setConsumoWatts(50);

        ProductoEspecificacionDto fuenteSpec = new ProductoEspecificacionDto();
        fuenteSpec.setCategoria("FUENTE_PODER");
        fuenteSpec.setConsumoWatts(600);

        when(catalogoClient.obtenerEspecificaciones(1L)).thenReturn(cpuSpec);
        when(catalogoClient.obtenerEspecificaciones(2L)).thenReturn(placaSpec);
        when(catalogoClient.obtenerEspecificaciones(3L)).thenReturn(fuenteSpec);

        ReglaEnergia regla = new ReglaEnergia();
        regla.setConsumoWattsMin(100);
        regla.setConsumoWattsMax(200);
        regla.setFuenteRecomendadaWatts(500);
        when(energiaRepository.encontrarReglaPorConsumo(715)).thenReturn(Optional.of(regla));

        // Cuando
        EvaluarCompatibilidadResponse response = compatibilidadService.evaluar(request);

        // Entonces
        assertNotNull(response);
        assertFalse(response.isEsCompatible());
        assertEquals(1, response.getErrores().size());
        assertTrue(response.getErrores().get(0).contains("El CPU requiere socket AM4 pero la Placa Madre tiene LGA1200"));
    }

    @Test
    void testEvaluar_FuenteInsufficient_ShouldReturnIncompatible() {
        // Dado
        EvaluarCompatibilidadRequest request = new EvaluarCompatibilidadRequest();
        request.setIdsProductos(Arrays.asList(1L, 2L, 3L));

        ProductoEspecificacionDto cpuSpec = new ProductoEspecificacionDto();
        cpuSpec.setCategoria("CPU");
        cpuSpec.setNombreSocket("AM4");
        cpuSpec.setConsumoWatts(65);

        ProductoEspecificacionDto placaSpec = new ProductoEspecificacionDto();
        placaSpec.setCategoria("PLACA_MADRE");
        placaSpec.setNombreSocket("AM4");
        placaSpec.setConsumoWatts(50);

        ProductoEspecificacionDto fuenteSpec = new ProductoEspecificacionDto();
        fuenteSpec.setCategoria("FUENTE_PODER");
        fuenteSpec.setConsumoWatts(400); // Capacidad insuficiente (se recomendaban 500)

        when(catalogoClient.obtenerEspecificaciones(1L)).thenReturn(cpuSpec);
        when(catalogoClient.obtenerEspecificaciones(2L)).thenReturn(placaSpec);
        when(catalogoClient.obtenerEspecificaciones(3L)).thenReturn(fuenteSpec);

        ReglaEnergia regla = new ReglaEnergia();
        regla.setConsumoWattsMin(100);
        regla.setConsumoWattsMax(200);
        regla.setFuenteRecomendadaWatts(500);
        when(energiaRepository.encontrarReglaPorConsumo(515)).thenReturn(Optional.of(regla));

        // Cuando
        EvaluarCompatibilidadResponse response = compatibilidadService.evaluar(request);

        // Entonces
        assertNotNull(response);
        assertFalse(response.isEsCompatible());
        assertEquals(1, response.getErrores().size());
        assertTrue(response.getErrores().get(0).contains("La fuente de poder (400W) es insuficiente. Se recomiendan al menos 500W"));
    }

    @Test
    void testEvaluar_CatalogoClientFails_ShouldIgnoreAndBeCompatible() {
        // Dado
        EvaluarCompatibilidadRequest request = new EvaluarCompatibilidadRequest();
        request.setIdsProductos(Collections.singletonList(1L));

        // Simula fallo de conexión con catálogo externo
        when(catalogoClient.obtenerEspecificaciones(1L)).thenThrow(new RuntimeException("Connection error"));

        // Cuando
        EvaluarCompatibilidadResponse response = compatibilidadService.evaluar(request);

        // Entonces
        assertNotNull(response);
        assertTrue(response.isEsCompatible());
        assertTrue(response.getErrores().isEmpty());
    }

    @Test
    void testEvaluar_NoEnergyRuleFound_ShouldReturnIncompatibleWithEnergyRuleErrorMessage() {
        // Dado
        EvaluarCompatibilidadRequest request = new EvaluarCompatibilidadRequest();
        request.setIdsProductos(Arrays.asList(1L, 2L, 3L));

        ProductoEspecificacionDto cpuSpec = new ProductoEspecificacionDto();
        cpuSpec.setCategoria("CPU");
        cpuSpec.setNombreSocket("AM4");
        cpuSpec.setConsumoWatts(65);

        ProductoEspecificacionDto placaSpec = new ProductoEspecificacionDto();
        placaSpec.setCategoria("PLACA_MADRE");
        placaSpec.setNombreSocket("AM4");
        placaSpec.setConsumoWatts(50);

        ProductoEspecificacionDto fuenteSpec = new ProductoEspecificacionDto();
        fuenteSpec.setCategoria("FUENTE_PODER");
        fuenteSpec.setConsumoWatts(500);

        when(catalogoClient.obtenerEspecificaciones(1L)).thenReturn(cpuSpec);
        when(catalogoClient.obtenerEspecificaciones(2L)).thenReturn(placaSpec);
        when(catalogoClient.obtenerEspecificaciones(3L)).thenReturn(fuenteSpec);

        when(energiaRepository.encontrarReglaPorConsumo(615)).thenReturn(Optional.empty());

        // Cuando
        EvaluarCompatibilidadResponse response = compatibilidadService.evaluar(request);

        // Entonces
        assertNotNull(response);
        assertFalse(response.isEsCompatible());
        assertEquals(1, response.getErrores().size());
        assertTrue(response.getErrores().get(0).contains("No existe regla de energía"));
    }
}
