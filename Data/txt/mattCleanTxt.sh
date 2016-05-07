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

TYPE=$1
LANGUAGE=$2
TEXT=$3
FILE="${TYPE}/${LANGUAGE}/${TEXT}"
OUTPUTFILE="../txt_clean_new/${TYPE}/${LANGUAGE}/${TEXT::-4}_clean.txt"
shift
shift
shift
TMP=`mktemp`
#echo $TMP
TMP2=`mktemp`
#echo $TMP2
cat $FILE > $TMP
for arg in "$@"
do
    pcregrep -M "$arg" ${TYPE}/${LANGUAGE}/${TEXT} #> some_sample_file.txt

    while [ 1 ]; do
        echo "Is this output OK? (y/n)"
	read answer
	if [ "$answer" == "y" ]; then
	    #ARRAY+=("$arg")
	    perl -0777 -pe "s/${arg}//gm" $TMP > $TMP2
	    DEL=$TMP
	    TMP=$TMP2
	    rm $DEL
	    TMP2=`mktemp`
	    break
	elif [ "$answer" == "n" ]; then
	    break
	else
	    echo "Please answer y or n."
	fi
    done
done
#replace underscores
tr < $TMP '_' ' ' > $TMP2
cat $TMP2 > $OUTPUTFILE
rm  $TMP $TMP2 #some_sample_file.txt

