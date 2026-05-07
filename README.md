# Microservicio: Compatibilidad

## 1. Descripción del Microservicio
Este microservicio funciona como un "motor de reglas" de negocio. Su objetivo principal es evaluar y validar técnicamente que las piezas de hardware seleccionadas por el usuario (por ejemplo, en el carrito de compras) sean compatibles entre sí. 

**Rol dentro del Sistema:**
Garantiza que ensamblajes propuestos sean viables (ej: validando que un procesador Ryzen específico se ajuste al socket de la placa madre elegida, o que la fuente de poder tenga la capacidad necesaria). A diferencia de otros microservicios, no guarda datos transaccionales de clientes, sino reglas maestras técnicas.

---

## 2. Base de Datos: `db_compatibilidad`

De acuerdo con la rúbrica del proyecto, este microservicio posee su propia base de datos independiente. Las tablas de esta base de datos funcionan como diccionarios de reglas independientes y no necesitan estar relacionadas entre sí.

### 2.1 Tablas y Estructura

#### Tabla: `reglas_socket`
Almacena las reglas de compatibilidad entre procesadores y placas madre según su socket.

| Atributo | Tipo de Dato | Descripción |
| :--- | :--- | :--- |
| `id_regla` | Primary Key | Identificador único y autoincremental de la regla. |
| `tipo_componente`| Varchar | El tipo de hardware (Ej: CPU, PLACA_MADRE). |
| `nombre_socket` | Varchar | El nombre técnico del socket (Ej: AM4, LGA1700). |
| `generacion_soportada`| Varchar | Especificación de la generación (Ej: Ryzen Serie 5000). |

#### Tabla: `reglas_energia`
Almacena las reglas de consumo eléctrico para validar la capacidad de la fuente de poder.

| Atributo | Tipo de Dato | Descripción |
| :--- | :--- | :--- |
| `id_regla` | Primary Key | Identificador único y autoincremental de la regla. |
| `consumo_watts_min`| Integer | Consumo mínimo en watts del componente o configuración. |
| `consumo_watts_max`| Integer | Consumo máximo en watts del componente o configuración. |
| `fuente_recomendada_watts`| Integer | Capacidad recomendada para la fuente de poder (en watts). |

### 2.2 Relaciones Internas
* **Sin relaciones explícitas:** Las tablas son diccionarios de reglas independientes que el código Java leerá para tomar decisiones, no requieren relaciones (Foreign Keys) entre sí.

---

## 3. Tecnologías
* **Lenguaje:** Java 21
* **Framework:** Spring Boot (Spring Web, Spring Data JPA, Validation)
* **Base de Datos:** MySQL
* **Otros:** Lombok
