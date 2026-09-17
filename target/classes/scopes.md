# 3. Extensión de los scopes a los endPoints de la API

## Cambios

Los endpoints de la 'apli\blueprints' ya no responden datos de ejemplo en memoria, ahora usan la misma capa de persistencia en PostgreSQL que habíamos implementado en la parte 1 (en específico, en [PostgresBlueprintPersistence](/src/main/java/co/edu/eci/blueprints/persistence/PostgresBluePrintPersistence.java) sobre la tabla de [blueprints](/src/main/resources/init.sql)).

El control de acceso por scopes se extendió para los 5 endpoints existentes

## Scopes por Endpoint

| Endpoint | Método | Scope requerido | Motivo |
| --- | --- | --- | --- |
| `/api/blueprints` | GET | `blueprints.read` | Consulta los datos, no los modifica. |
| `/api/blueprints/{author}` | GET | `blueprints.read` | Consulta filtrada, solo por el autor, sin modificación de datos. |
| `/api/blueprints/{author}/{name}` | GET | `blueprints.read` | Consulta puntual por autor y nombre, no se modifican los datos. |
| `/api/blueprints` | POST | `blueprints.write` | Crea un recurso nuevo en la base de datos con todos los elementos requeridos. |
| `/api/blueprints/{author}/{name}/points` | PUT | `blueprints.write` | Modifica un recurso existente agregando más puntos. |


La implementación es sencilla: si la operación ***lee***, pide `blueprints.read`; si ***crea o modifica***, pide `blueprints.write`. Un token que solo tenga `blueprints.read` puede consultar, pero un `POST` o `PUT` con ese mismo token debe fallar.

## Dos capas de control

Tenemos 2 formas de realizar el control que se presentan de la siguiente manera:

1. **`SecurityConfig`**: cualquier request a `/api/**`necesita alguno de los dos scopes (`hasAnyAuthority`). Esto bloquea a cualquiera
   que ni siquiera tenga un token válido con permisos sobre blueprints.

2. **`@PreAuthorize` en cada método**: exige el scope específico de esa operación. Esto es lo que realmente nos ayuda a separar la lectura de escritura, sin esta capa, un token con solo `blueprints.read` podría pasar el primer filtro y aun así crear blueprints.

## Pruebass

1. Login normal:

Ingresamos al auth/controller para obtener el token de acceso

![Autorizacion](/src/main/resources/photos/authController.png)

Ingresamos ese token en la parte de "Authorize" para obtener nuestra autorización

![](/src/main/resources/photos/Authorize.png)

Probamos todos los endpoints y todos nos dan el visto bueno (GET, POST, PUT)

![](/src/main/resources/photos/get.png)
![](/src/main/resources/photos/post.png)
![](/src/main/resources/photos/put.png)

Si, en caso contrario, quitamos la autorización, ya no podemos acceder a las respuestas de ejecución de ninguno de esos endpoints.