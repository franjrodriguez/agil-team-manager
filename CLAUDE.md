# Información del Proyecto: AgileTeam Manager

## Contexto del Desarrollador
- **Nombre:** Fran (Desarrollador Senior Backend, enfoque en sistemas robustos).
- **Estilo de aprendizaje:** Explicaciones visuales, esquemas y resúmenes de puntos clave (TDAH). Evitar párrafos excesivamente largos.

## Stack Tecnológico
- **Lenguaje:** Java 21
- **Gestor de dependencias:** Maven (pom.xml)
- **Framework:** Spring Boot / Java SE / JavaFX
- **Base de Datos:** PostgreSQL

## Reglas de Código (Guía de Estilo)
- Preferir código limpio y legible sobre "trucos" modernos complejos.
- Usar nombres de variables en castellano/inglés [Tú eliges].
- **Arquitectura:** Capas (Controller/Service/Repository)
- **Manejo de errores:** Usar excepciones personalizadas y evitar bloques try-catch vacíos.

## Comandos Frecuentes
> Maven no está instalado globalmente. Usar siempre el wrapper del proyecto.
- **Compilar:** `./mvnw clean compile`
- **Ejecutar tests:** `./mvnw test`
- **Limpiar proyecto:** `./mvnw clean`

## Notas Importantes para Claude
- **Antes de modificar el `pom.xml`**, explícame por qué es necesaria la nueva dependencia.
- Si encuentras código que parezca "basura" o restos de pruebas anteriores, avísame para borrarlo.
- No inventes librerías; asegura que las versiones existen en Maven Central.
- Ten en cuenta que estoy aprendiendo, así que genera un código legible, comprensible y ve documentando.