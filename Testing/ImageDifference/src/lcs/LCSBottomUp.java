package lcs;

public class LCSBottomUp extends LCSFinder {
	
	private static final int N = 0; 
	private static final int NW = 1; 
	private static final int W  = 2; 

	private int[][] reconstruction_helper; 

	public static void main (String[] args) {
		String s1 = "BDCABA "; 
		String s2 = "ABCBDAB"; 
		
		LCSBottomUp lcs_bottom_up_instance = new LCSBottomUp(s1, s2); 
		
		System.out.println("String 1 : " + s1);
		System.out.println("String 2 : " + s2);
		System.out.println("LCS length = " + lcs_bottom_up_instance.getLCSLength());
		System.out.println("LCS itself = " + lcs_bottom_up_instance.getLCSString());
	}


	public LCSBottomUp(String s1, String s2) {
		super(s1, s2);
		if (!s1.isEmpty() &&  !s1.isEmpty()) {	// if neither string empty, initialise array, otherwise leave null
													// arbitrarily choose s1's characters to be along top row (one column per s1 character) 
													// and choose s2's characters to be arranged in a column (i.e. one row per s1 character). 
			super.mTable = new int[s1.length() ][s2.length() ];  // NOW :: tester doesn't allow that, so modified read/write functions for array accordingly PREV ::: add one to lengths -- represent subproblems involving empty prefix.					
	
			this.reconstruction_helper = new int[ s1.length() + 1 ][ s2.length() + 1 ]; 
			
		}	
	}

	
	
	private int readmemo(int i, int j) {				// Wrapper function to retrieve value from array - and return zero if (i == 0 || j == 0)

		if ( i == 0 || j == 0 ) {
			return 0; 
		}
		else {
			return (mTable[ i - 1 ][ j - 1 ]); 
		}
		
	}
	
	private void updateReconstructionHelper(int i, int j, int info) {
		assert (info == N || info == NW || info == W); 
		this.reconstruction_helper[i][j] = info; 
	}
	
	private void storeMemo (int i, int j, int val) {
		super.mTable[i - 1][j - 1] = val; 
	}
	
	private int computeTableEntry (int prefix_length_1 , int prefix_length_2 ) {
		// precondition is that computeTableEntry has been called on all prefix length pairs smaller than 
		// the current argument (so those table entries are valid)
		if ( prefix_length_1 > 0 && prefix_length_2 > 0 ) {
			// if strings characters match then return 1 + c[i - 1, j - 1] read from memo. 
			// otherwise return max of c[i - 1, j], c[i , j - 1] 
			// where in both cases i = first argument of fn, j = second argument
			
			// char is primitive so OK to do == ...
			
			// prefix_length is one greater than the index position of the char we're interested in 
			// since we denote prefix_length == 0 to be the empty string
			
			if (  super.mString1.charAt(prefix_length_1 - 1) == super.mString2.charAt(prefix_length_2 - 1) ) {
				int v1 = 1 + readmemo( prefix_length_1 - 1 ,  prefix_length_2 - 1 ); 
				
				this.storeMemo(prefix_length_1, prefix_length_2, v1);	// update memo
				this.updateReconstructionHelper(prefix_length_1, prefix_length_2, NW); // state that we used the optimal solution of LCS with both final characters removed. 
				return v1; 
			}
			else {
				int q1 = readmemo ( prefix_length_1 - 1, prefix_length_2 ); 
				int q2 = readmemo ( prefix_length_1 , prefix_length_2 - 1); 
				int res = q1 > q2 ? q1 : q2; 
				
				// Determine which subproblem solution we used 
				int dependency; 
				
				if (res == q1) {
					dependency = W; // we removed a character from prefix of string 1 but not from prefix of string 2. 
									// so the subproblem we rely on is directly to the 'west' in the mTable array. 
				}
				else {
					assert (res == q2);
					dependency = N; // other case ... 
				}
				
				this.storeMemo(prefix_length_1, prefix_length_2, res);
				this.updateReconstructionHelper(prefix_length_1, prefix_length_2, dependency);
				return res;
			}
			
		}
		else {
			// either prefix is empty, length of lcs = 0
			return 0; 
		}
	}
	
	@Override
		public int getLCSLength() {	
													// Should compute the full table of LCS lengths 
													// return the final LCS length 
													// Should not compute the actual LCS string. 
													// Table of length solutions should be stored in mTable arraysky
													// if either input string is empty, solution should return zero for getLCSLength() 
		
		for ( int prefix_s2_size = 0; prefix_s2_size <= super.mString2.length() ; prefix_s2_size ++) {
			
			// Fix the row index (prefix_s2_size) to the length of the prefix of string 2 being considered
			
			for ( int  prefix_s1_size = 0; prefix_s1_size <= super.mString1.length() ; prefix_s1_size ++ ) {
			
				// Fix the column index to (prefix_s1_size), the length of the prefix of string 1 being considered 
				
				// Guaranteed to have computed all previous results (bottom up) 
				// Also updates memo table
				int prefix_lcs = this.computeTableEntry(prefix_s1_size, prefix_s2_size); 
			}
		}
		
		int length_lcs = this.readmemo( super.mString1.length(), super.mString2.length()); 
		return length_lcs; 
	}

	@Override
	public String getLCSString() {
		
													// Use table of solutions in mTable to compute actual LCS as a strin
													// If either input string is empty, solution should return "" for getLCSString, 
													// mTable should be null
													

		// If either string was empty, mTable was set to null by constructor, so check that ...
		if ( super.mTable == null ){
			return (""); 
		}
		
		
		// Allocate a char array of the same length of the LCS (and we need to find the lcs length so everything we need to find the lcs string is initialised properly). 
		// Walk back from the bottom right element of the mTable according to the direction in reconstruction helper
		// Read out the corresponding character whenever we make a NW transition (indicating we're including that character in the solution)
		// asserting that this character is indeed the same for both strings... 
		// This will enumerate the LCS in reverse order, so reverse the char array we've built and convert back to string, and return. 
		
		int length_of_lcs = getLCSLength(); 
		
		
		char[] lcs_chars_reversed = new char[ length_of_lcs ]; 
		
		int iFirstPrefix  = super.mString1.length(); // index position of last column in mTable  
		int iSecondPrefix = super.mString2.length(); // index position of last row in mTable[some column]
		int iAcc = 0; // index position to place next char. 
		
		while ( iFirstPrefix > 0 && iSecondPrefix > 0  ) {	// termination condition - where either prefix goes empty... 
			
			int dependency = this.reconstruction_helper[iFirstPrefix][iSecondPrefix]; 
			if ( dependency == NW ) {
				
				char next_char = super.mString1.charAt(iFirstPrefix- 1); 
				lcs_chars_reversed[iAcc++] = next_char; 
				assert (super.mString1.charAt(iFirstPrefix) == super.mString2.charAt(iSecondPrefix)); 
				
				iFirstPrefix--; 
				iSecondPrefix--; 
				
			}
			else if ( dependency == W ) {
				iFirstPrefix--; 
			}
			else {
				assert (dependency == N); 
				iSecondPrefix--; 
			}
		}
		
		String longest_cs_reversed = new String ( lcs_chars_reversed ); 
		String longest_cs = new StringBuilder(longest_cs_reversed).reverse().toString(); 
		return longest_cs; 
	}
	
	
	
	
}
