//: latexinterpreter/LatexInterpreter.java

import java.util.Scanner;
import java.util.HashMap;
import java.util.Map;
import java.util.Deque;
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
        
        return (end_block != "");
    }
}
public class LatexInterpreter{


    private static final Map<String, TexBlock> TEX_BLOCKS = new HashMap<>();
    static {
    
        TEX_BLOCKS.put(";b;", new TexBlock("\\textbf{", "}"));
        TEX_BLOCKS.put(";e;", new TexBlock("\\emph{", "}"));
        TEX_BLOCKS.put(";t;", new TexBlock("\\texttt{", "}")); 
        TEX_BLOCKS.put(";it;", new TexBlock("\\begin{itemize}", "\\end{itemize}")); 
        TEX_BLOCKS.put(";it;", new TexBlock("\\begin{itemize}", "\\end{itemize}")); 
        TEX_BLOCKS.put(";en;", new TexBlock("\\begin{enumerate}", "\\end{enumerate}")); 
        TEX_BLOCKS.put(";i;", new TexBlock("\\item ", "")); 
        TEX_BLOCKS.put(";ch;", new TexBlock("\\chapter*{", "}")); 
        TEX_BLOCKS.put(";s;", new TexBlock("\\section*{", "}")); 
        TEX_BLOCKS.put(";ss;", new TexBlock("\\subsection*{", "}")); 
        TEX_BLOCKS.put(";sss;", new TexBlock("\\subsubsection*{", "}")); 
        TEX_BLOCKS.put(";p;", new TexBlock("\\paragraph*{", "}")); 
        TEX_BLOCKS.put(";m;", new TexBlock("$", "$")); 
        TEX_BLOCKS.put(";mm;", new TexBlock("$$", "$$")); 
        TEX_BLOCKS.put(";tb;", new TexBlock("\\begin{tcolorbox}", "\\end{tcolorbox}")); 
        TEX_BLOCKS.put(";c;", new TexBlock("\\begin{center}", "\\end{center}")); 
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
    public static void main(String[] args){
    

        if (args.length != 1){
            System.err.println("usage: <input_file_name>");
            return;
        }

        LatexInterpreter li = new LatexInterpreter();
        try{
            Scanner fscan = new Scanner( new File(args[0]));
            while (fscan.hasNext())
                System.out.println(li.interpret(fscan.nextLine()));
        }
        catch (FileNotFoundException e){
        
            System.err.println(args[0] + " is not a valid file name.");
        }
    }
}
