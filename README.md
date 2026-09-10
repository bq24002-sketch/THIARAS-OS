<h1 align="center">THIARAS OS</h1>

<p align="center">
  <strong>Sistema personal de gestión de actividades, progreso, objetivos, logros y notificaciones.</strong>
</p>


<p align="center">
  <img src="https://img.shields.io/badge/Java-25-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" height="42">
  <img src="https://img.shields.io/badge/PostgreSQL-17-4169E1?style=for-the-badge&logo=postgresql&logoColor=white" height="42">
  <img src="https://img.shields.io/badge/PL%2FpgSQL-PostgreSQL-4169E1?style=for-the-badge&logo=postgresql&logoColor=white" height="42">
  <img src="https://img.shields.io/badge/Maven-Build-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white" height="42">
  <img src="https://img.shields.io/badge/Git-Version_Control-F05032?style=for-the-badge&logo=git&logoColor=white" height="42">
  <img src="https://img.shields.io/badge/GitHub-Repository-181717?style=for-the-badge&logo=github&logoColor=white" height="42">
  <img src="https://img.shields.io/badge/CSS-Style-1572B6?style=for-the-badge&logo=css3&logoColor=white" height="42">
</p>



THIARAS OS es un proyecto de software orientado a la organización, seguimiento y análisis de actividades personales. El sistema utiliza PostgreSQL como núcleo de persistencia y lógica de negocio, con Java como capa de aplicación e interfaz.

---

## Estado del proyecto

Actualmente se encuentra implementada y probada la capa principal de base de datos.

### Funcionalidades implementadas

- Gestión de actividades.
- Registro del cumplimiento de actividades.
- Inicio de actividades.
- Finalización de actividades.
- Cálculo automático de minutos realizados.
- Control de estados de actividad.
- Estadísticas diarias.
- Estadísticas semanales.
- Estadísticas acumuladas.
- Objetivos personales.
- Objetivos por minutos.
- Objetivos por cantidad de actividades.
- Períodos diarios, semanales, mensuales y personalizados.
- Evaluación automática de objetivos.
- Sistema de logros.
- Registro de logros obtenidos.
- Notificaciones de logros.
- Marcado de notificaciones como leídas.
- Consulta del estado actual de una actividad.
- Consulta de actividades preparada para la interfaz.
- Dashboard de progreso.

---

# Arquitectura

La arquitectura de THIARAS OS separa la persistencia, la lógica de negocio y la aplicación.

