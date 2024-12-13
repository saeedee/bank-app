#!/bin/bash

# Build the Docker image using the Dockerfile
echo "Building Docker image..."
docker build -t bank-app:1.0 .

# Check if the Docker image was built successfully
if [ $? -ne 0 ]; then
    echo "Failed to build Docker image."
    exit 1
fi

# Start the services defined in docker-compose.yaml
echo "Starting services..."
docker-compose up -d

# Check if services started successfully
if [ $? -ne 0 ]; then
    echo "Failed to start services."
    exit 1
fi

echo "Services started successfully!"