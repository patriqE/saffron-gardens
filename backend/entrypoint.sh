#!/bin/sh
set -e

# Load any files in /run/secrets as environment variables (name -> content)
if [ -d "/run/secrets" ]; then
  for f in /run/secrets/*; do
    if [ -f "$f" ]; then
      name=$(basename "$f")
      # read file content
      val=$(cat "$f")
      export "$name"="$val"
    fi
  done
fi

echo "Starting app with env vars: DB_HOST=${DB_HOST:-not-set}, DB_NAME=${DB_NAME:-not-set}, PRIMARY_SUPERADMIN_USERNAME=${PRIMARY_SUPERADMIN_USERNAME:-not-set}"

exec java -jar /app/app.jar
