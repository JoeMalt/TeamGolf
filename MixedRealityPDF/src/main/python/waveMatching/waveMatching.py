#!/usr/bin/env python3
import math
import sys
import numpy as np
from scipy import optimize
from sys import argv

def normalize(X, range, min=0):
    eX = math.e**X
    return range*eX/(1+eX) + min

def matchAreas(original, modified):
    Ao = 0
    Am = 0
    for i in original:
        Ao += i
    for i in modified:
        Am += i
    scale = Ao/Am
    for i in range(0, len(modified)):
        modified[i] *= scale
    return scale

def calculate_offset(scale, original, modified):
    Po = 0 #Peak original
    Xo = 0 #X    original
    for x in range(0, len(original)):
        if original[x] > Po:
            Po = original[x]
            Xo = x
    Pm = 0 #Peak modified
    Xm = 0 #X    modified
    for x in range(0, len(modified)):
        val = modified[x] / scale
        if val > Pm:
            Pm = val
            Xm = x * scale

    return Xo - Xm

def wave_difference(scale, original, modified): 
    offset = calculate_offset(scale, original, modified)
    limitM = len(modified)
    limitO = len(original)
    # transformation preserves the area.
    def getTransform(m):
        m = m - offset
        k = m / scale
        l = m % scale
        if math.isnan(k):
            return 0
        k = int(k)
        a = modified[k]   if 0 <= k   and k   < limitM else 0
        b = modified[k+1] if 0 <= k+1 and k+1 < limitM else 0
        return (a + ((l*(b-a))/scale)) / scale # divide by scale to keep areas the same
    errorV = 0
    for x in range(int(min(0, -offset)), int(max(limitO, limitM*scale - offset))):
        h1 = original[x] if 0 <= x and x < limitO else 0
        h2 = getTransform(x)
        errorV += (h1 - h2)**2
    return errorV

if(len(argv) != 5):
    print("scan_y: wave representation of the scanned  document along the Y axis.")
    print("scan_x: wave representation of the scanned  document along the X axis.")
    print("orig_y: wave representation of the original document along the Y axis.")
    print("orig_x: wave representation of the original document along the X axis.")
    print("Usage: ./wavematching scan_y scan_x orig_y orig_x")
    sys.exit()

file_scan_y = open(argv[1], "r")
file_scan_x = open(argv[2], "r")
file_orig_y = open(argv[3], "r")
file_orig_x = open(argv[4], "r")


scan_y = file_scan_y.read().strip()
scan_x = file_scan_x.read().strip()
orig_y = file_orig_y.read().strip()
orig_x = file_orig_x.read().strip()

scan_y = list(map(int, (scan_y if scan_y[0].isdigit() else scan_y[1:-1]).split(',')))
scan_x = list(map(int, (scan_x if scan_x[0].isdigit() else scan_x[1:-1]).split(',')))
orig_y = list(map(int, (orig_y if orig_y[0].isdigit() else orig_y[1:-1]).split(',')))
orig_x = list(map(int, (orig_x if orig_x[0].isdigit() else orig_x[1:-1]).split(',')))

intensityX = matchAreas(scan_y, orig_y)
intensityY = matchAreas(scan_x, orig_x) 
intensity = (intensityX + intensityY) / 2

def normalize(x):
    return normalize(x, 2, 1/2)

def opt_error(x):
    X = normalize(x)
    error = 0
    error += wave_difference(X, orig_y, scan_y)
    error += wave_difference(X, orig_x, scan_x)
    return error

ret = optimize.basinhopping(opt_error, 0, niter=100)
#print(opt_error(vector))

print("I'm here")

scale = normalize(ret.x)
offsetY = calculate_offset(scale, orig_y, scan_y)
offsetX = calculate_offset(scale, orig_x, scan_x)

print("\nRESULTS")
print("global min x = %s, f(x0) = %.4f" % (str(ret.x), ret.fun))
print("scale - %.4f"     %scale)
print("intensity - %.4f" %intensity)
print("offsetX - %.4f"    %offsetX)
print("offsetY - %.4f"    %offsetY)
print("")

print(opt_error(ret.x))

