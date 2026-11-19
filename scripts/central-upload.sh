#!/usr/bin/env bash
# Uploads the artifacts that `sbt publishSigned` staged in target/sona-staging to the Central
# Portal, then waits until the Portal has validated them (and, with `release`, is publishing them).
#
# Usage: scripts/central-upload.sh release|upload
#   release: the Portal publishes the deployment to Maven Central once it is validated.
#   upload:  the deployment waits, validated, in https://central.sonatype.com/publishing/deployments,
#            to publish or drop it there.
#
# Needs SONATYPE_USERNAME and SONATYPE_PASSWORD (a user token of the Central Portal), curl, zip
# and jq.
#
# It replaces sbt's sonaUpload/sonaRelease, which, for this bundle (about 57MB, with Z3's native
# libraries), retried failed uploads without reporting why, then hung until its timeout.
set -euo pipefail

mode=${1:?Usage: $0 release|upload}
case "$mode" in
  release) publishing_type=AUTOMATIC ;;
  upload) publishing_type=USER_MANAGED ;;
  *) echo "Unknown mode '$mode', expected release or upload" >&2; exit 2 ;;
esac
: "${SONATYPE_USERNAME:?}" "${SONATYPE_PASSWORD:?}"

staging=target/sona-staging
if [ ! -d "$staging" ] || [ -z "$(ls -A "$staging")" ]; then
  echo "Nothing staged in $staging: run sbt publishSigned first" >&2
  exit 1
fi

api=https://central.sonatype.com/api/v1/publisher
auth="Authorization: Bearer $(printf '%s:%s' "$SONATYPE_USERNAME" "$SONATYPE_PASSWORD" | base64 | tr -d '\n')"
work=$(mktemp -d)
bundle=$work/bundle.zip
(cd "$staging" && zip -qr "$bundle" .)
name=$(basename "$(find "$staging" -name '*.pom' | head -n 1)" .pom)
echo "Bundle $name: $(du -h "$bundle" | cut -f1), $(unzip -Z1 "$bundle" | grep -cv '/$') files"
unzip -Z1 "$bundle" | grep -v '/$' | sed 's/^/  /'

# Runs curl, printing the HTTP status and the response; fails on an error status. Fails instead of
# hanging: when the connection can't be made in 1 minute, when less than 10kB/s go through for
# 2 minutes, or after 30 minutes in all.
call() {
  local out=$work/response code
  code=$(curl --silent --show-error --output "$out" --write-out '%{http_code}' \
    --connect-timeout 60 --speed-limit 10240 --speed-time 120 --max-time 1800 \
    -H "$auth" "$@") || { echo "curl failed (HTTP status: ${code:-none})" >&2; cat "$out" >&2 2>/dev/null; return 1; }
  if [ "${code:0:1}" != 2 ]; then
    echo "The Central Portal answered HTTP $code:" >&2
    cat "$out" >&2; echo >&2
    return 1
  fi
  cat "$out"
}

echo "Uploading to the Central Portal, as $publishing_type"
# No retry, which could create two deployments of the same version
deployment=$(call --form "bundle=@$bundle" "$api/upload?name=$name&publishingType=$publishing_type")
echo "Deployment $deployment uploaded"

# Validation usually takes a few minutes; the state is in the Portal too
for _ in $(seq 1 120); do
  status=$(call -X POST "$api/status?id=$deployment")
  state=$(jq -r .deploymentState <<< "$status")
  echo "Deployment $deployment: $state"
  case "$state" in
    PUBLISHING|PUBLISHED) exit 0 ;;
    VALIDATED) if [ "$mode" = upload ]; then exit 0; fi ;;
    FAILED) jq . <<< "$status" >&2; exit 1 ;;
  esac
  sleep 15
done
echo "Deployment $deployment still not validated after 30 minutes: see the Central Portal" >&2
exit 1
