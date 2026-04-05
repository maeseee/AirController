#!/bin/bash

SSH_USER="cloudy"
SSH_HOST="192.168.50.12"
SYNOLOGY_ROOT="/volume1/docker/air_controller_app"
DOCKER_PATH="/usr/local/bin/docker-compose"

echo -e "\n\e[36m>>> Update Docker Container via SSH...\e[0m"

REMOTE_COMMANDS="cd $SYNOLOGY_ROOT && sudo $DOCKER_PATH down --rmi all && sudo $DOCKER_PATH up -d --build"

echo -e "\n\e[32m>>> RemoteCommand: $REMOTE_COMMANDS\e[0m"
ssh -t "$SSH_USER@$SSH_HOST" "$REMOTE_COMMANDS"
EXIT_CODE=$?

if [ "$EXIT_CODE" -eq 0 ]; then
    echo -e "\n\e[32m[SUCCESS] Frontend and Backend are updated!\e[0m"
else
    echo -e "\n\e[31m[ERROR] The update ended in a failure! (Exit Code: $EXIT_CODE)\e[0m"
    exit $EXIT_CODE
fi