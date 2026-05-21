param(
    [switch]$Clean
)

$ErrorActionPreference = "Stop"
Set-Location (Split-Path -Parent $PSScriptRoot)

Write-Host ""
Write-Host "Perfis criados pelo seed (senha: Demo2026!)" -ForegroundColor Cyan
Write-Host "  admin        -> admin@kanflow.local        (dono; edita tudo no workspace demo)" -ForegroundColor White
Write-Host "  membro       -> usuario@kanflow.local      (mesmo workspace e sprints; edita cards/sprint)" -ForegroundColor White
Write-Host "  visualizador -> visualizador@kanflow.local  (mesmo workspace e sprints; so leitura)" -ForegroundColor White
Write-Host ""

if (-not $env:JWT_SECRET) {
    $env:JWT_SECRET = "dev-only-change-me-dev-only-change-me"
}

$defaultPort = 8080
$port = if ($env:SERVER_PORT) { [int]$env:SERVER_PORT } else { $defaultPort }
$onPort = Get-NetTCPConnection -LocalPort $port -State Listen -ErrorAction SilentlyContinue
if ($onPort) {
    $port = 8081
    Write-Host "Porta $defaultPort ocupada — a usar SERVER_PORT=$port" -ForegroundColor Yellow
}
$env:SERVER_PORT = "$port"

if ($Clean) {
    Write-Host "mvn clean compile..." -ForegroundColor DarkGray
    mvn -q clean compile -DskipTests
} else {
    mvn -q compile -DskipTests
}

Write-Host "A iniciar API em http://localhost:$env:SERVER_PORT (perfis local,seed-demo)..." -ForegroundColor Green
mvn spring-boot:run "-Dspring-boot.run.profiles=local,seed-demo"
