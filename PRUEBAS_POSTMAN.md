# 🧪 Guía de Pruebas de API y Control de Errores: Microservicio de Compatibilidad (`ms-compatibilidad`)

Esta guía detalla la suite completa de pruebas para validar el correcto funcionamiento, las reglas de negocio y el control de excepciones en el microservicio **ms-compatibilidad** (Puerto `8085`).

---

## ⚙️ Configuración del Entorno de Pruebas
* **Base URL:** `http://localhost:8085`
* **Content-Type:** `application/json`
* **Base de Datos:** MySQL (`db_compatibilidad` en puerto `3306`)

---

## 🎯 Resumen de Validaciones de Negocio (Bean Validation) y Control de Recursos
El microservicio utiliza **Jakarta Bean Validation** (JSR 380) para asegurar la integridad de los datos de entrada a nivel de controlador y captura centralizada en **`GlobalExceptionHandler`** para las reglas de negocio y parámetros requeridos:

| Campo / Operación | Restricción / Estructura | Código HTTP Esperado | Mensaje de Error / Comportamiento |
| :--- | :--- | :--- | :--- |
| `tipo` (Validar Socket) | `@NotBlank` | `400 Bad Request` | `"El tipo de componente no puede estar vacío"` |
| `socket` (Validar Socket) | `@NotBlank` | `400 Bad Request` | `"El nombre del socket no puede estar vacío"` |
| `watts` (Fuente Recomendada) | `@Min(1)` | `400 Bad Request` | `"El consumo en watts debe ser mayor a 0"` |
| **Falta de parámetro en URL** | `MissingServletRequestParameterException` | `400 Bad Request` | JSON conteniendo el detalle `"Falta el parámetro requerido: '...'"` |
| **Watts sin regla asociada** | `RuntimeException` (Service) | `400 Bad Request` | JSON conteniendo el detalle `"No existe regla de energía para un consumo de: ... W. Rango soportado: 0 a 2000 W."` |

---

# 🟢 Escenarios Exitosos (Happy Paths)
Pruebas diseñadas para comprobar que el flujo operativo estándar de la aplicación funciona correctamente.

### 1. Validar Socket Compatible para un CPU (AM4)
Valida que el motor de reglas encuentre compatibilidad exitosa en la base de datos y retorne `compatible: true`.

* **Método:** `GET`
* **URL:** `http://localhost:8085/api/compatibilidad/validar-socket?tipo=CPU&socket=AM4`
* **Body:** *(No requiere)*
* **Respuesta Esperada (HTTP 200 OK):**
  ```json
  {
    "tipoComponente": "CPU",
    "nombreSocket": "AM4",
    "compatible": true
  }
  ```

### 2. Validar Socket Compatible para una Placa Madre (LGA1700)
Valida la compatibilidad de otro tipo de componente en la base de datos, en este caso para socket LGA1700.

* **Método:** `GET`
* **URL:** `http://localhost:8085/api/compatibilidad/validar-socket?tipo=PLACA_MADRE&socket=LGA1700`
* **Body:** *(No requiere)*
* **Respuesta Esperada (HTTP 200 OK):**
  ```json
  {
    "tipoComponente": "PLACA_MADRE",
    "nombreSocket": "LGA1700",
    "compatible": true
  }
  ```

### 3. Consultar Fuente de Poder Recomendada para un Consumo de 450 Watts
Valida que el motor de energía determine el rango aplicable (entre 301 y 500 Watts) y sugiera una fuente recomendada de 750W.

* **Método:** `GET`
* **URL:** `http://localhost:8085/api/compatibilidad/fuente-recomendada?watts=450`
* **Body:** *(No requiere)*
* **Respuesta Esperada (HTTP 200 OK):**
  ```json
  {
    "consumoWattsMin": 301,
    "consumoWattsMax": 500,
    "fuenteRecomendadaWatts": 750
  }
  ```

### 4. Consultar Fuente de Poder Recomendada para un Consumo de 150 Watts
Valida la consulta de potencia para rangos de bajo consumo (entre 0 y 300 Watts) y sugiera una fuente recomendada de 500W.

* **Método:** `GET`
* **URL:** `http://localhost:8085/api/compatibilidad/fuente-recomendada?watts=150`
* **Body:** *(No requiere)*
* **Respuesta Esperada (HTTP 200 OK):**
  ```json
  {
    "consumoWattsMin": 0,
    "consumoWattsMax": 300,
    "fuenteRecomendadaWatts": 500
  }
  ```

---

# 🔴 Escenarios de Error: Validaciones de Datos (HTTP 400 Bad Request)
Casos diseñados para forzar los límites de las restricciones `@NotBlank` y `@Min(1)` aplicadas directamente en los parámetros de entrada del controlador.

### 5. Error: Validar Socket con Tipo de Componente Vacío (`tipo` en blanco)
* **Método:** `GET`
* **URL:** `http://localhost:8085/api/compatibilidad/validar-socket?tipo=&socket=AM4`
* **Body:** *(No requiere)*
* **Respuesta de Error Esperada (HTTP 400 Bad Request):**
  ```json
  {
    "error": "El tipo de componente no puede estar vacío"
  }
  ```

