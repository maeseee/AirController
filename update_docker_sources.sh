#!/bin/bash

# Configuration
PROJECT_BASE="/home/marcel/Repos/AirController"
NETWORK_SHARE_BASE="/mnt/docker/air_controller_app"
EXCLUDES=(".angular" ".idea" ".vscode" "node_modules" ".git" "bin" "obj" ".gitignore")

# Project Map (Source relative to ProjectBase : Destination relative to NetworkShareBase)
declare -A PROJECT_MAP
PROJECT_MAP["Dockerfile"]="air-controller-backend"
PROJECT_MAP["front_end"]="air-controller-frontend"
PROJECT_MAP["compose.yaml"]=""
PROJECT_MAP["target/AirController-1.2-SNAPSHOT.jar"]="air-controller-backend/target"

cd "$PROJECT_BASE" || { echo "Error: Could not enter $PROJECT_BASE"; exit 1; }
mvn clean install -DskipTests

EXCLUDE_ARGS=()
for item in "${EXCLUDES[@]}"; do
    EXCLUDE_ARGS+=("--exclude=$item")
done

for SOURCE_REL in "${!PROJECT_MAP[@]}"; do
    SOURCE="$PROJECT_BASE/$SOURCE_REL"
    DEST_REL="${PROJECT_MAP[$SOURCE_REL]}"
    DEST="$NETWORK_SHARE_BASE/$DEST_REL"

    if [ ! -e "$SOURCE" ]; then
        echo -e "\e[31mERROR: Source not found at $SOURCE\e[0m"
        continue
    fi

    echo -e "\e[90mCopy $SOURCE to $DEST!\e[0m"

    mkdir -p "$DEST"

    if [ -f "$SOURCE" ]; then
        cp "$SOURCE" "$DEST/"
    else
        rsync -av --delete "${EXCLUDE_ARGS[@]}" "$SOURCE/" "$DEST/"
    fi
done

LOG_DIR="$NETWORK_SHARE_BASE/air-controller-backend/log"
mkdir -p "$LOG_DIR"

echo -e "\n\e[32m>>> All sources from Frontend and Backend are updated!\e[0m"