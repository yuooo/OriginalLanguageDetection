#!/bin/bash
#Variations:
#Do this PER NOVEL. Not per directory.

#Roman numerals: [IVX]+\.

#Remove subtitle after chapter:
#CHAPTER

#^[A-Z]
#^[A-Z'] (for around the world in 80 days)

#Add ^PART ^Book ^Chapter for Karamazov

#Part Chapter for Madame

#Chapter for TMITIM


#What I should do:
#1. Take in a filename as input
#2. Take one token and add it to grep. Ask if it's OK to remove this line.
#3. IF so, remove. If not, get the next line.

LANGUAGE=$1
TEXT=$2
shift
shift
for arg in "$@"
do
    echo "Is this output OK? (y/n)"
    grep $arg $TEXT
    while [1]; do
	read answer
	if [ "$answer" == "y" ]; then
	    echo "Do sed stuff!"
	    break
	elif [ "$answer" == "n"]; then
	    break
	else
	    echo "Please answer y or n."
	fi
    done
done


for file in $1/*
do
    filename_out="${file%.*}"
    file_out="../txt_clean_new/${filename_out}_clean.txt"
    echo $file_out 
    sed -e "/^CHAPTER\|^BOOK\|^VOLUME/Id" -e "/^PART/d" $file > $file_out
done

