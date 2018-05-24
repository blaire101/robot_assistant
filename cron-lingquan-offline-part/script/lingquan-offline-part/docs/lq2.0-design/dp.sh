#!/bin/bash

echo "start commit..."
git status
git commit -m "add a article or modify article"
git push origin master
echo "successful commit"
