/*************************************************************************
 *  Compilation:  javac LZW.java
 *  Execution:    java LZW - < input.txt   (compress)
 *  Execution:    java LZW + < input.txt   (expand)
 *  Dependencies: BinaryIn.java BinaryOut.java
 *
 *  Compress or expand binary input from standard input using LZW.
 *
 *  WARNING: STARTING WITH ORACLE JAVA 6, UPDATE 7 the SUBSTRING
 *  METHOD TAKES TIME AND SPACE LINEAR IN THE SIZE OF THE EXTRACTED
 *  SUBSTRING (INSTEAD OF CONSTANT SPACE AND TIME AS IN EARLIER
 *  IMPLEMENTATIONS).
 *
 *  See <a href = "http://java-performance.info/changes-to-string-java-1-7-0_06/">this article</a>
 *  for more details.
 *
 *************************************************************************/
import java.lang.*;
public class MyLZW {
    private static final int R = 256;        // number of input chars
    private static int W = 9;         // codeword width
    private static int L = (int)Math.pow(2,W);       // number of codewords = 2^W
    private static int mode =0;
    private static int count_u=0;
    private static int count_c=0;
    private static boolean isfull=false;
    private static boolean old =false;
    private static float old_ratio=0;
    private static float compression=0;

    public static void compress() { 
        String input = BinaryStdIn.readString();
        TST<Integer> st = new TST<Integer>();
        for (int i = 0; i < R; i++)
            st.put("" + (char) i, i);
        int code = R+1;  // R is codeword for EOF
        int k =257;
        if(mode == 0)                                 //do nothing mode
        {
            BinaryStdOut.write(mode, 3);  
        
            while (input.length() > 0) 
            {
                String s = st.longestPrefixOf(input);  // Find max prefix match s.
                BinaryStdOut.write(st.get(s), W);      // Print s's encoding.
                int t = s.length();
                if (t < input.length() && code < L)    // Add s to symbol table.

                {
                    st.put(input.substring(0, t + 1), code++);
                }
                
                           
                else if(t < input.length() && code == L) //if all width w codewords are full
                {
                    if(W < 16)
                    {
                        
                        W+=1;                           //increase w by 1
                        L = (int)Math.pow(2,W);
                        st.put(input.substring(0, t + 1), code++); //add code 
                        
                    }
                    
                }
                input = input.substring(t);           //scan past s in input 
            
            }
        }

        if(mode == 1)                               //reset mode

        {
           
            BinaryStdOut.write(mode,3);  
            while (input.length() > 0) 
            {
           
                String s = st.longestPrefixOf(input);  // Find max prefix match s.
                BinaryStdOut.write(st.get(s), W);      // Print s's encoding.
                int t = s.length();
              
                if (t < input.length() && code < L)    // Add s to symbol table.
                    st.put(input.substring(0, t + 1), code++);
                
                else if(t < input.length() && code == L)
                {
                    if(W < 16)
                    {
                        W+=1;
                        L = (int)Math.pow(2,W);
                        st.put(input.substring(0, t + 1), code++);
                    }
                    else if(W ==16)                  //if all 16 bit codewords are taken, reset the codebook
                    {
                        W = 9;                          // codeword width = 9
                        L = (int)Math.pow(2,W);  
                        st = new TST<Integer>();       
                        for (int i = 0; i < R; i++)
                        st.put("" + (char) i, i);
                        code = R+1;  
                        st.put(input.substring(0, t + 1), code++);
                    }
                    
                }
                input = input.substring(t);            // Scan past s in input.
                
            }
        }

        if(mode == 2)                                //monitor mode

        {
            BinaryStdOut.write(mode,3);  
            while (input.length() > 0) 
            {
            
                String s = st.longestPrefixOf(input);  // Find max prefix match s.
                BinaryStdOut.write(st.get(s), W);      // Print s's encoding.
                int t = s.length();

                count_u+=t;                            //uncompressed file length
                count_c+=W;                            //compressed file size
                
                if (t < input.length() && code < L)    // Add s to symbol table.
                    st.put(input.substring(0, t + 1), code++);
                
                else if(t < input.length() && code == L)
                {
                    if(W < 16)
                    {
                        W+=1;
                        L = (int)Math.pow(2,W);
                        st.put(input.substring(0, t + 1), code++);
                    }
                    else if(W ==16)                    //if all 16 bit codewords are taken, calculate compression ratio
                    {
                        if (old == false)
                        {
                            old_ratio = (float)(count_u*8)/(float)count_c;
                            old = true;
                        }
                        isfull = true;
                        
                    }
                    
                }
                if(isfull)
                {
                    
                    float new_ratio = (float)(count_u*8)/(float)count_c;  //new ratio
                    float compression = old_ratio/new_ratio;              //ratio of old and new ratios

                    if(compression > 1.1)                                 //if ratio > 1.1, reset
                    {
                        W = 9;         
                        L = (int)Math.pow(2,W);  
                        st = new TST<Integer>();
                        for (int i = 0; i < R; i++)
                            st.put("" + (char) i, i);
                        code = R+1;  
                        isfull = false;
                        old = false;
                        st.put(input.substring(0, t + 1), code++);

                    }

                }
                
                
                input = input.substring(t);
            }
        }

        BinaryStdOut.write(R, W);
        BinaryStdOut.close();
    } 


