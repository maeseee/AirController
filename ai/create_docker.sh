sudo docker build -t claude-code-container .
sudo docker run -it -e ANTHROPIC_API_KEY="your-api-key" -v /home/marcel/Repos/AirController:/workspace claude-code-container