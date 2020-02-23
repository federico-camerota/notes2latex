import latexnotesparser.LatexNotesParser;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

public class Notes2Latex{

    public static void main(String[] args){
    

        if (args.length != 1){
            System.err.println("usage: <input_file_name>");
            return;
        }

        LatexNotesParser li = new LatexNotesParser();
        try{
            Scanner fscan = new Scanner( new File(args[0]));
            while (fscan.hasNext())
                System.out.println(li.interpret(fscan.nextLine()));
        }
        catch (FileNotFoundException e){
        
            System.err.println(args[0] + " is not a valid file name.");
            System.exit(3);
        }
    }
}
