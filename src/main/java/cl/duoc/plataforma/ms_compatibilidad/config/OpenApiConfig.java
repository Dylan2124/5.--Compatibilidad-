package cl.duoc.plataforma.ms_compatibilidad.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de Swagger/OpenAPI para el microservicio de Compatibilidad.
 * Esta clase expone la documentación de la API a través de Swagger UI.
 */
@Configuration
public class OpenApiConfig {

    /**
     * Agrupa todos los endpoints bajo el nombre "public".
     * Puedes personalizar los patrones de rutas según sea necesario.
     */
    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("public")
                .pathsToMatch("/**")
                .build();
    }
}
