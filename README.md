# LZW_Compression
An implementation of LZW compression algorithm in Java.

## Overview 

LZW is a compression algorithm that was created in 1984 by Abraham Lempel, Jacob Ziv, and Terry Welch.In its most basic form, it will output a compressed file as a series of fixed-length codewords.*Variable-width* codewords can be used to increase the size of codewords output as the dictionary fills up.Further, once the dictionary fills up, the algorithm can either stop adding patterns and continue compression with only the patterns already discovered, or the algorithm can reset the codebook to find new patterns.

This project is a modification of the LZW compression algorithm to use variable-width codewords, and to optionally reset the codebook under certain conditions. 
With these changes in hand, I have compared the performance of modified LZW code with the LZW code in course textbook, and further with the performance of a widely used compression application.

## How to run
Compile the program by running `javac MyLZW.java`.  

There are 3 modes to run this project:  

1. **Do Nothing mode** Do nothing and continue to use the full codebook (this is already implemented by `LZW.java`).  
2. **Reset mode** Reset the dictionary back to its initial state so that new codewords can be added. Be careful to reset at the appropriate place for both compression and expansion, so that the algorithms remain in sync. This is very tricky and may require alot of planning in order to get it working correctly. 
3. **Monitor mode** Initially do nothing (keep using the full codebook) but begin monitoring the *compression ratio* whenever you fill the codebook.   

Which mode should be used should be chosen by the program during compression. Whichever mode is used to compress a file should also be used to expand the file. The user is not required to state the mode to use for expansion. The mode used to compress a file will be stored at the beginning of the output file, so that it can be automatically retrieved during expansion. To establish the mode to be used during compression, the program will accept 3 command line arguments:  
		* "n" for Do Nothing mode  
		* "r" for Reset mode  
		* "m" for Monitor mode  
Note that the provided LZW code already accepts a command line argument to determine whether compression or expansion should be performed ("-" and "+", respectively), and that input/output files are provided via standard I/O redirection ("&lt;" to indicate an input file and "&gt;" to indicate an output file). 
To compress the file foo.txt to generate foo.lzw using Reset mode, you should be able to run:  
		```
		java MyLZW - r < foo.txt > foo.lzw
		```
		Similarly to expand foo.lzw into foo2.txt, you should run:  
		```
		java MyLZW + < foo.lzw > foo2.txt
		```


