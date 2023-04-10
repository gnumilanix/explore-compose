#!/bin/sh

echo "Running pre-commit hook..."

./gradlew ktlint

if [ "$?" ]; then
  exit 0
else
  echo "Static analysis found problems. Please see output above and resolve."
  exit 1
fi
