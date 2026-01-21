# Flyway (local developer & CI usage)

This file explains how to run Flyway commands locally and in CI without committing database credentials.

## Environment variables (recommended)
Set these environment variables in your shell or CI job:
- FLYWAY_URL - JDBC url to the database (e.g. `jdbc:postgresql://localhost:5432/saffrondb`)
- FLYWAY_USER - DB user
- FLYWAY_PASSWORD - DB password

Example (PowerShell):

```powershell
$env:FLYWAY_URL = 'jdbc:postgresql://localhost:5432/saffrondb'
$env:FLYWAY_USER = 'postgres'
$env:FLYWAY_PASSWORD = '12345'
.\mvnw.cmd flyway:repair
```

## CLI overrides (alternative)
You can also pass Flyway config as -D properties on the mvnw command line:

```powershell
.\mvnw.cmd flyway:migrate -Dflyway.url='jdbc:postgresql://localhost:5432/saffrondb' -Dflyway.user=postgres -Dflyway.password=12345
```

## CI / Production
Store DB credentials in your CI's secret storage and inject them as environment variables into the job step that runs Flyway.

Example (GitHub Actions snippet):

```yaml
- name: Flyway repair
  env:
    FLYWAY_URL: ${{ secrets.DB_URL }}
    FLYWAY_USER: ${{ secrets.DB_USER }}
    FLYWAY_PASSWORD: ${{ secrets.DB_PASSWORD }}
  run: ./mvnw flyway:repair
```

## Notes
- Spring Boot's runtime Flyway (auto-run on application startup) uses `application.properties`. The Maven plugin is independent — use environment variables or -D overrides to configure it.
- Never commit production passwords to the repository.
