package cl.duoc.plataforma.ms_compatibilidad.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReglaEnergiaDto {

    private Long id;

    @NotNull(message = "El consumo mínimo es obligatorio")
    @Min(value = 0, message = "El consumo no puede ser negativo")
    private Integer consumoWattsMin;

    @NotNull(message = "El consumo máximo es obligatorio")
    @Min(value = 1, message = "El consumo máximo debe ser mayor a 0")
    private Integer consumoWattsMax;

    @NotNull(message = "La fuente recomendada es obligatoria")
    @Min(value = 1, message = "La fuente recomendada debe ser mayor a 0")
    private Integer fuenteRecomendadaWatts;
}
