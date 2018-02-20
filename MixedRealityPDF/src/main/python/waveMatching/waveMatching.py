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

def normalize(X, min, range):
    eX = math.e**X
    return range*eX/(1+eX) + min
        

def remap(vector):
    Xs, Ys, Xo = vector
    Xs = normalize(Xs, 1/3, 3)
    Ys = normalize(Ys, 1/4, 4)
    Xo = normalize(Xo, -50, 150)
    return [Xs, Ys, Xo]

def error(vector, original, modified): # vector = [X_scale, Y_scale, X_offset
    vector = remap(vector)
    print(vector)
    Xs, Ys, Xo = vector
    limitM = len(modified)
    limitO = len(original)

    def getTransform(m):
        if Xs > 1:
            m = m - Xo
            k = m / Xs
            l = m % Xs
            a = modified[k]   if 0 <= k   and k   < limitM else 0
            b = modified[k+1] if 0 <= k+1 and k+1 < limitM else 0
            return Ys*(a + ((l*(b-a))/Xs))
        else: # Xs <= 1:
            m = m - Xo
            k = m / Xs
            a = modified[k] if 0 <= k and k < limitM else 0
            return Ys*a

    errorV = 0
    for x in range(min(0, -Xo), max(limitO, limitM*Xs - Xo)):
        h1 = original[x] if 0 <= x and x < limitO else 0
        h2 = getTransform(x)
        errorV = (h1 - h2)**2

    return errorV

def apply(vector, modified):
    Xs, Ys, Xo = vector
    modified = list(map(lambda x: x*Ys, modified))

    return modified

def print_fun(x, f, accepted):
	print("at minimum %.4f accepted %d" % (f, int(accepted)))



original1 = [0,0,2,2,4,4,0,0]
modified1 = [0,2,4,0] 
#assert(error([2,1,0], original1, modified1) == 0)

original2 = [0,0,2,4,4,0,0,1,2,3,4,5,6,7,8,9,0]*4
modified2 = [0,0,2,4,4,0,0,1,2,3,4,5,6,7,8,9,0]*4
#modified2 = modified2[1:]
#assert(error([1,1,.1], original2, modified2) == 0)


original3 = [0,0,2,4,4,0,0]
modified3 = [0,0,1,2,2,0,0]
#assert(error([1,2,0], original3, modified3) == 0)

print(argv)

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

opt_error = lambda x : error(remap(x), data_A, data_B)

ret = optimize.basinhopping(opt_error, [1,1,2], niter=100)
#print(opt_error(vector))

results = remap(ret.x)

print("\nRESULTS")
print("global min x = %s, f(x0) = %.4f\n" % (str(results), ret.fun))
print("Xs - %.4f" %abs(results[0]))
print("Ys - %.4f" %abs(results[1]))
print("Xo - %.4f" %abs(results[2]))

print(opt_error(ret.x))


