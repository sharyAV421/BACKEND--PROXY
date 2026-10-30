# AI Usage Proxy Backend

Backend REST con Java 17, Spring Boot y H2 en memoria. No incluye frontend.

## Ejecución local

Requiere Java 17 y Maven. No requiere instalar ni configurar un servidor de base de datos. Ejecuta:

```bash
mvn spring-boot:run
```

El servidor escucha en `http://localhost:8080`. Hibernate crea automáticamente las tablas en H2 al iniciar. Los datos persisten mientras el proceso está activo y se pierden al apagar o reiniciar la aplicación.

## API

Todas las rutas requieren `X-User-Id`.

| Método | Ruta | Uso |
|---|---|---|
| POST | `/api/ai/generate` | JSON `{ "prompt": "..." }`; responde texto y tokens consumidos |
| GET | `/api/quota/status` | Plan, tokens usados/disponibles y fecha de reinicio |
| GET | `/api/quota/history` | Consumo por día de los últimos 7 días |
| POST | `/api/quota/upgrade` | Simula pago y actualiza FREE a PRO |

El límite por minuto responde `429` con `Retry-After`; cuota agotada responde `402`. Los errores usan JSON.

## Variables de entorno

- `PORT`: puerto HTTP, lo proporciona Render (por defecto `10000`). Localmente Spring usa `8080` si no se define.
- `VERCEL_ORIGIN`: origen permitido por CORS, por ejemplo `https://mi-app.vercel.app`.

No se necesitan `PGHOST`, `PGPORT`, `PGDATABASE`, `PGUSER` ni `PGPASSWORD`.

## Despliegue en Render

1. Crea un servicio web desde este repositorio y la rama `main`.
2. Selecciona el entorno Docker para que Render construya el `Dockerfile` de la raíz.
3. No hace falta crear ni conectar un servicio PostgreSQL. H2 se incluye en el JAR y se inicia en memoria junto con Spring Boot.
4. Render proporciona `PORT`; no hace falta asignarlo manualmente. Opcionalmente configura `VERCEL_ORIGIN` para el futuro frontend.
5. Genera el dominio público del servicio desde Settings/Networking.

H2 conserva los registros mientras la instancia de la aplicación permanece activa. Al reiniciar o desplegar una instancia nueva, la base en memoria se crea vacía; no es almacenamiento persistente entre despliegues.
