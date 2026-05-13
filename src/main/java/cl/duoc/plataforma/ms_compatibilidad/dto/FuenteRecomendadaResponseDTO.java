package cl.duoc.plataforma.ms_compatibilidad.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * FuenteRecomendadaResponseDTO: DTO de SALIDA.
 * El servidor lo construye y lo envía al cliente.
 * NO tiene anotaciones de validación (@NotNull, @Min, etc.)
 * porque el servidor lo arma, no el cliente.
 * */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FuenteRecomendadaResponseDTO {
    // Rango de la regla de energía que aplica
    private Integer consumoWattsMin;
    private Integer consumoWattsMax;
    // Resultado: la fuente de poder que se recomienda
    private Integer fuenteRecomendadaWatts;
}
