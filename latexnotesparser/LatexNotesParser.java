//: latexinterpreter/LatexNotesParser.java

package latexnotesparser;

import java.util.Scanner;
import java.util.HashMap;
import java.util.Map;
import java.util.Deque;
import java.util.NoSuchElementException;
import java.util.LinkedList;
import java.io.File;
import java.io.FileNotFoundException;

public class LatexNotesParser{


    private static final BlockTypes TEX_BLOCKS = new BlockTypes();
    private static final String BLOCK_ENDER = ";;";

    private Deque<String> end_stack = new LinkedList<>(); 

    public String interpret(String line){

        StringBuilder sb = new StringBuilder();
        Scanner sline = new Scanner(line);
        while (sline.hasNext())
            decomposeWord(sline.next(), sb);
            
        return sb.toString();
    }

    private void decomposeWord(String word, StringBuilder sb){
    
        for (String block : TEX_BLOCKS.keySet()){
        
            // check if block is present in the string, if not pass to next block
            int bidx = word.indexOf(block);
            if (bidx == -1)
                continue;

            /*
             *If block is in the string we get the associated object
             *Then process the part of the string preceding the block
             *Add the block initializer and push into the enders stack the ender
             *Then process the part of the string that follow the block
             */
            TexBlock btex = TEX_BLOCKS.get(block);
            decomposeWord(word.substring(0,bidx), sb);
            sb.append(btex.getInit());
            if (btex.hasEnder())
                end_stack.offerFirst(btex.getEnd());
            decomposeWord(word.substring(bidx + block.length()), sb);
            //If a block is found the remaining parts of the string have already been 
            //processed so return
            return;
        }
        /* If the string cointains no block then we decompose using the
         * ender signal. (There are no more blocks in the string
         * if executing this part of the code)
         */
        decomposeByEnders(word, sb);// purpouse is to avoid searching blocks that we know are not there anymore.
    }

    private void decomposeByEnders(String word, StringBuilder sb){
    
        if (word.equals(""))
            return;
        //Serach the block ender in the word
        int idx = word.indexOf(BLOCK_ENDER);
        /* If not found, print the string, without a leading space if it is a single character 
         * that is not a letter or a number.
         * If found, first process
         */
        if (idx == -1)
            if (word.length() == 1 && !Character.isLetterOrDigit(Character.codePointAt(word,0)))
               sb.append(word);
            else 
               sb.append(" " + word);
        else{
         
            decomposeByEnders(word.substring(0,idx),sb);
            sb.append(end_stack.pollFirst());
            decomposeByEnders(word.substring(idx +BLOCK_ENDER.length()), sb);
        } 
    }
}
