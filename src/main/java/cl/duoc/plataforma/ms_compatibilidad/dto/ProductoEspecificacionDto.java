package cl.duoc.plataforma.ms_compatibilidad.dto;

import lombok.Data;

@Data
public class ProductoEspecificacionDto {
    private Long idProducto;
    private String categoria; // Ej: "CPU", "PLACA_MADRE", "FUENTE_PODER"
    private String nombreSocket; // Ej: "AM4"
    private Integer consumoWatts; // Ej: 65
}
