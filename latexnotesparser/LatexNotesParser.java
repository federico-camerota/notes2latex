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

class TexBlock{
    
    private final String initial_block;
    private final String end_block;

    TexBlock(String init, String end){
        
        initial_block = init;
        end_block = end;
    }
    TexBlock(String init){
    
        initial_block = init;
        end_block = "";
    }

    String getInit(){
    
        return initial_block;
    }
    String getEnd(){
    
        return end_block;
    }
    boolean hasEnder(){
        
        return !end_block.equals("");
    }
}
public class LatexNotesParser{


    private static final Map<String, TexBlock> TEX_BLOCKS = new HashMap<>();
    private static final String CONFIG_FILENAME = System.getenv("HOME") + "/.notes2latex";
    static {
    
        try{
        Scanner s = new Scanner(new File(CONFIG_FILENAME));
        while (s.hasNext()){
        
            Scanner sline = (new Scanner(s.nextLine())).useDelimiter("::");
            try{
                TEX_BLOCKS.put(sline.next(), new TexBlock(sline.next(), sline.next()));
        }
        catch(NoSuchElementException e){
        
            System.err.println("error: invalid format in config file");
            System.exit(2);
        }
        }
        }
        catch(FileNotFoundException e){
        
            System.err.println("error: config file not found");
            System.exit(1);
        }

    }
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
