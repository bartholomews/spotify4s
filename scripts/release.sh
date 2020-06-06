#!/usr/bin/env bash

TAG=$1

push-tag() {
    git tag -a ${TAG} -m ${TAG} && git push origin ${TAG}
}

[[ ${TAG} =~ ^v[0-9]{1,2}.[0-9]{1,2}.[0-9]{1,2}$ ]] \
&& push-tag \
|| (echo "argument need to match version regex (e.g. \"v0.0.1\")" && exit 1)