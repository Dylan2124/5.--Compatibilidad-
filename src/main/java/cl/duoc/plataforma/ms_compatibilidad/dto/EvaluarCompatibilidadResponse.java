package cl.duoc.plataforma.ms_compatibilidad.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class EvaluarCompatibilidadResponse {
    private boolean esCompatible;
    private List<String> errores;
}
