# 💈 Madhouse Mobile

Madhouse Mobile es la aplicación cliente-servidor nativa para Android diseñada para gestionar de forma integral las reservas y la administración de una barbería moderna. 
Permite a los clientes agendar citas fácilmente y a los barberos gestionar su flujo de trabajo diario.

Esta aplicación forma parte de un ecosistema Monorepo que se conecta mediante una API RESTful a un backend desarrollado en Spring Boot.

## ✨ Características Principales

### 👤 Para Clientes
* **Dashboard Personalizado:** Vista rápida de la próxima cita, puntos acumulados y acceso directo a los servicios.
* **Wizard de Reservas (5 Pasos):** Flujo intuitivo para agendar citas seleccionando:
  1. Servicio deseado.
  2. Barbero de preferencia.
  3. Fecha y hora (integrado con el calendario nativo).
  4. Método de pago.
  5. Confirmación final.
* **Gestión de Citas:** Historial completo de reservas y opción de cancelación con actualización en tiempo real.
* **Fidelización:** Visualización de puntos acumulados por servicios adquiridos.
* **Gestión de Perfil:** Visualización y edición de datos personales, incluyendo foto de perfil decodificada en Base64.

### ✂️ Para Barberos
* **Agenda Dinámica:** Control de los servicios asignados para el día.
* **Gestión de Estados:** Capacidad de marcar citas como "COMPLETADAS" directamente desde la interfaz móvil.
* **Historial de Servicios:** Registro de todos los servicios prestados y cancelados.

## 🛠️ Tecnologías Utilizadas

* **Lenguaje:** Java 
* **Interfaz de Usuario (UI):** XML (Material Design Components, CardViews, Layouts interactivos).
* **Arquitectura de Red:** * [Retrofit2](https://square.github.io/retrofit/): Cliente HTTP tipado para el consumo de la API.
  * **GSON:** Para la serialización y deserialización de objetos JSON.
* **Almacenamiento Local:** `SharedPreferences` para gestión de sesiones y persistencia de identidad del usuario.
* **Backend Soportado:** Spring Boot (API RESTful), MySQL.

## 📂 Estructura del Monorepo

Este repositorio está estructurado para contener tanto el frontend móvil como el backend, facilitando la integración continua:

```text
/Madhouse_Proyecto_Completo
│
├── /MadhouseMobile         # Frontend Android (Este proyecto)
│   ├── /app/src/main/java  # Código fuente Java (Activities, Fragments, Models, Network)
│   └── /app/src/main/res   # Recursos XML (Layouts, Drawables, Values)
│
└── /Backend_SpringBoot     # Backend API (Spring Boot)
