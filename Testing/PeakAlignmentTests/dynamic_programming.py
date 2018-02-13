#!/usr/bin/python
import numpy as np
import matplotlib.pyplot as plt 

# x range of data
data_length = 1000

# peaks: list of {"position": x, "intensity":y, "narrowness":w} records 
peak_list1 = [
				{"position": 100, "intensity":1, "narrowness":1}, 
				{"position": 200, "intensity":1, "narrowness":1},
				{"position": 300, "intensity":1, "narrowness":1},
				{"position": 400, "intensity":1, "narrowness":1},
				{"position": 500, "intensity":1, "narrowness":1},
				{"position": 600, "intensity":1, "narrowness":1},
				{"position": 700, "intensity":1, "narrowness":1},
				{"position": 800, "intensity":1, "narrowness":1}
			]
			
peak_list2 = [
				{"position": 121, "intensity":1, "narrowness":1}, 
				{"position": 198, "intensity":1, "narrowness":1},
				{"position": 322, "intensity":1, "narrowness":1},
				{"position": 399, "intensity":1, "narrowness":1},
				{"position": 512, "intensity":1, "narrowness":1},
				{"position": 589, "intensity":1, "narrowness":1},
				{"position": 712, "intensity":1, "narrowness":1},
				{"position": 811, "intensity":1, "narrowness":1}
			]

# GENERAL PARAMETERS
perGapCost = 5
perMissCost = 1
xPosDifferenceImportance = 1

	
def print_table(table):
	for row in table:
		print(row)
		

def gaussian(centre, k, intensity, xpos):
	"""
		gaussian(centre, k, x, intensity) == (intensity * exp(-(k * (x - centre))^2))
	"""
	return intensity * np.exp(- np.power(k * (xpos - centre), 2))
	
def create_peaks(peaks, length):
	signal = []
	for xpos in range(length):
		
		signal_at_xpos = 0.0
		
		for pos_int_nar in peaks:
			
			pos = pos_int_nar["position"]
			int = pos_int_nar["intensity"]
			nar = pos_int_nar["narrowness"]
			signal_at_xpos += gaussian(pos, nar, int, xpos)
			
		signal.append(signal_at_xpos)
		
	return signal
	
def display_signal(peak_list_list, length):
	for peak_list in peak_list_list:
		signal = create_peaks(peak_list, length)
		plt.plot(signal) 
	plt.show()



# DISPLAY THE SIGNALS WE'RE USING 
# display_signal([peak_list1, peak_list2], data_length)


# peak1 = {"position":x, "intensity":y, "narrowness":w}
# peak2 = {"position":x, "intensity":y, "narrowness":w}

def peakDissimilarity(peak1, peak2, xpos_importance = xPosDifferenceImportance):
	# TODO: replace basic y-similarity comparison by dot product of the y-scan
	# profile
	return np.exp(- np.power(peak1["position"] - peak2["position"], 2)/(2 * np.power(xpos_importance, 2)))
	
# Implements an affine penalty function so that if the miss cost is B, and the length of the gap is L 
# and the fixed cost for starting a new gap is A, then returns A+BL
def gapPenalty(perGapCost, perMissCost, gapLength):
	return perGapCost + perMissCost * gapLength; 
	
m = len(peak_list1)
print("m = " + str(m))

n = len(peak_list2)
print("n = " + str(n))

memo_table = []

# Create the memo table
def createMbyNZeroTable(m, n):
	result = []
	for j in range(n):
		result.append([0 for _ in range(m)]) 
	return result
	
memo_table = createMbyNZeroTable(m+1, n+1) 

rightmost_matched_peak_table = createMbyNZeroTable(m+1, n+1)

# Convert peak list into the right format for the DP
def get_peak_list_keyed_by_number(peakList):
	resultList = []
	for record in peakList:
		pos = record["position"]
		int = record["intensity"]
		nar = record["narrowness"]		
		resultList.append((pos, int, nar))
	resultList = sorted(resultList, key = lambda x:x[0])
	return resultList 
	
pl1 = get_peak_list_keyed_by_number(peak_list1)
pl2 = get_peak_list_keyed_by_number(peak_list2)

print(pl1)
print(pl2)

# i range: 0 <= i <= n
# because: n peaks => n+1 gaps 
# j range: 0 <= j <= m

# set row = 0 values to the right costs 
for j in range(m+1):
	memo_table[0][j] = gapPenalty(perGapCost, perMissCost, j)

# set column = 0 values to the right costs
for i in range(n+1):
	memo_table[i][0] = gapPenalty(perGapCost, perMissCost, i)

# A gap of length zero isn't actually a gap.
memo_table[0][0] = 0

for i in range(m+1):
	for j in range(m+1):
		if i == 0 or j == 0:
			continue
		else:	
		
		
			
				
				
print("Final state of memo_table") 
print_table(memo_table)


,