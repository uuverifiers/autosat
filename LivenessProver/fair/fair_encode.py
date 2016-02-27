#!/usr/bin/python3

import argparse
import re
import sys




def parseOptions():
    '''parseOptions() -> options

Parses command-line options and returns them as an object with the following
attributes:

  filename - the name of the input file as a String
'''
    parser = argparse.ArgumentParser(description="Encodes (weak) fairness"
        " into a two-player transition system")
    parser.add_argument("filename", metavar="file")
    args = parser.parse_args()
    return args


def processAut(firstLine, it, output):
    '''processAut(firstLine, it, output)

Processes an automaton in the proper manner.
'''
    output.append(firstLine)
    for line in it:
        if (line == "}"): # end of automaton
            output.append(line)
            return
        else:
            output.append("kuk")


def processTopFile(it, output):
    '''processTopFile(it, output)

Processes top file structures in a file.  Modifies it and output.
'''
    for line in it:
        if (line[0:2] == "//"): # comments
            output.append(line)
        elif (line == ""): # empty string
            output.append(line)
        elif (re.match(r'^[a-zA-Z0-9]+;$', line)): # option
            output.append(line)
        elif (re.match(r'^[a-zA-Z0-9]+:.+;$', line)): # option with parameter
            output.append(line)
        elif (re.match(r'^[a-zA-Z0-9]+\ +\{$', line)): # beginning of an automaton
            processAut(line, it, output)
        else:
            output.append("*****" + line)


def processLines(inlines):
    '''processLines([inline]) -> [outline]

Process input lines into output lines, adding fairness into the system
'''
    outlines = []
    it = iter(inlines)

    processTopFile(it, outlines)

    return outlines


if __name__ == '__main__':
    options = parseOptions()
    # input
    inlines = [line.strip() for line in open(options.filename)]
    # output
    outlines = processLines(inlines)

    for line in outlines:
        print(line)
