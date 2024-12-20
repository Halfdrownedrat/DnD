#!/bin/bash

# Navigate to the directory containing the script
cd "$(dirname "$0")"

# Add all changes to git
git add .

# Commit the changes with a message
git commit -m "Automated commit from script"

# Push the changes to the remote repository
git push


