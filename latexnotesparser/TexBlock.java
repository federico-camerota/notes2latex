//: latexnotesparser/TextBlock.java

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
