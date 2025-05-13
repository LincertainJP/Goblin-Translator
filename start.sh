#!/bin/bash

java  -XX:+ShowCodeDetailsInExceptionMessages -cp "/home/lae/Documents/Work/N7/2A/TDL/MiniC/target/classes:/home/lae/Documents/Work/N7/2A/TDL/MiniC/tools/commons-lang3-3.12.0.jar:/home/lae/Documents/Work/N7/2A/TDL/MiniC/tools/commons-text-1.9.jar:/home/lae/Documents/Work/N7/2A/TDL/MiniC/tools/antlr-4.13.1-complete.jar" fr.n7.stl.minic.Driver "$@"