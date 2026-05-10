package cl.duoc.plataforma.ms_compatibilidad.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReglaSocketDto {

    private Long id;

    @NotEmpty(message = "El tipo de componente es obligatorio")
    private String tipoComponente;

    @NotEmpty(message = "El nombre del socket es obligatorio")
    private String nombreSocket;

    @NotEmpty(message = "La generacion soportada es obligatoria")
    private String generacionSoportada;
}
