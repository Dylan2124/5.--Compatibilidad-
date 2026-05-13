package cl.duoc.plataforma.ms_compatibilidad.repository;

import cl.duoc.plataforma.ms_compatibilidad.model.ReglaSocket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReglaSocketRepository extends JpaRepository<ReglaSocket, Long> {
    
    // Busca reglas por el nombre exacto del socket (ej: 'AM4')
    List<ReglaSocket> findByNombreSocket(String nombreSocket);
    
    // Verifica si existe compatibilidad entre tipo componente y socket
    boolean existsByTipoComponenteAndNombreSocket(String tipoComponente, String nombreSocket);
}
