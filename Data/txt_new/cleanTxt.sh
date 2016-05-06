#Variations:
#Do this PER NOVEL. Not per directory.

#Roman numerals: [IVX]+\.

#Remove subtitle after chapter:
#CHAPTER

#^[A-Z]


#!/bin/bash
for file in $1/*
do
    filename_out="${file%.*}"
    file_out="../txt_clean_new/${filename_out}_clean.txt"
    echo $file_out 
    sed -e "/^CHAPTER\|^BOOK\|^VOLUME/Id" -e "/^PART/d" $file > $file_out
done

