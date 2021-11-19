#!/bin/bash

libraryName="openbanking-client"

libraryVersion=$(grep '^version=.*$' "gradle.properties" | sed "s/^version=\(.*\)$/\1/")

echo "Checking ${libraryName}:${libraryVersion} in artifactory."

artifactPath="https://arti.tw.ee/artifactory/libs-release-local/com/transferwise/${libraryName}/${libraryVersion}/${libraryName}-${libraryVersion}.jar"
echo "${artifactPath}"
artifactStatus=$(curl -s -o /dev/null -I -w "%{http_code}" "${artifactPath}")

if [ "${artifactStatus}" == "404" ]; then
  echo "${libraryName} version ${libraryVersion} does not exist. Publishing."
  exit 0
else
  echo "${libraryName} version ${libraryVersion} exists. Skip publishing."
  exit 1
fi
