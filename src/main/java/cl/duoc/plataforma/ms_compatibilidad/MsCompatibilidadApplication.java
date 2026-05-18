package cl.duoc.plataforma.ms_compatibilidad;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MsCompatibilidadApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsCompatibilidadApplication.class, args);
	}

}
