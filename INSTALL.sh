#!/bin/bash

#set default install path
install_path=/usr/local/src/notes2latex
#set default operation
op=mv
#process command line arguments, the following are accepted:
#-d to set the installation directory
#-c to copy source files instead of moving them
while [[ $# -gt 0 ]]; do
    case $1 in
	"-d")
	    if [[ $# -gt 1 ]]; then 
		install_path="$2"/notes2latex
	    fi
	    shift 2
	    ;;
	"-c")
	    op="cp -r"
	    shift
	    ;;
	*)
	    echo "Unrecognised argument " "$1" >&2
	    echo "usage: " "$0" ' [-c] [-d install_path]' >&2
	    exit 1
    esac
done

#check if install_path dir already exists and create it if needed
if [[ ! -d install_path ]]; then
    sudo mkdir -p $install_path || exit 2
fi
#move data to install_path
sudo $op Notes2Latex.java latexnotesparser/ $install_path || exit 3
#create default configuration file
cp default_notes2latex ~/.notes2latex || exit 4
cd $install_path || exit 5
#compile java program
sudo javac Notes2Latex.java || exit 6
echo "Installation completed. Default .notes2latex file created in " ~
