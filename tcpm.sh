#!/usr/bin/env bash
#
#  Copyright 2022 Red Hat
#
#  Licensed under the Apache License, Version 2.0 (the "License");
#  you may not use this file except in compliance with the License.
#  You may obtain a copy of the License at
#
#      https://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS,
#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#  See the License for the specific language governing permissions and
#  limitations under the License.
#


# --------------------------------------------------
#
# tcpm: script to make (t)est(c)ontainers work with (p)odman on (m)acOS.
# Starts a SSH tunnel to the podman socket on the podman machine.
# See also https://github.com/testcontainers/testcontainers-java/issues/2088#issuecomment-911586506
#
# --------------------------------------------------

set -Eeuo pipefail
trap cleanup SIGINT SIGTERM ERR EXIT

VERSION=0.0.1

# Change into the script's directory
# Using relative paths is safe!
script_dir=$(cd "$(dirname "${BASH_SOURCE[0]}")" &>/dev/null && pwd -P)
readonly script_dir
cd "${script_dir}"

usage() {
  cat <<EOF
USAGE:
    $(basename "${BASH_SOURCE[0]}") [FLAGS]

FLAGS:
    -h, --help          Prints help information
    -v, --version       Prints version information
    --no-color          Uses plain text output
EOF
  exit
}

cleanup() {
  trap - SIGINT SIGTERM ERR EXIT
  rm -f /tmp/podman.sock 2>/dev/null
}

setup_colors() {
  if [[ -t 2 ]] && [[ -z "${NO_COLOR-}" ]] && [[ "${TERM-}" != "dumb" ]]; then
    NOFORMAT='\033[0m' RED='\033[0;31m' GREEN='\033[0;32m' ORANGE='\033[0;33m' BLUE='\033[0;34m' PURPLE='\033[0;35m' CYAN='\033[0;36m' YELLOW='\033[1;33m'
  else
    # shellcheck disable=SC2034
    NOFORMAT='' RED='' GREEN='' ORANGE='' BLUE='' PURPLE='' CYAN='' YELLOW=''
  fi
}

msg() {
  echo >&2 -e "${1-}"
}

die() {
  local msg=$1
  local code=${2-1} # default exit status 1
  msg "$msg"
  exit "$code"
}

version() {
  msg "${BASH_SOURCE[0]} $VERSION"
  exit 0
}

parse_params() {
  while :; do
    case "${1-}" in
    -h | --help) usage ;;
    -v | --version) version ;;
    --no-color) NO_COLOR=1 ;;
    -?*) die "Unknown option: $1" ;;
    *) break ;;
    esac
    shift
  done

  return 0
}

parse_params "$@"
setup_colors

[ -x awk ] && die "awk not installed"
[ -x jq ] && die "jq not installed"
[ -x podman ] && die "podman not installed"
PORT=$(podman system connection list --format=json | jq '.[0].URI' | awk -F'ssh://core@localhost:|/run/user/' '{print $2}')
[ -z "${PORT}" ] && die "Cannot extract port from 'podman system connection list'"

msg "Open ${YELLOW}SSH tunnel${NOFORMAT} at port ${CYAN}${PORT}${NOFORMAT}"
msg "Press ${CYAN}CTRL-C${NOFORMAT} to stop"
ssh \
  -i ~/.ssh/podman-machine-default \
  -p "${PORT}" \
  -L '/tmp/podman.sock:/run/user/1000/podman/podman.sock' \
  -N \
  core@localhost
