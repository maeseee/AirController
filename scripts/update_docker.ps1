# Execute with powershell -ExecutionPolicy Bypass -File .\update_docker.ps1

# --- KONFIGURATION ---
$SSHUser = "cloudy"
$SSHHost = "192.168.50.12"
$ProjectBase = "C:\Users\maese\Documents\Repos\AirController"
$NetworkShareBase = "\\MyCloud77\docker\air_controller_app"
$SynologyRoot = "/volume1/docker/air_controller_app"

# Liste der zu ignorierenden Ordner (Namen oder Pfade)
$ExcludeList = ".angular", ".idea", ".vscode", "node_modules", ".git", "bin", "obj", ".gitignore"

$ProjectMap = @{
    "src" = "air-controller-backend\src"
    "pom.xml" = "air-controller-backend"
    "doc\Docker\Dockerfile" = "air-controller-backend"
    "front_end" = "air-controller-frontend"
    "doc\Docker\.env" = ""
    "doc\Docker\compose.yaml" = ""
}

# --- DEPLOYMENT LOGIK ---

Write-Host ">>> Starte Kopuervorgang (Excludes: $ExcludeList)..." -ForegroundColor Cyan

foreach ($SourceFolder in $ProjectMap.Keys)
{
    $Source = Join-Path -Path $ProjectBase $SourceFolder
    $Destination = Join-Path -Path $NetworkShareBase -ChildPath $ProjectMap[$SourceFolder]

    Write-Host "Kopiere $Source nach $Destination!" -ForegroundColor Gray

    # Sicherstellen, dass der Zielordner existiert
    if (!(Test-Path $Destination))
    {
        New-Item -ItemType Directory -Path $Destination -Force | Out-Null
    }

    if (Test-Path -Path $Source -PathType Leaf)
    {
        $FileName = Split-Path $Source -Leaf
        $SourceDir = Split-Path $Source -Parent

        robocopy $SourceDir $Destination $FileName /MT:8 /R:2 /W:5 /NFL /NDL /NJH /NJS
    }
    else
    {
        robocopy $Source $Destination /MIR /XD $ExcludeList /MT:8 /R:2 /W:5 /NFL /NDL /NJH /NJS
    }
}

$LogDir = Join-Path -Path $NetworkShareBase -ChildPath "air-controller-backend\log"
New-Item -Path $LogDir -ItemType Directory -Force | Out-Null

# --- DOCKER UPDATE (FRONTEND & BACKEND) ---
$SSHPass = "" # TODO read password from terminal
Write-Host "`n>>> Aktualisiere Docker Container via SSH..." -ForegroundColor Cyan
$InnerCommand = "cd $SynologyRoot && /usr/local/bin/docker-compose down --remove-orphans && /usr/local/bin/docker-compose up -d --build"
$FullSSHCommand = "echo '$SSHPass' | sudo -S sh -c '$InnerCommand'"

ssh "$SSHUser@$SSHHost" "$FullSSHCommand"
Write-Host "`n>>> Frontend und Backend wurden aktualisiert!" -ForegroundColor Green