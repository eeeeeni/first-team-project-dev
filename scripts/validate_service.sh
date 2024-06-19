#!/bin/bash

# Validate service script
echo "Validating service at $(date)"
curl -f https://sportlink.store || exit 1
echo "Service is up and running at $(date)"