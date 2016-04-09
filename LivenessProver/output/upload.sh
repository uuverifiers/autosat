#!/bin/bash

FILES=" \
	automatonB.svg \
	automatonF.svg \
	automatonI0.svg \
	transducerOrder.svg \
	transducerP1.svg \
	transducerP2.svg \
	source.txt \
	index.html \
	output.log \
	"

TARGET="kazi.fit.vutbr.cz:~/www/show/"

scp -C $(echo ${FILES}) ${TARGET}
