#!/bin/bash
for file in $1/*
do
    filename_out="${file%.*}"
    file_out="../txt_clean/${filename_out}_clean.txt"
    echo $file_out 
    gsed -e "/^CHAPTER\|^BOOK\|^VOLUME/Id" -e "/^PART/d" $file > $file_out
done

