package cl.duoc.plataforma.ms_compatibilidad.client;

import cl.duoc.plataforma.ms_compatibilidad.dto.ProductoEspecificacionDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-catalogo", url = "http://localhost:8082/api/productos")
public interface CatalogoClient {

    @GetMapping("/{id}/especificaciones")
    ProductoEspecificacionDto obtenerEspecificaciones(@PathVariable("id") Long idProducto);
}
