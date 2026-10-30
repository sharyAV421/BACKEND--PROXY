# AI Usage Proxy Backend

Backend REST con Java 17, Spring Boot y PostgreSQL. No incluye frontend.

## Ejecución local

Requiere Java 17, Maven y PostgreSQL. Crea una base `ai_proxy` y configura `PGHOST`, `PGPORT`, `PGDATABASE`, `PGUSER` y `PGPASSWORD`; luego ejecuta:

```bash
mvn spring-boot:run
```

El servidor escucha en `http://localhost:8080`. El esquema se actualiza con Hibernate (`ddl-auto=update`). Para Railway se incluye Dockerfile.

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

- `PORT`: puerto HTTP (Railway lo proporciona).
- `PGHOST`, `PGPORT`, `PGDATABASE`, `PGUSER`, `PGPASSWORD`: conexión PostgreSQL.
- `VERCEL_ORIGIN`: origen permitido por CORS, por ejemplo `https://mi-app.vercel.app`.
