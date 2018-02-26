import math 

original = [100, 200, 300, 400]
scan = [95, 202, 301, 405, 441]

matches = []


# i : peak index in scan
# j : peak index in pdf 
# original : list of peak positions in original pdf 
# scan : list of peak positions in scanned version
# matches : list of pairs (x, y) where x < i and y < j of peaks that have been matched so far. 

def eval_cost(matches):
	cost = 0
	for x, y in matches:
		cost += (original[x] - scan[y])**2
	return cost 

# return cost of best match given matches made so far 
def tryMatch(i, j, matches, scan, original):


	# If all peaks in the original have been matched (i.e. j >= len(pdf)) 
	# then return the cost of the current match list [eval_cost(matches)]

	if j >= len(original):
		eval_cost(matches)
		
	else:	
	
		# For the current peak being considered from the original pdf 
		# we could assign it to any of the scan peaks with indices [ i, len(scan) )
		# Try each of these assignments and recursively complete the matching...
		
		
		best_sofar = matches
		
		old_matches = matches[:]
		mincost = math.inf 
		
		inext = i
		jnext = j
		
		for i_try in range(i, len(scan)):
			if eval_cost (matches.append((i_try, j))) < mincost:
				best_sofar = tryMatch(i_try+1, j+1, matches[:].append((j_try, i)), original, scan)
			 
		skipped_one = tryMatch(i+1,j, matches, original, scan)  
		
		if skipped_one < mincost: 
			return skipped_one
		else:
			return mincost 
		
		
		
		