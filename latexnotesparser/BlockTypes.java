import java.util.Scanner;

class BlockTypes extends HashMap<String, TexBlock>{

    public BlockTypes(){
        init(this);
    }

    private static final String CONFIG_FILENAME = System.getenv("HOME") + "/.notes2latex";

    private static init(BlockTypes bt){

        try{
            Scanner s = new Scanner(new File(CONFIG_FILENAME));
            while (s.hasNext()){

                Scanner sline = (new Scanner(s.nextLine())).useDelimiter("::");
                try{
                    bt.put(sline.next(), new TexBlock(sline.next(), sline.next()));
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

}
