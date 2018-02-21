#!/usr/bin/env python3
import math
import sys
import numpy as np
from scipy import optimize
from sys import argv

def frange(x, y, jump):
	while x < y:
		yield x
		x += jump

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
    #scale modified by scale then find peaks of original and modified
    #the difference of the peaks is the offset.
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

#    print("Po - %f" % Po)
#    print("Xo - %d" % Xo)
#    print("Pm - %f" % Pm)
#    print("Xm - %d" % Xm)
#    print("Diff - %d" % (Xo-Xm))
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
        #print(h1, end=" - ")
        #print(h2)
        errorV += (h1 - h2)**2
    #print("error - %s - %s" % (str(scale), str(errorV)))
    return errorV

original1 = [0,2,4,0] 
modified1 = [0,0,2,2,4,4,2,0,0]

original2 = [0,0,2,4,4,0,0,1,2,3,4,5,6,7,8,9,0]*10
modified2 = [0,0,2,4,4,0,0,1,2,3,4,5,6,7,8,9,0]*10
#assert(error([1,1,0], original2, modified2) == 0)
modified2 = modified2[1:]

original3 = [0,0,2,4,4,0,0]
modified3 = [0,0,1,2,2,0,0]
#assert(error([1,2,0], original3, modified3) == 0)

if(len(argv) != 3):
    print("Usage: ./wavematching input_file1 input_file2")
    sys.exit()


file_A = open(argv[1], "r")
file_B = open(argv[2], "r")

data_A = file_A.read().strip()
data_B = file_B.read().strip()

data_A = list(map(int, (data_A if data_A[0].isdigit() else data_A[1:-1]).split(',')))
data_B = list(map(int, (data_B if data_B[0].isdigit() else data_B[1:-1]).split(',')))

data_A = original2
data_B = modified2


intensity = matchAreas(data_A, data_B)

print(data_A)
print(data_B)

print(calculate_offset(1, data_A, data_B))

#sys.exit()

opt_error = lambda x : wave_difference(normalize(x, 2, 1/2), data_A, data_B)

ret = optimize.basinhopping(opt_error, 0, 10)
#print(opt_error(vector))

print("I'm here")

scale = normalize(ret.x, 3, 1/3)
offset = calculate_offset(scale, data_A, data_B)

print("\nRESULTS")
print("global min x = %s, f(x0) = %.4f" % (str(ret.x), ret.fun))
print("scale - %.4f"     %scale)
print("intensity - %.4f" %intensity)
print("offset - %.4f"    %offset)
print("")

print(opt_error(ret.x))