```text
                    THIARAS OS
                        │
             ┌──────────┴──────────┐
             │                     │
          JAVA                 POSTGRESQL
             │                     │
       Aplicación/UI        Persistencia
             │              Lógica de negocio
             │                     │
             └──────────┬──────────┘
                        │
                 Operaciones SQL

                ACTIVIDAD
                    │
                    ▼
             INICIAR ACTIVIDAD
                    │
                    ▼
               EN_PROGRESO
                    │
                    ▼
            COMPLETAR ACTIVIDAD
                    │
                    ▼
                COMPLETADA
                    │
                    ▼
          PROCESAR CUMPLIMIENTO
                    │
          ┌─────────┼─────────┐
          ▼         ▼         ▼
      OBJETIVOS   LOGROS   NOTIFICACIONES


Base de datos

THIARAS OS utiliza PostgreSQL y PL/pgSQL para implementar la persistencia y parte importante de la lógica de negocio.

Entre las operaciones principales se encuentran:

Entre las operaciones principales se encuentran:

SELECT iniciar_actividad(?);
SELECT completar_actividad(?);
SELECT procesar_cumplimiento(?);
SELECT marcar_notificacion_leida(?);
SELECT * FROM estado_actividad(?);

Estas funciones permiten que la futura aplicación Java delegue las operaciones de negocio en PostgreSQL en lugar de duplicar la lógica en la aplicación.

Flujo de cumplimiento

Cuando una actividad comienza:

PENDIENTE
    │
    │ iniciar_actividad()
    ▼
EN_PROGRESO

Cuando finaliza:

EN_PROGRESO
    │
    │ completar_actividad()
    ▼
COMPLETADA

Al completarse, el sistema puede:

COMPLETADA
    │
    ▼
procesar_cumplimiento()
    │
    ├── Actualizar progreso
    │
    ├── Evaluar objetivos
    │
    ├── Evaluar logros
    │
    └── Generar notificaciones

El tiempo realizado se calcula a partir de:

momento_fin - momento_inicio

y se almacena en minutos_realizados.

Objetivos

THIARAS OS permite establecer objetivos medibles.

Las métricas disponibles son:

MINUTOS
ACTIVIDADES

Los períodos disponibles son:

DIARIO
SEMANAL
MENSUAL
PERSONALIZADO

Ejemplo:

Objetivo:
Estudiar 120 minutos esta semana

Métrica:
MINUTOS

Meta:
120

Progreso:
17 / 120

Porcentaje:
14.17%

También es posible definir objetivos basados en cantidad de actividades:

Objetivo:
Completar 3 actividades esta semana

Métrica:
ACTIVIDADES

Meta:
3

Progreso:
3 / 3

Porcentaje:
100%
Sistema de logros

El sistema incorpora un mecanismo de logros basado en métricas y metas.

Actualmente se contemplan logros como:

Código	Nombre	Métrica	Meta
PRIMERA_ACTIVIDAD	Primera actividad	ACTIVIDADES	1
CINCO_ACTIVIDADES	Cinco actividades	ACTIVIDADES	5
DIEZ_ACTIVIDADES	Diez actividades	ACTIVIDADES	10
SESENTA_MINUTOS	Primera hora	MINUTOS	60
CINCO_HORAS	Cinco horas	MINUTOS	300
DIEZ_HORAS	Diez horas	MINUTOS	600

Cuando se alcanza una meta, el sistema registra el logro en logro_obtenido.

Notificaciones

Los logros obtenidos pueden generar notificaciones.

Ejemplo:

🎉 Primera actividad

¡Felicidades! Has desbloqueado el logro
"Primera actividad".

Las notificaciones cuentan con estado de lectura:

leida = FALSE

Al ser leídas:

leida = TRUE
leida_en = CURRENT_TIMESTAMP

La operación está encapsulada mediante:

SELECT marcar_notificacion_leida(?);
Dashboard

El sistema dispone de una consulta consolidada para obtener información relevante del estado general.

El dashboard contempla:

ACTIVIDADES
├── Completadas hoy
├── Minutos realizados hoy
├── Completadas esta semana
└── Minutos realizados esta semana

OBJETIVOS
├── Meta
├── Realizado
├── Restante
└── Porcentaje

LOGROS
├── Disponibles
├── Desbloqueados
└── Valor alcanzado

NOTIFICACIONES
└── Pendientes de lectura

La consulta principal es:

data/database/queries/31_dashboard.sql
Estructura del proyecto
THIARAS-OS/
│
├── data/
│   └── database/
│       ├── ddl/
│       │
│       └── queries/
│
├── src/
│   └── ...
│
├── pom.xml
├── estructura.txt
├── README.md
├── .gitignore
└── LICENSE
data/database/ddl/

Contiene la definición de la estructura de PostgreSQL y las funciones de negocio.

Entre ellas:

iniciar_actividad()
completar_actividad()
procesar_cumplimiento()
marcar_notificacion_leida()
estado_actividad()
data/database/queries/

Contiene las consultas SQL utilizadas para operar, consultar y validar las funcionalidades del sistema.

Entre ellas:

Actividades del día
Resumen de progreso
Progreso de objetivos
Evaluación de logros
Notificaciones
Dashboard
Actividades para interfaz
src/

Contiene la aplicación Java.

La siguiente fase consiste en integrar esta capa con las funciones y consultas PostgreSQL existentes.

pom.xml

Archivo de configuración de Maven utilizado para administrar el proyecto Java y sus dependencias.

Tecnologías
Tecnología	Uso
Java	Aplicación
PostgreSQL	Base de datos
PL/pgSQL	Lógica de negocio en BD
Maven	Gestión del proyecto Java
Git	Control de versiones
GitHub	Repositorio
Integración Java + PostgreSQL

La aplicación Java deberá utilizar PostgreSQL como fuente central de persistencia y lógica de negocio.

El objetivo es evitar duplicación de reglas entre Java y SQL.

Por ejemplo:

Usuario
   │
   ▼
Interfaz Java
   │
   ▼
DAO / Repository
   │
   ▼
PostgreSQL
   │
   ▼
iniciar_actividad()
   │
   ▼
EN_PROGRESO

Y posteriormente:

Usuario
   │
   ▼
Completar
   │
   ▼
Java
   │
   ▼
completar_actividad()
   │
   ▼
COMPLETADA
   │
   ▼
procesar_cumplimiento()
   ├── Objetivos
   ├── Logros
   └── Notificaciones
Próxima fase

La siguiente etapa del proyecto consiste en completar la integración entre Java y PostgreSQL.

Se busca que la aplicación permita:

Visualizar las actividades.
Consultar el estado de cada actividad.
Iniciar una actividad.
Mostrar actividades en progreso.
Completar una actividad.
Actualizar automáticamente las estadísticas.
Mostrar el progreso de los objetivos.
Mostrar los logros obtenidos.
Mostrar las notificaciones.
Marcar notificaciones como leídas.
Actualizar el dashboard después de las operaciones.
Principios de desarrollo

THIARAS OS se desarrolla de forma incremental.

Cada componente se prueba individualmente antes de integrarlo con las capas superiores.

La separación buscada es:

┌──────────────────────┐
│      INTERFAZ        │
├──────────────────────┤
│      APLICACION      │
├──────────────────────┤
│   LOGICA DE NEGOCIO  │
├──────────────────────┤
│     PERSISTENCIA     │
└──────────────────────┘

Esto permite mantener el sistema organizado y facilita futuras modificaciones, pruebas y ampliaciones.

Estado actual
[████████████████████░░░░] Base de datos

[██████████░░░░░░░░░░░░░░] Aplicación Java

[░░░░░░░░░░░░░░░░░░░░░░░░] Interfaz final
PostgreSQL

Funcional y validado.

Java

En proceso de integración.

Interfaz

Próxima fase de desarrollo.

Licencia

Consulta el archivo LICENSE incluido en el repositorio para conocer los términos de uso del proyecto.

<<<<<<< HEAD
=======
La aplicacion inicia en modo escritorio con `mvn javafx:run`. Incluye dashboard, acciones de iniciar y completar, notificaciones pendientes y formulario de nueva actividad. El motor de correos se inicia aparte con `--recordatorios` y consulta PostgreSQL en cada ciclo; `horario.json` ya no es la fuente de recordatorios en ejecucion.
>>>>>>> e588f61 (adjunto lógica de integración)
