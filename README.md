# Practica2

Este proyecto implementa una solución completa de cliente-servidor para la gestión de notas (CRUD), autenticación segura y despliegue mediante contenedores.

---
###Integrantes:
Castro Rodriguez Paola Yazmin
Díaz Peña Alfredo Yael
Flores Madrigal Diego
Mejia Franco Esteban Saúl
## Arquitectura del Proyecto

El sistema se divide en dos componentes principales:

1.  **Backend (Node.js + Express):**
    * Desplegado en **Docker**.
    * Autenticación con **Bcryptjs** para el hash de contraseñas.
    * Manejo de **CORS** para comunicación con dispositivos móviles.
    * Base de datos temporal en memoria (Array de objetos).
2.  **Frontend (Android - Kotlin):**
    * Arquitectura basada en **Fragments**.
    * Navegación mediante **Navigation Drawer** (Menú lateral).
    * Consumo de API REST con **Retrofit 2** y **Coroutines**.

---

## Guía de Instalación del Backend (Docker)

Sigue estos pasos para levantar el servidor en cualquier computadora con Docker:

1.  **Clonar el repositorio:**
    ```bash
    git clone [https://github.com/tu-usuario/nombre-del-repo.git](https://github.com/tu-usuario/nombre-del-repo.git)
    cd T3-Backend
    ```

2.  **Preparar el entorno:**
    Asegúrate de que el puerto **5000** esté libre. Si Windows lo tiene ocupado (por AirPlay u otros servicios), puedes cambiar el mapeo en el archivo `docker-compose.yml` de `5000:5000` a `3001:5000`.

3.  **Construir y ejecutar el contenedor:**
    Ejecuta el siguiente comando en la terminal (PowerShell o CMD):
    ```powershell
    docker-compose up --build
    ```
    *El flag `--build` es indispensable para que Docker instale las dependencias (`express`, `cors`, `bcryptjs`) dentro del contenedor.*

4.  **Confirmación:**
    El servidor estará listo cuando veas en la terminal: 
    `api-1 | --- SERVIDOR ACTIVO PARA LA RED ---`

---

## Configuración de la App Android

Para que el celular se comunique con el servidor en Docker:

1.  **Sincronizar IP:**
    Identifica la IP local de la computadora donde corre Docker (usando `ipconfig`). 
2.  **Actualizar Retrofit:**
    En los archivos `LoginFragment.kt`, `RegisterFragment.kt` y `CrudFragment.kt`, asegúrate de que la URL sea:
    `http://tu_ip_local:5000/`
3.  **Red:** Ambos dispositivos (celular y laptop) deben estar conectados a la **misma red Wi-Fi**.

---

## Estado del Proyecto (Checklist)

### ✅ Finalizado (100% Funcional)
* **Dockerización:** Contenedor optimizado y mapeo de puertos exitoso.
* **Seguridad:** Registro y Login (falta la seguridad que puede ser con Json Web Token).
* **Endpoints API:** *`POST /register` y `POST /login`.
        
    * `GET /notas` (Obtención de lista). 
    * `POST /notas` (Creación con ID dinámico `Date.now()`).
    * `DELETE /notas/:id` (Borrado por ID).
* **UI Android:** Menú lateral funcional y diseño base de fragments.

### Pendientes para el Equipo (DevCore Crew)
1.  **Refresco de Interfaz (Bug Visual):** El backend confirma la creación/borrado de notas (ver logs de Docker), pero Android a veces lanza un `Error de conexión` falso debido al formato de respuesta. Se requiere ajustar el `catch` en Retrofit para forzar el refresco de la lista.
Aqui falta corregir el error que da al iniciar (error de conexion, ademas de q cuando quieres subir una nota no la muestra) es error de android, el servidor si la recibe 
2.  **Gestión de Colores:** Validar que el texto en el `ListView` sea visible (evitar texto blanco sobre fondo blanco).
3.  **Update (PUT):** El endpoint en Node.js ya está creado, falta implementar el diálogo de edición en el `CrudFragment`.

---

## Tecnologías Utilizadas
* **Backend:** Node.js, Express, Docker, Docker Compose.
* **Seguridad:** Bcrypt.js, CORS.
* **Android:** Kotlin, Retrofit 2, Coroutines, Navigation Component.
