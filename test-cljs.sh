#!/usr/bin/env bash
if [ ! -f target/inflections-test.js ]; then
    lein cljsbuild once
fi
echo "inflections.test.run()" | d8 --shell target/inflections-test.js
