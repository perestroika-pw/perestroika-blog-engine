#!/bin/bash

RGB="/usr/share/color/icc/ghostscript/default_rgb.icc"
CMYK="/usr/share/color/icc/ghostscript/default_cmyk.icc"

convert perestroika-eurosize-argumendid-vol3.png -profile $RGB -profile $CMYK perestroika-eurosize-argumendid-vol3.tif

