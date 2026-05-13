$ErrorActionPreference = "Stop"
Set-Location (Split-Path -Parent $PSScriptRoot)

if (-not $env:JWT_SECRET) {
    $env:JWT_SECRET = "dev-only-change-me-dev-only-change-me"
}

if (-not $env:SERVER_PORT) {
    $on9090 = Get-NetTCPConnection -LocalPort 9090 -State Listen -ErrorAction SilentlyContinue
    if ($on9090) {
        $env:SERVER_PORT = "9091"
        Write-Host "Porta 9090 ocupada; a usar SERVER_PORT=$env:SERVER_PORT (ex.: Swagger em http://localhost:$env:SERVER_PORT/swagger-ui.html)." -ForegroundColor Yellow
    }
}

Write-Host "A iniciar Spring Boot com perfis local,seed-demo (seed corre uma vez por arranque)..." -ForegroundColor Cyan

mvn -q spring-boot:run "-Dspring-boot.run.profiles=local,seed-demo"
