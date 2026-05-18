package cl.duoc.plataforma.ms_compatibilidad.dto;

import lombok.Data;
import java.util.List;

@Data
public class EvaluarCompatibilidadRequest {
    private List<Long> idsProductos;
}
