package cl.duoc.plataforma.ms_compatibilidad.repository;

import cl.duoc.plataforma.ms_compatibilidad.model.ReglaEnergia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReglaEnergiaRepository extends JpaRepository<ReglaEnergia, Long> {
    
    // Encuentra la regla de energía correcta basada en el consumo total estimado.
    // La consulta busca donde el consumo total esté entre el mínimo y máximo de la regla.
    @Query("SELECT r FROM ReglaEnergia r WHERE :consumoTotal >= r.consumoWattsMin AND :consumoTotal <= r.consumoWattsMax")
    Optional<ReglaEnergia> encontrarReglaPorConsumo(@Param("consumoTotal") Integer consumoTotal);
}