    public static void expand() {
         
        W = 9;
        L = (int)Math.pow(2,W);
        String[] st = new String[(int)Math.pow(2,16)];
        int i; // next available codeword value

        // initialize symbol table with all 1-character strings
        for (i = 0; i < R; i++)
            st[i] = "" + (char) i;
        st[i++] = "";                        // (unused) lookahead for EOF
        int k =257;
        mode = BinaryStdIn.readInt(3);
        if(mode ==0)                            //do nothing mode
        {

            int codeword = BinaryStdIn.readInt(W);
            if (codeword == R) return;           // expanded message is empty string
            String val = st[codeword];

            while (true) {
                
                BinaryStdOut.write(val);         //write value
                if( i == L)                      //if length w codewords are taken, increase w by 1
                {
                    if(W < 16)
                    {
                        W+=1;
                        L = (int)Math.pow(2,W);
                    }
                }
                codeword = BinaryStdIn.readInt(W);
                if (codeword == R) break;
                String s = st[codeword];
                if (i == codeword) s = val + val.charAt(0);   // special case hack
                if (i < L) st[i++] = val + s.charAt(0);
                System.err.println(val + s.charAt(0) +" "+ i);
                val = s;
            
            }
        }

        if(mode ==1)                    //reset mode
        {
            int codeword = BinaryStdIn.readInt(W);
            if (codeword == R) return;           // expanded message is empty string
            String val = st[codeword];

            while (true) {
                
                BinaryStdOut.write(val);
                if( i == L)
                {
                    if(W < 16)
                    {
                        W+=1;
                        L = (int)Math.pow(2,W);
                    }
                    else if(W == 16)                  //if all 16 bit code words are taken, reset codebook
                    {
                        W = 9;
                        L = (int)Math.pow(2,W);
                        st = new String[(int)Math.pow(2,16)];
                        i = 0 ;                                        
                        for (i = 0; i < R; i++)
                            st[i] = "" + (char) i;
                        st[i++] = "";                        // (unused) lookahead for EOF
                    }
                }
                codeword = BinaryStdIn.readInt(W);
                if (codeword == R) break;
                String s = st[codeword];
                if (i == codeword) s = val + val.charAt(0);   // special case hack
                if (i < L) st[i++] = val + s.charAt(0);
                val = s;
                
            }
        }

        if(mode ==2)                 //monitor mode
        {
            int codeword = BinaryStdIn.readInt(W);
            if (codeword == R) return;           // expanded message is empty string
            String val = st[codeword];

            while (true) {
                
                BinaryStdOut.write(val);
                int t = val.length();
                count_u+=t;
                count_c+=W;
                
                if( i == L)
                {
                    if(W < 16)
                    {
                        W+=1;
                        L = (int)Math.pow(2,W);
                    }
                    else if(W == 16)                    //if all 16 bit codes are taken, begin monitoring compression ratio
                    {
                        if (old == false)
                        {
                            old_ratio = (float)(count_u*8)/(float)count_c;
                            old = true;
                        }
                       isfull = true;  
                    }
                }
                if(isfull)
                {

                    float new_ratio = (float)(count_u*8)/(float)count_c;
                    float compression = old_ratio/new_ratio;

                     if(compression > 1.1)                    //if ratio exceeds 1.1, reset
                    {
                        W = 9;
                        L = (int)Math.pow(2,W);
                        st = new String[(int)Math.pow(2,16)];
                        i = 0 ; 
                        for (i = 0; i < R; i++)
                            st[i] = "" + (char) i;
                        st[i++] = "";  
                        isfull = false;
                        old = false;

                    }

                }

                codeword = BinaryStdIn.readInt(W);
                if (codeword == R) break;
                String s = st[codeword];
                if (i == codeword) s = val + val.charAt(0);   // special case hack
                if (i < L) st[i++] = val + s.charAt(0);
                val = s;
                
            }
        }
        BinaryStdOut.close();
    }



    public static void main(String[] args) 
    {
        //select mode
        if (args[0].equals("-"))
        {
            if(args[1].equals("n"))
                {mode = 0;
                compress();}
            else if (args[1].equals("r"))
                {mode = 1;
                compress();}
            else if (args[1].equals("m"))
                {mode =2;
                compress();}

            else throw new IllegalArgumentException("Illegal command line argument");

        }
        else if (args[0].equals("+")) 
        {
            expand();
        }

        else throw new IllegalArgumentException("Illegal command line argument");


    }

}
