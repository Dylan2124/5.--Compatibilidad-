# Pruebas de Conexión (OpenFeign) - MS Compatibilidad

Este documento detalla cómo probar la integración del motor de reglas (`ms-compatibilidad`) con otros microservicios mediante Postman. 

---

## 1. Consultar Especificaciones al Catálogo (Al evaluar)

**Objetivo:** Validar que antes de aplicar las reglas de energía y socket locales, el motor de reglas consulta a `ms-catalogo` para obtener los detalles técnicos de cada componente.

*   **Método:** `POST`
*   **URL:** `http://localhost:8085/api/compatibilidad/evaluar`
*   **Body (JSON):**
    ```json
    {
      "idsProductos": [10, 15, 22]
    }
    ```

### Resultados Esperados en Consola de MS-COMPATIBILIDAD:

Si el microservicio de Catálogo (puerto 8082) está **ENCENDIDO**:
> El sistema obtendrá los datos (Socket, Consumo Watts) de cada ID para hacer las comparaciones pertinentes. Postman devolverá la lista de errores real si los componentes no coinciden.

Si el microservicio de Catálogo está **APAGADO** (Demostración de Tolerancia a Fallos):
> *"Iniciando evaluación de compatibilidad para 3 productos"*
> *"ATENCION: No se pudo obtener specs de ms-catalogo para ID 10. Simulando para no detener evaluación."*
> *"ATENCION: No se pudo obtener specs de ms-catalogo para ID 15. Simulando para no detener evaluación."*

En caso de estar apagado, Postman devolverá un `200 OK` con `"esCompatible": true`, asumiendo que no hay reglas que violar al no tener la información externa.
