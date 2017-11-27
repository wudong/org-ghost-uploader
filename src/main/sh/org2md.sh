#!/usr/bin/env bash

ORGFILE=$1
printf "Converting org file %s ... " $ORGFILE

emacsclient -e "(find-file \"$ORGFILE\")" "(org-md-export-to-markdown)" "(kill-buffer (buffer-name))" > /dev/null

if [ $? ]
then
    printf "Done\n"
else
    printf "Failed\n"
fi    


