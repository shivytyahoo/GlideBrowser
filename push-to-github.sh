#!/bin/bash
# Push GlideBrowser to GitHub — run on a PC after `gh auth login`.
# Repo https://github.com/shivytyahoo/GlideBrowser already exists (created with an empty README).
set -e
cd "$(dirname "$0")"
git remote remove origin 2>/dev/null || true
git remote add origin https://github.com/shivytyahoo/GlideBrowser.git
git branch -M main
git push -u origin main --force
echo "Pushed. Actions: https://github.com/shivytyahoo/GlideBrowser/actions"
