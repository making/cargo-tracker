#!/bin/bash
DIR=$(dirname $0)
curl --data-binary "@${DIR}/src/test/resources/sampleHandlingReport.json" \
    -H 'Content-Type: application/json;charset=UTF-8' \
    http://localhost:8080/handlingReport -v
