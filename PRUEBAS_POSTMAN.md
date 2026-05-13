# Pruebas para Postman: Microservicio de Compatibilidad

Aquí tienes las peticiones listas para probar el motor de reglas en **ms-compatibilidad** (Puerto 8083).

### 1. Validar compatibilidad de CPU con socket AM4 (Debería dar VERDADERO)
- **Método:** `GET`
- **URL:** `http://localhost:8083/api/compatibilidad/validar-socket?tipo=CPU&socket=AM4`
- **Body:** *(No requiere)*
- **Respuesta esperada:**
```json
{
  "tipoComponente": "CPU",
  "nombreSocket": "AM4",
  "compatible": true
}
```

### 2. Validar compatibilidad de CPU con socket inexistente (Debería dar FALSO)
- **Método:** `GET`
- **URL:** `http://localhost:8083/api/compatibilidad/validar-socket?tipo=CPU&socket=LGA1151`
- **Body:** *(No requiere)*
- **Respuesta esperada:**
```json
{
  "tipoComponente": "CPU",
  "nombreSocket": "LGA1151",
  "compatible": false
}
```

### 3. Consultar fuente de poder recomendada para un consumo de 450 Watts
- **Método:** `GET`
- **URL:** `http://localhost:8083/api/compatibilidad/fuente-recomendada?watts=450`
- **Body:** *(No requiere)*
- **Respuesta esperada:**
```json
{
  "consumoEstimadoWatts": 450,
  "fuenteRecomendadaWatts": 750
}
```

### 4. Consultar fuente para un consumo fuera del rango (Debería dar Error 500)
- **Método:** `GET`
- **URL:** `http://localhost:8083/api/compatibilidad/fuente-recomendada?watts=2500`
- **Body:** *(No requiere)*
- **Respuesta esperada:**
```json
{
  "status": 500,
  "error": "No existe regla de energía para un consumo tan alto o bajo: 2500"
}
```
