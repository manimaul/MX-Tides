#!/bin/bash
######################
inkscape --without-gui --export-png="../src/main/res/drawable-ldpi/icon.png" --export-dpi=120 --export-background-opacity=0 "./icon.svg"
inkscape --without-gui --export-png="../src/main/res/drawable-mdpi/icon.png" --export-dpi=160 --export-background-opacity=0 "./icon.svg"
inkscape --without-gui --export-png="../src/main/res/drawable-hdpi/icon.png" --export-dpi=240 --export-background-opacity=0 "./icon.svg"
inkscape --without-gui --export-png="../src/main/res/drawable-xhdpi/icon.png" --export-dpi=320 --export-background-opacity=0 "./icon.svg"
inkscape --without-gui --export-png="../src/main/res/drawable-xxhdpi/icon.png" --export-dpi=480 --export-background-opacity=0 "./icon.svg"
