# ExternalsManagement-be

## Prerequisites
- Docker Desktop

## Run (Dockerized app + DB)
From the project root:

```powershell
docker compose up -d --build
```

This starts:
- `app` on `http://localhost:8080`
- `postgres` on `localhost:5432`

Swagger UI:
- `http://localhost:8080/swagger-ui.html`

## Check status
```powershell
docker compose ps
```

## Stop
```powershell
docker compose down
```

To also remove the Postgres volume data:

```powershell
docker compose down -v
```

## Logs
```powershell
docker compose logs -f app
```

## Local (non-Docker) fallback
If you want to run without Docker for the app process, Java 21 is required:

```powershell
$env:JAVA_HOME='C:\Program Files\Java\jdk-21'
$env:Path="$env:JAVA_HOME\bin;$env:Path"
.\mvnw.cmd clean compile -DskipTests
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=local" -DskipTests
```

## Troubleshooting
`Could not find or load main class ma.nttdata.externals.ExternalsManagementBeApplication`

Compiled output in `target/classes` is stale or incomplete (often after a failed build with the wrong JDK).

```powershell
$env:JAVA_HOME='C:\Program Files\Java\jdk-21'
$env:Path="$env:JAVA_HOME\bin;$env:Path"
.\mvnw.cmd clean compile -DskipTests
```
