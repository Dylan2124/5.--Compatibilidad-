# Pruebas para Postman: Microservicio de Compatibilidad

Aquí tienes las peticiones listas para probar el motor de reglas en **ms-compatibilidad** (Puerto 8083).

### 1. Consultar si un CPU es compatible con una Placa Madre (Socket AM4)
- **Método:** `GET`
- **URL:** `http://localhost:8083/api/compatibilidad/validar-socket?tipo=CPU&socket=AM4`
- **Body:** *(No requiere)*

### 2. Consultar si un CPU antiguo es compatible con una placa moderna (Debería dar falso)
- **Método:** `GET`
- **URL:** `http://localhost:8083/api/compatibilidad/validar-socket?tipo=CPU&socket=LGA1151`
- **Body:** *(No requiere)*

### 3. Consultar fuente de poder mínima recomendada para un consumo de 450 Watts
- **Método:** `GET`
- **URL:** `http://localhost:8083/api/compatibilidad/fuente-recomendada?watts=450`
- **Body:** *(No requiere)*
