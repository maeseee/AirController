# Execute with powershell -ExecutionPolicy Bypass -File .\update_docker.ps1

$SSHUser = "cloudy"
$SSHHost = "192.168.50.12"
$SynologyRoot = "/volume1/docker/air_controller_app"

Write-Host "`n>>> Aktualisiere Docker Container via SSH..." -ForegroundColor Cyan
$DockerPath = "/usr/local/bin/docker-compose"
$GoToProjectDir = "cd $SynologyRoot"
$StopContainersAndDeleteImages = "sudo $DockerPath down --rmi all"
$BuildProject ="sudo $DockerPath up -d --build"
$RemoteCommand = "$GoToProjectDir && $StopContainersAndDeleteImages && $BuildProject"

Write-Host "`n>>> RemoteCommand: $RemoteCommand" -ForegroundColor Green

ssh -t "$SSHUser@$SSHHost" "$RemoteCommand"

if ($LASTEXITCODE -eq 0) {
    Write-Host "`n[SUCCESS] Frontend und Backend wurden aktualisiert!" -ForegroundColor Green
} else {
    Write-Host "`n[ERROR] Die Aktualisierung ist fehlgeschlagen! (Exit Code: $LASTEXITCODE)" -ForegroundColor Red
    exit $LASTEXITCODE
}