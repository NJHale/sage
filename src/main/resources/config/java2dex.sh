#!/bin/bash"
dx="/usr/share/android-sdk-linux/build-tools/23.0.2/dx"

configPath="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
path=$1
name=$2

javaFile=$path$name.java
classFile=$path$name.class
dexFile=$path$name.dex

echo $path

javac -source 1.6 -target 1.6 -cp $configPath $javaFile
$dx --dex --output $dexFile $path