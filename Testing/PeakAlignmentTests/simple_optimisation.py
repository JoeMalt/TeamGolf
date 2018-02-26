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

def peakDissimilarity(peak1, peak2, xpos_importance = xPosDifferenceImportance):
	# TODO: replace basic y-similarity comparison by dot product of the y-scan
	# profile
	return np.exp(- np.power(peak1["position"] - peak2["position"], 2)/(2 * np.power(xpos_importance, 2)))
	

def gapPenalty(perGapCost, perMissCost, gapLength):
	return perGapCost + perMissCost * gapLength; 
	
for i in range(





