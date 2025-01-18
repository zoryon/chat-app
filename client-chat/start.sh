#!/bin/bash

# run with host's network and display setted to host's display
docker run -it --rm --network host -e DISPLAY=$(ifconfig getifaddr en0):0 -v /tmp/.X11-unix:/tmp/.X11-unix zoryon/client-chat:1.0
