#!/bin/bash

FILES=" \
	*.svg \
	source.txt \
	enabled_encoded.txt \
	fair_encoded.txt \
	index.html \
	index-old.html \
	output.log \
	"

TARGET="kazi.fit.vutbr.cz:~/www/show/"

scp -C $(echo ${FILES}) ${TARGET}
