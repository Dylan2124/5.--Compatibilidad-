package cl.duoc.plataforma.ms_compatibilidad.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ═══════════════════════════════════════════════════
 * CLASE: ReglaEnergia.java
 * Diccionario que permite calcular si una fuente de poder
 * será suficiente para los componentes seleccionados.
 * ═══════════════════════════════════════════════════
 */
@Entity
@Table(name = "reglas_energia")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReglaEnergia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer consumoWattsMin;

    @Column(nullable = false)
    private Integer consumoWattsMax;

    @Column(nullable = false)
    private Integer fuenteRecomendadaWatts;
}