### 6. Error: Validar Socket con Nombre de Socket Vacío (`socket` en blanco)
* **Método:** `GET`
* **URL:** `http://localhost:8085/api/compatibilidad/validar-socket?tipo=CPU&socket=`
* **Body:** *(No requiere)*
* **Respuesta de Error Esperada (HTTP 400 Bad Request):**
  ```json
  {
    "error": "El nombre del socket no puede estar vacío"
  }
  ```

### 7. Error: Enviar Consumo Cero en Watts (`watts=0`)
* **Método:** `GET`
* **URL:** `http://localhost:8085/api/compatibilidad/fuente-recomendada?watts=0`
* **Body:** *(No requiere)*
* **Respuesta de Error Esperada (HTTP 400 Bad Request):**
  ```json
  {
    "error": "El consumo en watts debe ser mayor a 0"
  }
  ```

### 8. Error: Enviar Consumo Negativo en Watts (`watts=-100`)
* **Método:** `GET`
* **URL:** `http://localhost:8085/api/compatibilidad/fuente-recomendada?watts=-100`
* **Body:** *(No requiere)*
* **Respuesta de Error Esperada (HTTP 400 Bad Request):**
  ```json
  {
    "error": "El consumo en watts debe ser mayor a 0"
  }
  ```

### 9. Error de Validación Múltiple Combinado (Ambos Parámetros de Socket Vacíos)
Prueba la capacidad de acumular errores de validación de parámetros en la respuesta del `GlobalExceptionHandler`.

* **Método:** `GET`
* **URL:** `http://localhost:8085/api/compatibilidad/validar-socket?tipo=&socket=`
* **Body:** *(No requiere)*
* **Respuesta de Error Esperada (HTTP 400 Bad Request):**
  ```json
  {
    "error": "El tipo de componente no puede estar vacío, El nombre del socket no puede estar vacío"
  }
  ```

---

# 🔴 Escenarios de Error: Parámetros URL Faltantes (HTTP 400 Bad Request)
Casos diseñados para comprobar que el microservicio responde de forma clara y homogénea cuando un parámetro obligatorio no es enviado en la solicitud.

### 10. Error: Falta el parámetro `tipo` en Validar Socket
* **Método:** `GET`
* **URL:** `http://localhost:8085/api/compatibilidad/validar-socket?socket=AM4`
* **Body:** *(No requiere)*
* **Respuesta de Error Esperada (HTTP 400 Bad Request):**
  ```json
  {
    "error": "Falta el parámetro requerido: 'tipo'"
  }
  ```

### 11. Error: Falta el parámetro `socket` en Validar Socket
* **Método:** `GET`
* **URL:** `http://localhost:8085/api/compatibilidad/validar-socket?tipo=CPU`
* **Body:** *(No requiere)*
* **Respuesta de Error Esperada (HTTP 400 Bad Request):**
  ```json
  {
    "error": "Falta el parámetro requerido: 'socket'"
  }
  ```

### 12. Error: Falta el parámetro `watts` en Fuente Recomendada
* **Método:** `GET`
* **URL:** `http://localhost:8085/api/compatibilidad/fuente-recomendada`
* **Body:** *(No requiere)*
* **Respuesta de Error Esperada (HTTP 400 Bad Request):**
  ```json
  {
    "error": "Falta el parámetro requerido: 'watts'"
  }
  ```

---

# 🔴 Escenarios de Error: Reglas de Negocio No Cubiertas (HTTP 400 / 200 Incompatible)
Casos diseñados para validar las reglas lógicas internas del motor de compatibilidad.

### 13. Error: Consumo de Watts Fuera de Rango (2500 Watts)
Valida que si no se encuentra ninguna regla de rango energético en la BD para un consumo exagerado, la API retorne un error ordenado de negocio en vez de un fallo del sistema.

* **Método:** `GET`
* **URL:** `http://localhost:8085/api/compatibilidad/fuente-recomendada?watts=2500`
* **Body:** *(No requiere)*
* **Respuesta de Error Esperada (HTTP 400 Bad Request):**
  ```json
  {
    "error": "No existe regla de energía para un consumo de: 2500 W. Rango soportado: 0 a 2000 W."
  }
  ```

### 14. Validar Socket Inexistente o No Compatible (Respuesta Negativa Exitosa)
Valida que si la combinación de tipo de componente y socket no está registrada en el diccionario de compatibilidad, se retorne `compatible: false` en un código de estado `200 OK` normal (ya que la consulta se ejecutó de forma correcta).

* **Método:** `GET`
* **URL:** `http://localhost:8085/api/compatibilidad/validar-socket?tipo=CPU&socket=LGA1151`
* **Body:** *(No requiere)*
* **Respuesta Esperada (HTTP 200 OK):**
  ```json
  {
    "tipoComponente": "CPU",
    "nombreSocket": "LGA1151",
    "compatible": false
  }
  ```
