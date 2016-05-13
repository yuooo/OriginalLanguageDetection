#!/bin/bash

TMP=`mktemp`
echo 'universe = vanilla
environment = CLASSPATH=/u/mad4672/NLP/Project/OriginalLanguageDetection/Jars/*:/u/mad4672/NLP/Project/OriginalLanguageDetection/out

Initialdir = /u/mad4672/NLP/Project/OriginalLanguageDetection
Executable = /usr/bin/java

+Group   = "GRAD"
+Project = "INSTRUCTIONAL"
+ProjectDescription = "CS388 Project"

Notification = complete
Notify_user = mad4672@cs.utexas.edu
' >> $TMP
OUTPUT_FILE=$1
shift
for arg in "$@" 
do
    IFS='/' read -ra ADDR <<< "$arg"
    echo "Log = /u/mad4672/NLP/Project/condor_logs/${arg::-4}.log" >> $TMP
    echo "Arguments = -mx8g -server mess.preprocessing.MassPreprocessor -tree -oneNovel $arg" >> $TMP
    echo "Output = /u/mad4672/NLP/Project/condor_logs/${arg::-4}.out" >> $TMP
    echo "Error  = /u/mad4672/NLP/Project/condor_logs/${arg::-4}.err" >> $TMP
    echo "Queue 1" >> $TMP
    echo  >> $TMP
done
cat $TMP > $OUTPUT_FILE
rm $TMP

