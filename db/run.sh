#!/bin/sh
# The migrations image's entry point: migrate | info | validate | seed | export-seed | backup, against the database in the
# environment (DB_HOST, DB_PORT, DB_NAME, DB_OWNER_USER, DB_OWNER_PASSWORD), over TLS.
set -eu

: "${DB_HOST:?DB_HOST is required}"
: "${DB_NAME:?DB_NAME is required}"
: "${DB_OWNER_USER:?DB_OWNER_USER is required}"
: "${DB_OWNER_PASSWORD:?DB_OWNER_PASSWORD is required}"
DB_PORT="${DB_PORT:-5432}"
# require: managed databases take TLS only. A local database sets DB_SSLMODE=disable.
DB_SSLMODE="${DB_SSLMODE:-require}"

case "${1:-migrate}" in
  migrate | info | validate)
    # Exercise texts quote ${...} property syntax: Flyway must leave placeholders alone.
    exec flyway \
      -url="jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME}?sslmode=${DB_SSLMODE}" \
      -user="$DB_OWNER_USER" -password="$DB_OWNER_PASSWORD" \
      -locations=filesystem:/flyway/sql -placeholderReplacement=false \
      "$1"
    ;;
  seed)
    # An export from another environment, on stdin. One transaction; refuses a database that
    # already has content.
    PGPASSWORD="$DB_OWNER_PASSWORD" exec psql -q -v ON_ERROR_STOP=1 \
      "host=${DB_HOST} port=${DB_PORT} dbname=${DB_NAME} user=${DB_OWNER_USER} sslmode=${DB_SSLMODE}" \
      -f -
    ;;
  export-seed)
    # This database's content as a new environment's seed, on stdout (read-only): see
    # seed/export-content.sql for what travels.
    PGPASSWORD="$DB_OWNER_PASSWORD" exec psql -At -v ON_ERROR_STOP=1 \
      "host=${DB_HOST} port=${DB_PORT} dbname=${DB_NAME} user=${DB_OWNER_USER} sslmode=${DB_SSLMODE}" \
      -f /flyway/seed/export-content.sql
    ;;
  backup)
    # The whole database as plain SQL on stdout (the caller compresses and stores it). Restores
    # with psql into an empty database: no owner or privileges in it, those are the target's.
    PGPASSWORD="$DB_OWNER_PASSWORD" exec pg_dump --no-owner --no-privileges \
      "host=${DB_HOST} port=${DB_PORT} dbname=${DB_NAME} user=${DB_OWNER_USER} sslmode=${DB_SSLMODE}"
    ;;
  *)
    echo "Usage: migrate | info | validate | seed | export-seed | backup" >&2
    exit 2
    ;;
esac
