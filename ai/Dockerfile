FROM ubuntu:latest

RUN apt-get update && apt-get install -y \
    git \
    curl \
    nodejs \
    npm

RUN npm install -g @anthropic-ai/claude-code

WORKDIR /workspace