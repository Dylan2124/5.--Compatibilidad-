# Pruebas para Postman: Microservicio de Compatibilidad

*(NOTA: Los endpoints para consultar este diccionario se implementarán en la Fase 2 del desarrollo).*

Por ahora, este microservicio actúa como una base de datos estática que será consultada por el sistema. Una vez que se implemente el Controlador (`CompatibilidadController`), aquí tendrás los JSON para probar:

### 1. Consultar si un CPU es compatible con una Placa Madre
- **Método:** `POST`
- **URL:** `http://localhost:8083/api/compatibilidad/validar-socket`
- **Body JSON:** *(Se agregará en la Fase 2)*

### 2. Consultar fuente de poder mínima recomendada
- **Método:** `POST`
- **URL:** `http://localhost:8083/api/compatibilidad/validar-energia`
- **Body JSON:** *(Se agregará en la Fase 2)*
