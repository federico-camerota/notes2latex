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
    private final boolean has_title;

    TexBlock(String init, String end, boolean title){
        
        initial_block = init;
        end_block = end;
        has_title = title;
    }
    TexBlock(String init, boolean title){
    
        initial_block = init;
        has_title = title;
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
    boolean hasTitle(){
        return has_title;
    }
}
public class LatexInterpreter{


    private static final Map<String, TexBlock> TEX_BLOCKS = new HashMap<>();
    static {
    
        TEX_BLOCKS.put(";b;", new TexBlock("\\textbf{", "}", false));
        TEX_BLOCKS.put(";e;", new TexBlock("\\emph{", "}", false));
        TEX_BLOCKS.put(";t;", new TexBlock("\\texttt{", "}", false)); 
        TEX_BLOCKS.put(";it;", new TexBlock("\\begin{itemize}", "\\end{itemize}", false)); 
        TEX_BLOCKS.put(";it;", new TexBlock("\\begin{itemize}", "\\end{itemize}", false)); 
        TEX_BLOCKS.put(";en;", new TexBlock("\\begin{enumerate}", "\\end{enumerate}", false)); 
        TEX_BLOCKS.put(";i;", new TexBlock("\\item ", "", false)); 
        TEX_BLOCKS.put(";ch;", new TexBlock("\\chapter*{", "", true)); 
        TEX_BLOCKS.put(";s;", new TexBlock("\\section*{", "", true)); 
        TEX_BLOCKS.put(";ss;", new TexBlock("\\subsection*{", "", true)); 
        TEX_BLOCKS.put(";sss;", new TexBlock("\\subsubsection*{", "", true)); 
        TEX_BLOCKS.put(";p;", new TexBlock("\\paragraph*{", "", true)); 
        TEX_BLOCKS.put(";m;", new TexBlock("$", "$", false)); 
        TEX_BLOCKS.put(";mm;", new TexBlock("$$", "$$", false)); 
        TEX_BLOCKS.put(";tb;", new TexBlock("\\begin{tcolorbox}", "\\end{tcolorbox}", false)); 
        TEX_BLOCKS.put(";c;", new TexBlock("\\begin{center}", "\\end{center}", false)); 
    }
    private static final String BLOCK_ENDER = ";;";

    private Deque<String> end_stack = new LinkedList<>(); 

    public String interpret(String line){

        StringBuilder sb = new StringBuilder();
        Scanner sline = new Scanner(line);
        while (sline.hasNext()){
        
            String s = sline.next();
            TexBlock tb = TEX_BLOCKS.get(s);
            if (tb != null){
            
                sb.append(" " + tb.getInit());
                if (tb.hasEnder())
                    end_stack.offerFirst(tb.getEnd());
                if (tb.hasTitle())
                    sb.append(sline.nextLine().trim() + "}");
            }
            else{
                decomposeWord(s, sb);
            }
        }

        return sb.toString();
    }

    private void decomposeWord(String word, StringBuilder sb){
    
        int idx = word.indexOf(BLOCK_ENDER);
        if (idx == -1)
            if (word.length() == 1 && !Character.isLetterOrDigit(Character.codePointAt(word,0)))
               sb.append(word);
            else 
               sb.append(" " + word);
        else{
        
            decomposeWord(word.substring(0,idx),sb);
            sb.append(end_stack.pollFirst());
            decomposeWord(word.substring(idx +BLOCK_ENDER.length()), sb);
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
