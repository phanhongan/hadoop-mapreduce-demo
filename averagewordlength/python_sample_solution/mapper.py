#!/usr/bin/python

import re
import sys

NONALPHA = re.compile("\W")

for input in sys.stdin.readlines():
  for w in NONALPHA.split(input):
    if len(w) > 0:
      print w[0].lower() + '\t' + str(len(w))

    
