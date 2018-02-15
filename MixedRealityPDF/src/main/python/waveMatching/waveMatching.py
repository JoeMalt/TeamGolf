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


def error(vector, original, modified): # vector = [X_scale, Y_scale, X_offset
    Xs, Ys, Xo = vector
    # both checks neccessary cuz floating point.
    if Xs < 1 and 1/Xs > 1:
        return error([1/Xs, 1/Ys, -Xo], modified, original)
    Xo = int(Xo)
	

    ## original[i] and modified[transform(i)] maps to the same X coordinate.
    #transform = lambda x: int(math.floor(min(max(Xs*x + Xo, 0), len(modified)-1)))
    #modified[i] and original[transform(i)] maps to the same X coordinate.
    #transform = lambda x: int(math.floor(min(max(Xs*x + Xo, 0), len(original)-1)))

    def getTransformed(x):
        new_x = Xs*x + Xo
        floor = math.floor(new_x)
        ceil = math.ceil(new_x)
        if floor < 0 or len(original) <= ceil:
            return 0
        b = original[ceil]
        a = original[floor]
        # interpolating between two points
        return a + (new_x - int(new_x))*(b - a)
        


    errorV = 0
    for i in range(0, len(modified)):
        print(i, end=" - ")
        print(getTransformed(i))
        errorV += (Ys*modified[i] - getTransformed(i))**2
    print("----------------------")

    return errorV

def apply(vector, modified):
    Xs, Ys, Xo = vector
    modified = list(map(lambda x: x*Ys, modified))

    return modified

def print_fun(x, f, accepted):
	print("at minimum %.4f accepted %d" % (f, int(accepted)))



original1 = [0,0,2,2,4,4,0,0]
modified1 = [0,2,4,0] 
assert(error([2,1,0], original1, modified1) == 0)

original2 = [0,0,2,4,4,0,0,1,2,3,4,5,6,7,8,9]
modified2 = [0,2,4,4,0,0,1,2,3,4,5,6,7,8,9]
assert(error([1,1,1], original2, modified2) == 0)


original3 = [0,0,2,4,4,0,0]
modified3 = [0,0,1,2,2,0,0]
assert(error([1,2,0], original3, modified3) == 0)

print(argv)

if((len(argv) - 1) % 2 != 0):
    print("Usage: ./wavematching input_file1 input_file2")
    sys.exit()
    


print (argv[1:])
file_A = open(argv[1], "r")
file_B = open(argv[2], "r")

data_A = file_A.read().strip()
data_B = file_B.read().strip()
print (data_A)
print (data_B)

data_A = list(map(int, (data_A if data_A[0].isdigit() else data_A[1:-1]).split(',')))
data_B = list(map(int, (data_B if data_B[0].isdigit() else data_B[1:-1]).split(',')))

data_A = original2
data_B = modified2

opt_error = lambda x : error(x, data_A, data_B)

ret = optimize.basinhopping(opt_error, [1,1,0], niter=100)
#print(opt_error(vector))
print("global min x = %s, f(x0) = %.4f" % (str(ret.x), ret.fun))
print("Xs - %.4f" %ret.x[0])
print("Ys - %.4f" %ret.x[1])
print("Xo - %.4f" %ret.x[2])

print(opt_error(ret.x))


