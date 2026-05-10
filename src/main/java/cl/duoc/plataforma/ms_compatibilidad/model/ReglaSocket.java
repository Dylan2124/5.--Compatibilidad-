package cl.duoc.plataforma.ms_compatibilidad.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ═══════════════════════════════════════════════════
 * CLASE: ReglaSocket.java
 * Diccionario de reglas que indica qué sockets son compatibles
 * con ciertas generaciones de componentes.
 * ═══════════════════════════════════════════════════
 */
@Entity
@Table(name = "reglas_socket")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReglaSocket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String tipoComponente; // Ej: CPU, PLACA_MADRE

    @Column(nullable = false, length = 50)
    private String nombreSocket; // Ej: AM4, LGA1700

    @Column(nullable = false, length = 100)
    private String generacionSoportada; // Ej: Ryzen Serie 5000
}
