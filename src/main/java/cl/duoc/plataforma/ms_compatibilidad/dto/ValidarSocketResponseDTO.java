package cl.duoc.plataforma.ms_compatibilidad.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * ValidarSocketResponseDTO: DTO de SALIDA.
 * El servidor lo construye y lo envía al cliente.
 * NO tiene anotaciones de validación (@NotBlank, etc.)
 * porque el servidor lo arma, no el cliente.
 * */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidarSocketResponseDTO {
    private String tipoComponente;
    private String nombreSocket;
    // Resultado de la validación del motor de reglas
    private boolean compatible;
}
