# Execute with powershell -ExecutionPolicy Bypass -File .\update_docker_sources.ps1

$ProjectBase = "C:\Users\maese\Documents\Repos\AirController"
$NetworkShareBase = "\\MyCloud77\docker\air_controller_app"
$ExcludeList = ".angular", ".idea", ".vscode", "node_modules", ".git", "bin", "obj", ".gitignore"
$ProjectMap = @{
    "Dockerfile" = "air-controller-backend"
    "front_end" = "air-controller-frontend"
    "compose.yaml" = ""
#    ".env" = ""
    "target\AirController-1.2-SNAPSHOT-jar-with-dependencies.jar" = "air-controller-backend"
}

cd $ProjectBase
mvn clean install -DskipTest

foreach ($SourceFolder in $ProjectMap.Keys)
{
    $Source = Join-Path -Path $ProjectBase $SourceFolder
    if (!(Test-Path -Path $Source))
    {
        Write-Host "ERROR: Source not found at $Source" -ForegroundColor Red
        continue
    }

    $Destination = Join-Path -Path $NetworkShareBase -ChildPath $ProjectMap[$SourceFolder]
    Write-Host "Copy $Source to $Destination!" -ForegroundColor Gray

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

Write-Host "`n>>> All sources from Frontend and Backend are updated!" -ForegroundColor Green