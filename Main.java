import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Main {

    public static int argsCount = 0;

    public static List<String> WordBank = new ArrayList<String>();
    public static Map<String,String> balls = new HashMap<String, String>();
    public static List<String> AllBank = new ArrayList<String>();
    public static List<String> EqualBank = new ArrayList<String>();
    public static List<String> numBank = new ArrayList<String>();
    public static List<Token> tokenArray = new ArrayList<Token>();
    public static List<String> output4 = new ArrayList<String>();
    public static List<Quads> myQuads = new ArrayList<Quads>();
    public static Token curr;
    static int count, token;
    public static int functionCount;
    public static int roundStack = 0;
    public static int curlyStack = 0;
    public static int squareStack = 0;

    public static int tCount = 0;
    public static int uniTempcount = 0;
    public static int lineCount22 = 1;
    public static String loopType = "none";
    public static String comparetype = "none";

    //static Token next = null;


    public static void main(String[] args) throws FileNotFoundException {

        int count = 0;
        List<String> list = new ArrayList<String>();
        File library = new File(args[0]);
        Scanner sc = new Scanner(library);
        // new
        while(sc.hasNextLine()) {
            String line;
            line = sc.nextLine();
            list.add(line);
        }



        tokenArray = lexiconParser(list);

        System.out.println("/////////////////////////////////////////////////////////////////////");



        token = 0;
        Token toke = new Token("$");
        toke.officialType = "$";
        toke.token = "$";
        toke.type = "dollar Sign!!!!";
        tokenArray.add(toke);


        curr = tokenArray.get(0);

        System.out.println("////////////////////////////////////////////////////////////////////");
        System.out.println("Start: " + curr.officialType);
        Program();
        System.out.println("////////////////////////////////////////////////////////////////////");


        ScopeAnalysis(); //given scope names and depth levels
        MainMethods();   //main methods are counted, methods are given types and
        IDtypes(); //gives declarations and IDs int or float types
        ArrayAnalysis();
        //Mathematics();
        returns();
        proj3params();
        System.out.println("Project 3 Success!");
        List<Token> debugshortcut = tokenArray;
        System.out.println("//////////////////////////////////////////////////////////////////////");

        proj4();

        System.out.println("Project 4 Success!");
    }


    //<editor-fold desc="Project 4">
    private static void proj4() {
        List<Token> debugshortcut = tokenArray;
        String compy = "";
        String Funcname = "";

        int tempCount = 0;

        //Quads( int Linecount, String op, String opnd1, String opnd2, String Result){
        for(int i = 0; i < tokenArray.size(); i++){
            //  Token iDebugView = tokenArray.get(i);
            //functions
            if(tokenArray.get(i).officialType.equals("func")){
                // System.out.println(lineCount + "\t\t\t" + tokenArray.get(i).type + "\t\t\t" + tokenArray.get(i).token);
                String result = String.valueOf(tokenArray.get(i).ParameterCount);
                myQuads.add(new Quads(lineCount22, tokenArray.get(i).officialType,tokenArray.get(i).type, tokenArray.get(i).token, result));
                lineCount22++;
                Funcname = tokenArray.get(i).token;
                int f = 0;
                //PRINTS PARAMS
                while(f < tokenArray.get(i).ParameterCount){
                    myQuads.add(new Quads(lineCount22, "param","","",""));
                    f++;
                    lineCount22++;
                }
            }

            //declarations
            if(tokenArray.get(i).officialType.equals("dec")){
                //System.out.println(lineCount + "\t\t\t" + tokenArray.get(i).type + "\t\t\t" + tokenArray.get(i).token);
                int foo = 1;
                if(tokenArray.get(i).type.equals("intArray") || tokenArray.get(i).type.equals("floatArray")){
                    if(!tokenArray.get(i+2).officialType.equals("RBRACK")){
                        foo = Integer.parseInt(tokenArray.get(i+2).token);
                    }
                }
                foo = foo*4;
                String Alloc =  Integer.toString(foo);
                myQuads.add(new Quads(lineCount22, "Alloc",Alloc, "", tokenArray.get(i).token));
                lineCount22++;
            }

            //returns
            if(tokenArray.get(i).officialType.equals("return")){
                myQuads.add(new Quads(lineCount22, tokenArray.get(i).officialType,"","",tokenArray.get(i+1).token));
                lineCount22++;
            }

            //function ends
            if(tokenArray.get(i).changemarker == true && tokenArray.get(i).officialType.equals("RBRACE")){
                myQuads.add(new Quads(lineCount22, "end","func",Funcname,""));
                lineCount22++;
            }

            //math start
            //MAYBE CHANGE THE SECOND PART HERE?!

            if((tokenArray.get(i).officialType.equals("ID") || tokenArray.get(i).officialType.equals("funcCall"))){
                if(tokenArray.get(i).officialType.equals("SEMICOLON")){
                    //continue;
                }
                List<Token> Mathlist = new ArrayList<Token>();
                //Mathlist.add(tokenArray.get(i-1));
                int jay = 0;
                for(int j = i; !tokenArray.get(j).officialType.equals("SEMICOLON"); j++){
                    Mathlist.add(tokenArray.get(j));
                    jay = j;
                    //i = j;
                }
                i=jay;
                Math4(Mathlist);
            }




            if((tokenArray.get(i).officialType.equals("if"))){
                int branchstart = lineCount22;
                loopType = "if";
                i=i+1;
                List<Token> Mathlist = new ArrayList<Token>();
                //Mathlist.add(tokenArray.get(i-1));
                int jay = 0;
                for(int j = i; !tokenArray.get(j).officialType.equals("LBRACE"); j++){
                    Mathlist.add(tokenArray.get(j));
                    jay = j;
                    //i = j;
                }


                i=jay;
                compy = Math4(Mathlist);
                int curlydeep = tokenArray.get(i+1).curlydepthlevel;
                List<Token> whileList = new ArrayList<Token>();
                for(int j = i+1; j < tokenArray.size(); j++){
                    if(curlydeep > tokenArray.get(j).curlydepthlevel){
                        break;
                    }
                    whileList.add(tokenArray.get(j));
                    jay = j;
                    //i = j;
                }
                String branch = Integer.toString(branchstart);
                i=jay;

                while4(whileList);

                myQuads.add(new Quads(lineCount22, "end", "block", "", ""));

                for(int y = myQuads.size()-1; y >=0; y--){
                    String bpcount = Integer.toString(lineCount22+2);
                    if(myQuads.get(y).isBP){
                        myQuads.get(y).Result = bpcount;
                        myQuads.get(y).isBP = false;
                        break;
                    }
                }


                lineCount22++;
                //myQuads.add(new Quads(lineCount22, "BR", "", "", branch));
                //lineCount22++;
            }

            if((tokenArray.get(i).officialType.equals("else"))){
                if(tokenArray.get(i+1).officialType.equals("if")){
                    continue;
                }
                 System.out.println("compy");
                int branchstart = lineCount22;
                loopType = "else";
                i=i+1;
                List<Token> Mathlist = new ArrayList<Token>();
                //Mathlist.add(tokenArray.get(i-1));
                int jay = i;
                i=jay;
                int curlydeep = tokenArray.get(i+1).curlydepthlevel;
                List<Token> whileList = new ArrayList<Token>();
                for(int j = i+1; j < tokenArray.size(); j++){
                    if(curlydeep > tokenArray.get(j).curlydepthlevel){
                        break;
                    }
                    whileList.add(tokenArray.get(j));
                    jay = j;
                    //i = j;
                }
                String branch = Integer.toString(branchstart);
                i=jay;

                if(compy.equals("BRGT")){
                    compy = "BRLEQ";
                }
                if(compy.equals("BRGEQ")){
                    compy = "BRLT";
                }
                if(compy.equals("BRLT")){
                    compy = "BRGEQ";
                }
                if(compy.equals("BRLEQ")){
                    compy = "BRGT";
                }
                if(compy.equals("BREQ")){
                    compy = "BRNEQ";
                }
                if(compy.equals("BRNEQ")){
                    compy = "BREQ";
                }



                myQuads.add(new Quads(lineCount22,compy,"","","-1"));
                int quadsize = myQuads.size();
                lineCount22++;
                myQuads.add(new Quads(lineCount22,"Block","","",""));
                lineCount22++;
                while4(whileList);

                myQuads.get(quadsize-1).Result = Integer.toString(lineCount22);
                myQuads.add(new Quads(lineCount22, "end", "block", "", ""));

                for(int y = myQuads.size()-1; y >=0; y--){
                    String bpcount = Integer.toString(lineCount22+2);
                    if(myQuads.get(y).isBP){
                        myQuads.get(y).Result = bpcount;
                        myQuads.get(y).isBP = false;
                        break;
                    }
                }
                lineCount22++;
            }

            if((tokenArray.get(i).officialType.equals("while"))){



                int branchstart = lineCount22;
                loopType = "while";
                i=i+1;
                List<Token> Mathlist = new ArrayList<Token>();
                //Mathlist.add(tokenArray.get(i-1));
                int jay = 0;
                for(int j = i; !tokenArray.get(j).officialType.equals("LBRACE"); j++){
                    Mathlist.add(tokenArray.get(j));
                    jay = j;
                    //i = j;
                }
                i=jay;
                Math4(Mathlist);
                int curlydeep = tokenArray.get(i+1).curlydepthlevel;
                List<Token> whileList = new ArrayList<Token>();
                for(int j = i+1; j < tokenArray.size(); j++){
                    if(curlydeep > tokenArray.get(j).curlydepthlevel){
                        break;
                    }
                    whileList.add(tokenArray.get(j));
                    jay = j;
                    //i = j;
                }
                String branch = Integer.toString(branchstart);
                i=jay;

                while4(whileList);

                myQuads.add(new Quads(lineCount22, "end", "block", "", ""));

                for(int y = myQuads.size()-1; y >=0; y--){
                    String bpcount = Integer.toString(lineCount22+2);
                    if(myQuads.get(y).isBP){
                        myQuads.get(y).Result = bpcount;
                        myQuads.get(y).isBP = false;
                        break;
                    }
                }

                lineCount22++;
                myQuads.add(new Quads(lineCount22, "BR", "", "", branch));
                lineCount22++;
            }
        }


        //OP OPND1 OPND2 Result
        myQuads.add(new Quads(lineCount22,"","","",""));
        System.out.println("\t\t\tOP\t\t\tOPND1\t\t\tOPND2\t\t\tResult");
        for(int i = 0; i<myQuads.size(); i++){
            if(myQuads.get(i).op.equals("PLUS")){
                myQuads.get(i).op = "add";
            }
            if(myQuads.get(i).op.equals("TIMES")){
                myQuads.get(i).op = "mult";
            }
            if(myQuads.get(i).op.equals("MINUS")){
                myQuads.get(i).op = "sub";
            }
            if(myQuads.get(i).op.equals("DIVIDE")){
                myQuads.get(i).op = "div";
            }

            System.out.println(myQuads.get(i).Linecount + "\t\t\t" + myQuads.get(i).op + "\t\t\t" + myQuads.get(i).opnd1 + "\t\t\t" + myQuads.get(i).opnd2 + "\t\t\t" +  myQuads.get(i).Result);
        }



    }


    public static void while4(List<Token> whileList){
        String compy = "";
        List<Token> debugshortcut = whileList;

        String Funcname = "";

        int tempCount = 0;

        //Quads( int Linecount, String op, String opnd1, String opnd2, String Result){
        for(int i = 0; i < whileList.size(); i++){
            //  Token iDebugView = whileList.get(i);
            //functions
            if(whileList.get(i).officialType.equals("func")){
                // System.out.println(lineCount + "\t\t\t" + whileList.get(i).type + "\t\t\t" + whileList.get(i).token);
                String result = String.valueOf(whileList.get(i).ParameterCount);
                myQuads.add(new Quads(lineCount22, whileList.get(i).officialType,whileList.get(i).type, whileList.get(i).token, result));
                lineCount22++;
                Funcname = whileList.get(i).token;
                int f = 0;
                //PRINTS PARAMS
                while(f < whileList.get(i).ParameterCount){
                    myQuads.add(new Quads(lineCount22, "param","","",""));
                    f++;
                    lineCount22++;
                }
            }

            //declarations
            if(whileList.get(i).officialType.equals("dec")){
                //System.out.println(lineCount + "\t\t\t" + whileList.get(i).type + "\t\t\t" + whileList.get(i).token);
                int foo = 1;
                if(whileList.get(i).type.equals("intArray") || whileList.get(i).type.equals("floatArray")){
                    if(!whileList.get(i+2).officialType.equals("RBRACK")){
                        foo = Integer.parseInt(whileList.get(i+2).token);
                    }
                }
                foo = foo*4;
                String Alloc =  Integer.toString(foo);
                myQuads.add(new Quads(lineCount22, "Alloc",Alloc, "", whileList.get(i).token));
                lineCount22++;
            }

            //returns
            if(whileList.get(i).officialType.equals("return")){
                myQuads.add(new Quads(lineCount22, whileList.get(i).officialType,"","",whileList.get(i+1).token));
                lineCount22++;
            }

            //function ends
            if(whileList.get(i).changemarker == true && whileList.get(i).officialType.equals("RBRACE")){
                myQuads.add(new Quads(lineCount22, "end","func",Funcname,""));
                lineCount22++;
            }

            //math start
            //MAYBE CHANGE THE SECOND PART HERE?!

            if((whileList.get(i).officialType.equals("ID") || whileList.get(i).officialType.equals("funcCall"))){
                if(whileList.get(i).officialType.equals("SEMICOLON")){
                   // continue;
                }
                List<Token> Mathlist = new ArrayList<Token>();
                //Mathlist.add(whileList.get(i-1));
                int jay = 0;
                for(int j = i; !whileList.get(j).officialType.equals("SEMICOLON"); j++){
                    Mathlist.add(whileList.get(j));
                    jay = j;
                    //i = j;
                }
                i=jay;
                Math4(Mathlist);
            }


            if((whileList.get(i).officialType.equals("if"))){
                int branchstart = lineCount22;
                loopType = "if";
                i=i+1;
                List<Token> Mathlist = new ArrayList<Token>();
                //Mathlist.add(tokenArray.get(i-1));
                int jay = 0;
                for(int j = i; !whileList.get(j).officialType.equals("LBRACE"); j++){
                    Mathlist.add(whileList.get(j));
                    jay = j;
                    //i = j;
                }
                i=jay;
                compy = Math4(Mathlist);
                int curlydeep = whileList.get(i+1).curlydepthlevel;
                List<Token> innerwhileList = new ArrayList<Token>();
                for(int j = i+1; j < whileList.size(); j++){
                    if(curlydeep > whileList.get(j).curlydepthlevel){
                        break;
                    }
                    innerwhileList.add(whileList.get(j));
                    jay = j;
                    //i = j;
                }
                String branch = Integer.toString(branchstart);
                i=jay;

                while4(innerwhileList);

                myQuads.add(new Quads(lineCount22, "end", "block", "", ""));

                for(int y = myQuads.size()-1; y >=0; y--){
                    String bpcount = Integer.toString(lineCount22+2);
                    if(myQuads.get(y).isBP){
                        myQuads.get(y).Result = bpcount;
                        myQuads.get(y).isBP = false;
                        break;
                    }
                }

                lineCount22++;
                //myQuads.add(new Quads(lineCount22, "BR", "", "", branch));
                //lineCount22++;
            }


            if((whileList.get(i).officialType.equals("else"))){
                if(whileList.get(i+1).officialType.equals("if")){
                    continue;
                }
                System.out.println("compy");
                int branchstart = lineCount22;
                loopType = "else";
                i=i+1;
                List<Token> Mathlist = new ArrayList<Token>();
                //Mathlist.add(tokenArray.get(i-1));
                int jay = i;
                i=jay;
                int curlydeep = whileList.get(i+1).curlydepthlevel;
                List<Token> innerwhileList = new ArrayList<Token>();
                for(int j = i+1; j < whileList.size(); j++){
                    if(curlydeep > whileList.get(j).curlydepthlevel){
                        break;
                    }
                    innerwhileList.add(whileList.get(j));
                    jay = j;
                    //i = j;
                }
                String branch = Integer.toString(branchstart);
                i=jay;

                if(compy.equals("BRGT")){
                    compy = "BRLEQ";
                }
                if(compy.equals("BRGEQ")){
                    compy = "BRLT";
                }
                if(compy.equals("BRLT")){
                    compy = "BRGEQ";
                }
                if(compy.equals("BRLEQ")){
                    compy = "BRGT";
                }
                if(compy.equals("BREQ")){
                    compy = "BRNEQ";
                }
                if(compy.equals("BRNEQ")){
                    compy = "BREQ";
                }


                myQuads.add(new Quads(lineCount22,compy,"","","-1"));
                int quadsize = myQuads.size();
                lineCount22++;
                myQuads.add(new Quads(lineCount22,"Block","","",""));
                lineCount22++;
                while4(innerwhileList);

                myQuads.get(quadsize-1).Result = Integer.toString(lineCount22);
                myQuads.add(new Quads(lineCount22, "end", "block", "", ""));

                for(int y = myQuads.size()-1; y >=0; y--){
                    String bpcount = Integer.toString(lineCount22+2);
                    if(myQuads.get(y).isBP){
                        myQuads.get(y).Result = bpcount;
                        myQuads.get(y).isBP = false;
                        break;
                    }
                }
                lineCount22++;
            }



            if((whileList.get(i).officialType.equals("while"))){
                int branchstart = lineCount22;
                loopType = "while";
                i=i+1;
                List<Token> Mathlist = new ArrayList<Token>();
                //Mathlist.add(whileList.get(i-1));
                int jay = 0;
                for(int j = i; !whileList.get(j).officialType.equals("LBRACE"); j++){
                    Mathlist.add(whileList.get(j));
                    jay = j;
                    //i = j;
                }
                i=jay;
                Math4(Mathlist);
                int curlydeep = whileList.get(i+1).curlydepthlevel;
                List<Token> Innerwhileloop = new ArrayList<Token>();
                for(int j = i+1; j < whileList.size(); j++){
                    if(curlydeep > whileList.get(j).curlydepthlevel){
                        break;
                    }
                    Innerwhileloop.add(whileList.get(j));
                    jay = j;
                    //i = j;
                }
                i=jay;
                String br =  Integer.toString(branchstart);
                while4(Innerwhileloop);
                myQuads.add(new Quads(lineCount22, "end","block","",""));
                for(int y = myQuads.size()-1; y >=0; y--){
                    String bpcount = Integer.toString(lineCount22 +2);
                    if(myQuads.get(y).isBP){
                        myQuads.get(y).Result = bpcount;
                        myQuads.get(y).isBP = false;
                        break;
                    }
                }

                lineCount22++;
                myQuads.add(new Quads(lineCount22, "BR", "", "", br));
                lineCount22++;
            }


        }

    }

    private static String Math4(List<Token> mathlist){
        String compy = "";
        List<Quads> quadmath = new ArrayList<Quads>();
        int maxDepth = 0;
        int DepthName = 0;
        int argcount = 0;
        Token tail = new Token("ffds");
        tail.token = "no!!";
        tail.type = "asads";
        tail.funcType = "nomorenulls";
        mathlist.add(tail);
        mathlist.add(tail);

        for(int i = 0; i < mathlist.size(); i++){
            if(mathlist.get(i).epicdepthlevel > maxDepth){
                maxDepth = mathlist.get(i).epicdepthlevel;
            }
        }


        Stack<Integer> nameStack = new Stack<Integer>();
        Stack<Integer> recordStack = new Stack<Integer>();
        nameStack.push(0);
        recordStack.push(0);
        int maxName = 0;
        int scopeStack = 0;
        for(int i = 0; i < mathlist.size(); i++){
            //scopeStack = 0;
            if(mathlist.get(i).officialType.equals("LPAREN") || mathlist.get(i).officialType.equals("LBRACK")){
                while(recordStack.contains(scopeStack)){
                    scopeStack++;
                }
                nameStack.push(scopeStack);
                recordStack.push(scopeStack);
            }
            if(mathlist.get(i).officialType.equals("RPAREN") || mathlist.get(i).officialType.equals("RBRACK")) {
                nameStack.pop();
                if(!nameStack.isEmpty()){
                    scopeStack = nameStack.peek();
                }
            }
            mathlist.get(i).depthName = scopeStack;
            if(scopeStack > maxName){
                maxName = scopeStack;
            }
        }


        System.out.println("remove parens");
        for(int i = maxDepth; i>=0; i--){
            //remove parens and brackets
            //NO DEPTH LEVEL
            for(int j = 1; j < mathlist.size()-1; j++){
                if(mathlist.get(j).isT || mathlist.get(j).officialType.equals("ID") || mathlist.get(j).officialType.equals("NUM")){
                    if( !mathlist.get(j-1).officialType.equals("funcCall") && ((mathlist.get(j-1).officialType.equals("LPAREN") && mathlist.get(j+1).officialType.equals("RPAREN"))
                            || (mathlist.get(j-1).officialType.equals("LBRACK") && mathlist.get(j+1).officialType.equals("RBRACK"))) ){

                        //   if(j >= 2){
                        //    if(!mathlist.get(j-2).officialType.equals("funcCall") ) {
                        mathlist.remove(j-1);
                        mathlist.remove(j);
                        j= j-1;
                        //    }
                        //  }

                    }
                }
            }

            for(int j = 1; j < mathlist.size()-1; j++){

            }



            System.out.println("arrays");
            //function calls and array indexes
            //NO DEPTH LEVEL
            for(int j = 1; j < mathlist.size(); j++){
                if((mathlist.get(j).isT || mathlist.get(j).officialType.equals("ID") || mathlist.get(j).officialType.equals("NUM") || mathlist.get(j).isT)
                        && (mathlist.get(j-1).type.equals("intArray") || mathlist.get(j-1).type.equals("floatArray"))) {

                    //COME BACK TO THIS WHEN YOUR BRAIN WORKS
                    if(mathlist.get(j-1).type.equals("intArray") || mathlist.get(j-1).type.equals("floatArray")){
                        String rresult = Integer.toString(uniTempcount);
                        myQuads.add(new Quads(lineCount22,"TIMES",mathlist.get(j).token,"4","_t" + rresult));
                        uniTempcount++;
                        lineCount22++;
                        String now = rresult;
                        rresult = Integer.toString(uniTempcount);
                        myQuads.add(new Quads(lineCount22,"DISP",mathlist.get(j-1).token, "_t" + now,"_t" + rresult));
                        lineCount22++;
                        //uniTempcount++;
                        //RPAREN is wrong
                    }
//                    if(mathlist.get(j-1).officialType.equals("funcCall") && (mathlist.get(j+1).officialType.equals("RPAREN"))){
//
//                        String paramcountstring = Integer.toString(mathlist.get(j-1).ParameterCount);
//                        String rresult = Integer.toString(uniTempcount);
//                        mathlist.get(j-1).officialType = "called";
//                        myQuads.add(new Quads(lineCount22,"call",mathlist.get(j-1).token,paramcountstring,"_t" + rresult));
//
//                        // myQuads.add(new Quads(lineCount22,"call",mathlist.get(j-1).officialType,paramcountstring,))
//                    }


                    // int Linecount, String op, String opnd1, String opnd2, String Result
                    // myQuads.add(new Quads());
                    mathlist.get(j-1).token = "_t" + uniTempcount;
                    uniTempcount++;

                    mathlist.remove(j);
                    j = j-1;
                }
            }


            //merge calls
            int aCount = 0;
            System.out.println("A");
            for(int j = 1; j < mathlist.size()-1; j++){
                if(     (mathlist.get(j-1).officialType.equals("LPAREN") && mathlist.get(j+1).officialType.equals("COMMA"))
                        || (mathlist.get(j-1).officialType.equals("COMMA") && mathlist.get(j+1).officialType.equals("RPAREN"))
                        || (mathlist.get(j-1).officialType.equals("COMMA") && mathlist.get(j+1).officialType.equals("COMMA"))
                        ){
                    //System.out.println("fred fuchs");


                    //myQuads.add(new Quads(lineCount22,"arg","","",mathlist.get(j).token));
                    mathlist.get(j).isA = true;
                    //j = j-1;
                }
            }
            System.out.println("functionn calls");
            for(int j = 0; j < mathlist.size();j++){
                if(mathlist.get(j).officialType.equals("funcCall") && !mathlist.get(j).isT){
                    if(mathlist.get(j+2).officialType.equals("RPAREN")){
                        String rresult = Integer.toString(uniTempcount);
                        String parrcount = Integer.toString(mathlist.get(j).ParameterCount);
                        myQuads.add(new Quads(lineCount22,"call",mathlist.get(j).token,parrcount,"_t" + rresult ));
                        mathlist.get(j).token = "_t" + rresult;
                        mathlist.get(j).isT = true;
                        uniTempcount++;
                        lineCount22++;
                        mathlist.remove(j+2);
                        mathlist.remove(j+1);
                        continue;
                    }
                    boolean allClear = true;
                    int dName = mathlist.get(j+1).depthName;
                    int aich = 0;
                    for(int h = j+1; h < mathlist.size()-1; h++){
                        if(dName > mathlist.get(h).depthName){
                            break;
                        }
                        if(mathlist.get(h).officialType.equals("TIMES")){
                            //System.out.println("scie");
                        }

                        if(mathlist.get(h).depthName == dName
                                && (((mathlist.get(h).officialType.equals("LPAREN") && !mathlist.get(h+1).isA))
                                || ((mathlist.get(h).officialType.equals("COMMA") && (!mathlist.get(h-1).isA || !mathlist.get(h+1).isA))) )
                                ){
                            allClear = false;
                        }
                        aich = h;
                    }
                    if(allClear){
                        System.out.println("praise yeezus");
                        int argscount = 0;
                        for(int k = j+1; k < mathlist.size()-1; k++){
                            if(dName > mathlist.get(k).depthName){
                                break;
                            }

                            if(mathlist.get(k).isA){
                                myQuads.add(new Quads(lineCount22,"Args","","",mathlist.get(k).token ));
                                lineCount22++;
                                argscount++;
                            }

                        }
                        int killme = 0;
                        for(int h = j+1; h < mathlist.size()-1; h++){
                            mathlist.remove(h);
                            killme++;
                            if(dName > mathlist.get(h).depthName){
                                if(killme != 1){
                                    mathlist.remove(h);
                                }
                                break;
                            }
                            h=h-1;
                        }


                        String shabba = Integer.toString(mathlist.get(j).ParameterCount);
                        if(argscount == 0){
                            myQuads.add(new Quads(lineCount22,"Args","","","_t"+uniTempcount));
                            uniTempcount++;
                        }
                        String rresult = Integer.toString(uniTempcount);
                        myQuads.add(new Quads(lineCount22, "call", mathlist.get(j).token, shabba, "_t" + rresult));
                        mathlist.get(j).token = "_t" + rresult;
                        mathlist.get(j).isT = true;
                        uniTempcount++;
                        lineCount22++;
                    }
                }

            }

            System.out.println("multiply/divide");
            for(int j = 0; j < mathlist.size();j++){
                Token debch = mathlist.get(j);
                //multiply/divide
                if(mathlist.get(j).epicdepthlevel == i && (mathlist.get(j).officialType.equals("TIMES") || mathlist.get(j).officialType.equals("DIVIDE") )){
                    mathlist.get(j).token = "_t" + uniTempcount;
                    myQuads.add(new Quads(lineCount22,mathlist.get(j).officialType,mathlist.get(j-1).token,mathlist.get(j+1).token,mathlist.get(j).token ));
                    lineCount22++;
                    mathlist.get(j).isT = true;
                    uniTempcount++;
                    mathlist.remove(j-1);
                    mathlist.remove(j);
                    j = j-1;
                }
            }

            System.out.println("addition/subtraction");
            //add/subtract
            for(int j = 0; j < mathlist.size();j++){
                Token debch = mathlist.get(j);
                if(mathlist.get(j).epicdepthlevel == i && (mathlist.get(j).officialType.equals("PLUS") || mathlist.get(j).officialType.equals("MINUS") )){
                    mathlist.get(j).token = "_t" + uniTempcount;
                    myQuads.add(new Quads(lineCount22,mathlist.get(j).officialType,mathlist.get(j-1).token,mathlist.get(j+1).token,mathlist.get(j).token ));
                    mathlist.get(j).isT = true;
                    lineCount22++;
                    uniTempcount++;
                    mathlist.remove(j-1);
                    mathlist.remove(j);
                    j = j-1;
                }
            }



            //MUST BE LAST
            System.out.println("equal sign");
            for(int j = 0; j < mathlist.size();j++){
                Token debch = mathlist.get(j);
                if(mathlist.get(j).epicdepthlevel == i && (mathlist.get(j).officialType.equals("EQ"))){
                    mathlist.get(j).token = "_t" + uniTempcount;
                    myQuads.add(new Quads(lineCount22,"ASSIGN",mathlist.get(j+1).token,"",mathlist.get(j-1).token ));
                    lineCount22++;
                    mathlist.get(j).isT = true;
                    //uniTempcount++;
                    mathlist.remove(j-1);
                    mathlist.remove(j);
                    j = j-1;
                }
            }
            //<editor-fold desc="comparisons">
            System.out.println("comparison");
            for(int j = 0; j < mathlist.size();j++){
                Token debch = mathlist.get(j);
                if(mathlist.get(j).epicdepthlevel == i
                        && (mathlist.get(j).officialType.equals("LT") || mathlist.get(j).officialType.equals("LTE") || mathlist.get(j).officialType.equals("GT")
                        ||mathlist.get(j).officialType.equals("GTE") ||mathlist.get(j).officialType.equals("DOUBLEEQ") || mathlist.get(j).officialType.equals("NOTEQ"))){
                    mathlist.get(j).token = "_t" + uniTempcount;
                    myQuads.add(new Quads(lineCount22,"compare",mathlist.get(j-1).token,mathlist.get(j+1).token,mathlist.get(j).token ));
                    lineCount22++;
                    mathlist.get(j).isT = true;
                    uniTempcount++;

                    String ffffff = Integer.toString(lineCount22 + 2);
                    String compareName = "";

                     compy = comparisons(mathlist, j, ffffff, compareName);

                    mathlist.remove(j-1);
                    mathlist.remove(j);
                    j = j-1;
                }
            }

            System.out.println("end loop");
        }



        System.out.println("final");

        return compy;
    }

    private static String comparisons(List<Token> mathlist, int j, String ffffff, String compareName) {
        if(mathlist.get(j).officialType.equals("GT")){


                compareName = "BRLEQ";
            comparetype = compareName;
                ffffff = Integer.toString(lineCount22 + 2);

            Quads brQuad = new Quads(lineCount22, compareName,mathlist.get(j).token,"",ffffff);

                brQuad.isBP = true;
//            if(loopType.equals("if")){
//            Quads br2quad = new Quads(lineCount22,"BRGT","","","-1");
//            myQuads.add(br2quad);
//            lineCount22++;
//            br2quad.isBP = true;
//            br2quad.isIfbp = true;
//            }


            myQuads.add(brQuad);
            lineCount22++;
            myQuads.add(new Quads(lineCount22,"block","","",""));
            lineCount22++;


        }

        if(mathlist.get(j).officialType.equals("LT")){
                compareName = "BRGEQ";
            comparetype = compareName;
                ffffff = Integer.toString(lineCount22 + 2);

            Quads brQuad = new Quads(lineCount22, compareName,mathlist.get(j).token,"",ffffff);

                brQuad.isBP = true;

            myQuads.add(brQuad);
            lineCount22++;
            myQuads.add(new Quads(lineCount22,"block","","",""));
            lineCount22++;
        }

        if(mathlist.get(j).officialType.equals("GTE")){


                compareName = "BRLT";
            comparetype = compareName;
                ffffff = Integer.toString(lineCount22 + 2);

            Quads brQuad = new Quads(lineCount22, compareName,mathlist.get(j).token,"",ffffff);

                brQuad.isBP = true;

            myQuads.add(brQuad);
            lineCount22++;
            myQuads.add(new Quads(lineCount22,"block","","",""));
            lineCount22++;
        }

        if(mathlist.get(j).officialType.equals("LTE")){


                compareName = "BRGT";
            comparetype = compareName;
                ffffff = Integer.toString(lineCount22 + 2);

            Quads brQuad = new Quads(lineCount22, compareName,mathlist.get(j).token,"",ffffff);

                brQuad.isBP = true;

            myQuads.add(brQuad);
            lineCount22++;
            myQuads.add(new Quads(lineCount22,"block","","",""));
            lineCount22++;
        }

        if(mathlist.get(j).officialType.equals("DOUBLEEQ")){


                compareName = "BRNEQ";
            comparetype = compareName;
                ffffff = Integer.toString(lineCount22 + 2);

            Quads brQuad = new Quads(lineCount22, compareName,mathlist.get(j).token,"",ffffff);

                brQuad.isBP = true;

            myQuads.add(brQuad);
            lineCount22++;
            myQuads.add(new Quads(lineCount22,"block","","",""));
            lineCount22++;
        }

        if(mathlist.get(j).officialType.equals("NOTEQ")){


                compareName = "BREQ";
            comparetype = compareName;
                ffffff = Integer.toString(lineCount22 + 2);

            Quads brQuad = new Quads(lineCount22, compareName,mathlist.get(j).token,"",ffffff);

                brQuad.isBP = true;

            myQuads.add(brQuad);
            lineCount22++;
            myQuads.add(new Quads(lineCount22,"block","","",""));
            lineCount22++;
        }

        return compareName;
    }


    //</editor-fold>

    //<editor-fold desc="Project 3">

    private static void proj3params()
    {
        List<Token> paramsTokens = new ArrayList<Token>();
        for(int i = 0; i < tokenArray.size(); i++){
            if(tokenArray.get(i).officialType.equals("func")){
                int paramCount = 1;
                for(int j = i; !tokenArray.get(j).officialType.equals("RPAREN"); j++){
                    if(tokenArray.get(j).officialType.equals("void")){
                        paramCount = 0;
                        break;
                    }
                    if(tokenArray.get(j).officialType.equals("COMMA") ){
                        paramCount++;
                    }
                }
                tokenArray.get(i).ParameterCount = paramCount;
                paramsTokens.add(tokenArray.get(i));
            }
        }

        for(int i = 0; i < tokenArray.size(); i++){

            if(tokenArray.get(i).officialType.equals("funcCall")){
                int paramCountCall = 1;
                if(tokenArray.get(i+2).officialType.equals("RPAREN") ){
                    paramCountCall = 0;
                }
                for(int u = i+2; !tokenArray.get(u).officialType.equals("RPAREN"); u++){
                    if(tokenArray.get(u).officialType.equals("COMMA")){
                        paramCountCall++;
                    }
                }
                tokenArray.get(i).ParameterCount = paramCountCall;
            }
        }


        //match paramcounts
        for(int i = 0; i < tokenArray.size(); i++){
            if(tokenArray.get(i).officialType.equals("funcCall")){
                for(int z = 0; z < paramsTokens.size(); z++){
                    if(tokenArray.get(i).token.equals(paramsTokens.get(z).token)){
                        tokenArray.get(i).ParameterCount = paramsTokens.get(z).ParameterCount;
//                        if (tokenArray.get(i).ParameterCount == paramsTokens.get(z).ParameterCount){
//
//                        }else{
//                            System.out.println("mismatching parameter types");
//                            //System.exit(290);
//                        }

                    }
                }
            }
        }

    }

    private static void returns() {
        int returnRequirement = 0;
        String functype = "none";
        for(int i = 0; i < tokenArray.size(); i++){
            if(tokenArray.get(i).officialType.equals("return")){
                i = i+1;
                if(!(tokenArray.get(i).officialType.equals("SEMICOLON") || tokenArray.get(i+1).officialType.equals("SEMICOLON"))){
                    if(tokenArray.get(i+1).officialType.equals("LBRACK")){
                        int tempCount = i;
                        while(!tokenArray.get(tempCount).officialType.equals("RBRACK") ) {
                            tempCount++;
                        }
                        if(!tokenArray.get(tempCount+1).officialType.equals("SEMICOLON")) {

                        }
                    }

                }
            }
        }


        for(int i = 0; i < tokenArray.size(); i++){
            if(tokenArray.get(i).officialType.equals("func")){
                functype = tokenArray.get(i).type;
            }


            tokenArray.get(i).funcType = functype;
            if(tokenArray.get(i).curlydepthlevel == 0){
                tokenArray.get(i).funcType = "none";
            }
        }

        for(int f = 0; f < tokenArray.size(); f++){
            if(tokenArray.get(f).officialType.equals("return")){

                if(tokenArray.get(f).funcType.equals("int") && (tokenArray.get(f+1).type.equals("int") || tokenArray.get(f+1).type.equals("intNUM") || tokenArray.get(f+1).type.equals("intArray")))  {
                    //System.out.println("int return " + tokenArray.get(f+1).token);

                }else if(tokenArray.get(f).funcType.equals("float") && (tokenArray.get(f+1).type.equals("float") || tokenArray.get(f+1).type.equals("floatNUM") || tokenArray.get(f+1).type.equals("floatArray")))  {
                    //System.out.println("float return " + tokenArray.get(f+1).token);

                }else if(tokenArray.get(f).funcType.equals("void") &&  tokenArray.get(f+1).officialType.equals("SEMICOLON")){
                    //System.out.println("void return " + tokenArray.get(f+1).token);

                }
                else{
                    System.out.println("Incorrect return statement");
                    //System.exit(280);
                }
            }
        }

        int funcChange = 0;
        int lastfuncChange = 0;
        for(int i = 1; i < tokenArray.size(); i++){
            funcChange = tokenArray.get(i).functionName;
            lastfuncChange = tokenArray.get(i-1).functionName;
            if(funcChange != lastfuncChange){
                tokenArray.get(i).changemarker = true;
            }
            if((funcChange != lastfuncChange) && (tokenArray.get(i).curlydepthlevel != 0) && !tokenArray.get(i).funcType.equals("void")){

                boolean stop = false;
                int t = i;
                while(!stop && t < tokenArray.size()){
                    if(tokenArray.get(t).officialType.equals("return")){

                        //System.out.println("return in " + tokenArray.get(t).functionName);
                        stop = true;
                    }
                    else if(tokenArray.get(t).officialType.equals("RBRACE") && tokenArray.get(t+1).curlydepthlevel == 0){
                        System.out.println("return statement not found in function " + tokenArray.get(t).functionName);
                        break;

                    }
                    t++;
                }
            }
        }
    }


    private static void Mathematics() {
        //
        //System.out.println(tokenArray.get(101).token);
        for(int i = 0; i < tokenArray.size(); i++){
            String fishface = tokenArray.get(i).officialType;
            if(tokenArray.get(i).officialType.equals("PLUS") ||
                    tokenArray.get(i).officialType.equals("MINUS") ||
                    tokenArray.get(i).officialType.equals("TIMES") ||
                    tokenArray.get(i).officialType.equals("DIVIDE") ||
                    tokenArray.get(i).officialType.equals("LT") ||
                    tokenArray.get(i).officialType.equals("LTE") ||
                    tokenArray.get(i).officialType.equals("GT") ||
                    tokenArray.get(i).officialType.equals("GTE") ||
                    tokenArray.get(i).officialType.equals("DOUBLEEQ") ||
                    tokenArray.get(i).officialType.equals("NOTEQ") ||
                    tokenArray.get(i).officialType.equals("EQ") ){
                Token Left = tokenArray.get(i-1);
                Token Right = tokenArray.get(i+1);

                //if paren on left side

                if(Left.officialType.equals("RPAREN")) {
                    int tempCount = i;
                    boolean stop = false;
                    while(!stop){
                        Token tokkkkk = tokenArray.get(tempCount);
                        if(tokkkkk.officialType.equals("LPAREN")){
                            Left = tokenArray.get(tempCount - 1);
                            stop = true;
                        }
                        tempCount--;
                    }


                    if(!Left.officialType.equals("funcCall")){
                        Left = tokenArray.get(i-2);
                        int tempCount2 = i-2;
                        boolean stop2 = false;
                        while(!stop2){
                            if(tokenArray.get(tempCount2).officialType.equals("ID") || tokenArray.get(tempCount2).officialType.equals("funcCall")){
                                Left = tokenArray.get(tempCount2);
                                stop2 = true;
                            }
                            tempCount2--;
                        }
                    }
                }



                if(Right.officialType.equals("LPAREN")) {
                    Right = tokenArray.get(i+2);
                    int tempCount = i+2;
                    boolean stop = false;
                    while(!stop){
                        if(tokenArray.get(tempCount).officialType.equals("ID") || tokenArray.get(tempCount).officialType.equals("funcCall")){
                            Right = tokenArray.get(tempCount);
                            stop = true;
                        }
                        tempCount++;
                    }
                }


                if(Left.officialType.equals("RBRACK")) {
                    int tempCount = i;
                    boolean stop = false;
                    while(!stop){
                        Token tokkkkk = tokenArray.get(tempCount);
                        if(tokkkkk.officialType.equals("LBRACK"))  {
                            Left = tokenArray.get(tempCount - 1);
                            stop = true;
                        }
                        tempCount--;
                    }
                }
                //floatArray
                //System.out.println(Left.token + " " + tokenArray.get(i).token + " " + Right.token);
                if(!((Left.type.equals("int") && Right.type.equals("int"))
                        || (Left.type.equals("int") && Right.type.equals("intNUM"))
                        || (Left.type.equals("intNUM") && Right.type.equals("int"))
                        || (Left.type.equals("intNUM") && Right.type.equals("intNUM"))
                        || (Left.type.equals("intArray") && Right.type.equals("intArray"))
                        || (Left.type.equals("intArray") && Right.type.equals("int"))
                        || (Left.type.equals("intArray") && Right.type.equals("intNUM"))
                        || (Left.type.equals("int") && Right.type.equals("intArray"))
                        || (Left.type.equals("intNUM") && Right.type.equals("intArray"))
                        || (Left.type.equals("floatArray") && Right.type.equals("floatArray"))
                        || (Left.type.equals("floatArray") && Right.type.equals("float"))
                        || (Left.type.equals("floatArray") && Right.type.equals("floatNUM"))
                        || (Left.type.equals("float") && Right.type.equals("floatArray"))
                        || (Left.type.equals("floatNUM") && Right.type.equals("floatArray"))
                        || (Left.type.equals("floatNUM") && Right.type.equals("float"))
                        || (Left.type.equals("float") && Right.type.equals("float"))
                        || (Left.type.equals("float") && Right.type.equals("floatNUM"))
                        || (Left.type.equals("floatNUM") && Right.type.equals("float"))
                        || (Left.type.equals("floatNUM") && Right.type.equals("floatNUM")))){
                    System.out.println("Mismatching types " + Left.token + " " + tokenArray.get(i).token + " " + Right.token);
                    //System.exit(270);
                }
            }
        }
    }


    private static void ArrayAnalysis() {
        //FINISH MESSING WITH THIS AFTER MATH
        //all numbers inside braces are ints
        for(int i = 0; i < tokenArray.size(); i++){
            if((tokenArray.get(i).type.equals("intArray") || tokenArray.get(i).type.equals("floatArray"))){


                //i = i+2;
                int tempCount = i;
//                if(tokenArray.get(i-1).officialType.equals("LBRACK")) {
////                while(!tokenArray.get(tempCount).officialType.equals("RBRACK")){
////                    if(!(tokenArray.get(tempCount).type.equals("intNUM") || tokenArray.get(tempCount).type.equals("int") || tokenArray.get(tempCount).type.equals("intArray")
////                            || tokenArray.get(tempCount).token.equals("+")|| tokenArray.get(tempCount).token.equals("-")|| tokenArray.get(tempCount).token.equals("*")|| tokenArray.get(tempCount).token.equals("/")
////                            || tokenArray.get(tempCount).token.equals("(")|| tokenArray.get(tempCount).token.equals(")") || tokenArray.get(tempCount).token.equals("[") || tokenArray.get(tempCount).token.equals("]"))) {
////
////
////                        System.out.println("improper indexing " + tokenArray.get(tempCount).token);
////                        //System.exit(212);
////                    }
////                    tempCount++;
////                }
//            }
            }

        }
    }


    public static void MainMethods(){
        int tokeup = 0;
        for(int z = 0; z < tokenArray.size(); z++){

            tokenArray.get(z).Tokencount = tokeup;
            tokeup++;
        }
        //check for mains (no mains or more than one main)
        int maincount = 0;
        int funcName = 0;
        String funcType;
        mainlist : for(int i =0; i < tokenArray.size(); i++){


            if((tokenArray.get(i).token.equals("main")) && (tokenArray.get(i).scopeCount == 0) && (tokenArray.get(i+1).officialType.equals("LPAREN"))){
                maincount++;
                tokenArray.get(i).officialType = "func";
                funcName++;
                //funcType = tokenArray.get(i).
            }

            else if((tokenArray.get(i).scopeCount == 0) && !(tokenArray.get(i).token == "$") && (tokenArray.get(i+1).officialType.equals("LPAREN"))){
                tokenArray.get(i).officialType = "func";
                funcName++;
            }
            else if((tokenArray.get(i).scopeCount > 0) && !(tokenArray.get(i).token == "$")
                    && (tokenArray.get(i+1).officialType.equals("LPAREN"))
                    && !(tokenArray.get(i).type.equals("keyword") ||tokenArray.get(i).type.equals("symbol") ||tokenArray.get(i).type.equals("intNUM")||tokenArray.get(i).type.equals("floatNUM"))){
                tokenArray.get(i).officialType = "funcCall";
            }



            Token BAN = tokenArray.get(i);
            tokenArray.get(i).functionName = funcName;

            if(tokenArray.get(i).curlydepthlevel == 0){
                tokenArray.get(i).functionName = 0;
            }

            if(maincount > 1){
                System.out.println("multiple main methods");
                //System.exit(200);
            }
        }
        if(maincount == 0){
            System.out.println("no main method");
            //System.exit(201);
        }

        List<Token> funcCheck = new ArrayList<Token>();
        for(int i =0; i < tokenArray.size(); i++){
            if(tokenArray.get(i).officialType.equals("func")){

                for(int j =0; j < funcCheck.size(); j++){
                    if(tokenArray.get(i).token.equals(funcCheck.get(j).token)){
                        System.out.println("Matching function names: " + tokenArray.get(i).token);
                        //System.exit(280);
                    }
                }
                funcCheck.add(tokenArray.get(i));
            }
        }
    }


    public static void ScopeAnalysis(){
        Stack<Integer> nameStack = new Stack<Integer>();
        Stack<Integer> recordStack = new Stack<Integer>();
        nameStack.push(0);
        recordStack.push(0);

        int scopeStack = 0;
        for(int i = 0; i < tokenArray.size(); i++){
            //scopeStack = 0;
            if(tokenArray.get(i).officialType.equals("LBRACE")){
                while(recordStack.contains(scopeStack)){
                    scopeStack++;
                }
                nameStack.push(scopeStack);
                recordStack.push(scopeStack);
            }
            if(tokenArray.get(i).officialType.equals("RBRACE")) {
                nameStack.pop();
                if(!nameStack.isEmpty()){
                    scopeStack = nameStack.peek();
                }
            }
            tokenArray.get(i).scopeCount = scopeStack;

        }
    }

    public static void IDtypes(){
        List<Token> DecBank = new ArrayList<Token>();
        //Managing Declarations and scope
        //I couldn't think of a better name

        //match all type declarations!!
        for(int i = 0; i < tokenArray.size(); i++){
            if((tokenArray.get(i).officialType.equals("int")  || tokenArray.get(i).officialType.equals("void") || tokenArray.get(i).officialType.equals("float"))){
                tokenArray.get(i+1).type = tokenArray.get(i).officialType;
                i = i+1;
            }
        }


        for(int i = 0; i < tokenArray.size(); i++){
            if((tokenArray.get(i).type.equals("int") || tokenArray.get(i).type.equals("float")) && !tokenArray.get(i).officialType.equals("func")){
                for(int j = 0; j < DecBank.size(); j++){
                    if(tokenArray.get(i).token.equals(DecBank.get(j).token)){
                        if((tokenArray.get(i).scopeCount == DecBank.get(j).scopeCount) ||
                                ((tokenArray.get(i).scopeCount != DecBank.get(j).scopeCount) && (tokenArray.get(i).curlydepthlevel > DecBank.get(j).curlydepthlevel)
                                        &&(tokenArray.get(i).functionName == DecBank.get(j).functionName))){
                            //potential conflict from declaration outside of functions done here
                            System.out.println("Conflicting Declarations");
                            //System.exit(205);
                        }
                    }
                }
                tokenArray.get(i).officialType = "dec";
                DecBank.add(tokenArray.get(i));

            }
        }



        for(int i = 0; i < tokenArray.size(); i++){
            if(tokenArray.get(i).officialType.equals("dec") && tokenArray.get(i+1).officialType.equals("LBRACK")){
                if(tokenArray.get(i).type.equals("int")){
                    tokenArray.get(i).type = "intArray";
                } else if(tokenArray.get(i).type.equals("float")){
                    tokenArray.get(i).type = "floatArray";
                }

            }
        }

        //spread type declarations to matching ID's in the right scope
        for(int i = 0; i < tokenArray.size(); i++){
            Token czech = tokenArray.get(i);
            int czechCount = 0;
            for(int j = 0; j < DecBank.size(); j++){
                if((DecBank.get(j).token.equals(tokenArray.get(i).token))){
                    if((((tokenArray.get(i).functionName == DecBank.get(j).functionName) || DecBank.get(j).functionName == 0) && (tokenArray.get(i).curlydepthlevel >= DecBank.get(j).curlydepthlevel))
                            && !(tokenArray.get(i).officialType.equals("dec") || tokenArray.get(i).officialType.equals("func") || tokenArray.get(i).officialType.equals("funcCall"))){
                        if(tokenArray.get(i).Tokencount > DecBank.get(j).Tokencount){
                            tokenArray.get(i).type = DecBank.get(j).type;

                        }


                    }
                }
            }

            if(tokenArray.get(i).officialType.equals("ID") && tokenArray.get(i).type.equals("ID")){
                System.out.println(tokenArray.get(i).token + " is undeclared");
                //System.exit(209);
            }

            if(tokenArray.get(i).officialType.equals("ID") &&tokenArray.get(i-1).officialType.equals("void") ){
                System.out.println(tokenArray.get(i).token + " is a void type ID");
                //System.exit(211);
            }
        }
        List<Token> funcList = new ArrayList<Token>();
        for(Token tick : tokenArray){
            if(tick.officialType.equals("funcCall")){
                for(int i = 0; i < tokenArray.size(); i++){
                    if((tick.token.equals(tokenArray.get(i).token)) && tokenArray.get(i).officialType.equals("func")){
                        tick.type =   tokenArray.get(i).type;
                    }
                }
            }
        }





        for(int i = 0;i < tokenArray.size(); i++){
            if(tokenArray.get(i).officialType.equals("funcCall") && tokenArray.get(i).type.equals("ID") ){
                System.out.println("undeclared function call " + tokenArray.get(i).token);
                //System.exit(215);
            }
        }

    }


    //</editor-fold>

    //<editor-fold desc="Top Down Parser">
    public static void next() {
        try{
            token++;
            curr = tokenArray.get(token);
            //System.out.println("next " + curr);
        } catch( NoSuchElementException e) {
            curr=null;
        }
    }

    public static void Program()
    {
        //System.out.println("Program  ");
        //program -> declaration-list
        DeclarationList();
    }

    public static void DeclarationList()
    {
        //System.out.println("DeclarationList  ");
        //declaration-list -> declaration declaration-loop
        Declaration();
        DeclarationLoop();
    }

    public static void DeclarationLoop()
    {
        //System.out.println("DeclarationLoop  ");
        //declaration-list2 -> declaration declaration-loop | empty

        if(curr.officialType.equals("int") || curr.officialType.equals("float") || curr.officialType.equals("void"))
        {
            Declaration();
            DeclarationLoop();
        }
        else if(curr.officialType.equals("$"))
        {

            // System.out.println("Project 2 Success!");
        }
        else
        {
            System.out.println("FAILED at " + curr.officialType);
            System.exit(1);
        }
    }

    public static void TypeSpecifier()
    {
        // System.out.println("TypSpecifier  ");
        //typeSpecifier -> int | float | void
        if(curr.officialType.equals("int") || curr.officialType.equals("float") || curr.officialType.equals("void"))
        {
            next();
        }
        else
        {
            System.out.println("FAILED at " + curr.officialType);
            System.exit(2);
        }
    }

    public static void Declaration()
    {
        //System.out.println("Declaration  ");
        //declaration -> typeSpecifier ID delcaration-kind
        if(curr.officialType.equals("int") || curr.officialType.equals("float") || curr.officialType.equals("void"))
        {
            TypeSpecifier();
            if(curr.officialType.equals("ID"))
            {
                next();
                oneDeclaration();
            }
            else
            {
                System.out.println("FAILED at " + curr.officialType);
                System.exit(3);
            }
        }
        else
        {
            System.out.println("FAILED at " + curr.officialType);
            System.exit(4);
        }
    }

    public static void oneDeclaration()
    {
        //System.out.println("oneDeclaration  ");
        //declarationKind -> declarationType |  ( parameterList ) compoundStatement
        if(curr.officialType.equals("SEMICOLON") || curr.officialType.equals("LBRACK"))
        {
            DeclarationType();
        }
        else if(curr.officialType.equals("LPAREN"))
        {
            next();
            Params();
            if(curr.officialType.equals("RPAREN"))
            {
                next();
                CompoundStatement();
            }
            else
            {
                System.out.println("FAILED at " + curr.officialType);
                System.exit(5);
            }
        }
        else
        {
            System.out.println("FAILED at " + curr.officialType);
            System.exit(6);
        }
    }

    public static void DeclarationType()
    {
        // System.out.println("DeclarationType  ");
        //declarationType -> ; | [ NUM ] ;
        if(curr.officialType.equals("SEMICOLON"))
        {
            next();
        }
        else if(curr.officialType.equals("LBRACK"))
        {
            next();
            if(curr.officialType.equals("NUM"))
            {
                next();
                if(curr.officialType.equals("RBRACK"))
                {
                    next();
                    if(curr.officialType.equals("SEMICOLON"))
                    {
                        next();
                    }
                    else
                    {
                        System.out.println("FAILED at " + curr.officialType);
                        System.exit(7);
                    }
                }
                else
                {
                    System.out.println("FAILED at " + curr.officialType);
                    System.exit(8);
                }
            }
            else
            {
                System.out.println("FAILED at " + curr.officialType);
                System.exit(9);
            }
        }
        else
        {
            System.out.println("FAILED at " + curr.officialType);
            System.exit(10);
        }
    }

    public static void Params()
    {
        //System.out.println("Params  ");
        if(curr.officialType.equals("void"))
        {
            next();
            Parameter();
        }
        else if(curr.officialType.equals("int") || curr.officialType.equals("float"))
        {
            next();
            if(curr.officialType.equals("ID"))
            {
                next();
                ParamType();
                ParamLoop();
            }
            else
            {
                System.out.println("FAILED at " + curr.officialType);
                System.exit(11);
            }
        }
        else
        {
            System.out.println("FAILED at " + curr.officialType);
            System.exit(12);
        }

    }

    public static void Parameter()
    {
        //System.out.println("Parameter  ");
        if(curr.officialType.equals("ID"))
        {
            next();
            ParamType();
            ParamLoop();
        }
        else if(curr.officialType.equals("RPAREN"))
        {
        }
        else
        {
            System.out.println("FAILED at " + curr.officialType);
            System.exit(13);
        }
    }

    public static void ParamLoop()
    {
        //System.out.println("ParamLoop  ");
        if(curr.officialType.equals("COMMA"))
        {
            next();
            Param();
            ParamLoop();
        }
        else if(curr.officialType.equals("RPAREN"))
        {
        }
        else
        {
            System.out.println("FAILED at " + curr.officialType);
            System.exit(14);
        }
    }

    public static void Param()
    {
        // System.out.println("Param  ");
        if(curr.officialType.equals("int") || curr.officialType.equals("float") || curr.officialType.equals("void"))
        {
            TypeSpecifier();
            if(curr.officialType.equals("ID"))
            {
                next();
                ParamType();
            }
            else
            {
                System.out.println("FAILED at " + curr.officialType);
                System.exit(15);
            }
        }
        else
        {
            System.out.println("FAILED at " + curr.officialType);
            System.exit(16);
        }
    }

    public static void ParamType()
    {
        //System.out.println("ParamType  ");
        if(curr.officialType.equals("LBRACK"))
        {
            next();
            if(curr.officialType.equals("RBRACK"))
            {
                next();
            }
            else
            {
                System.out.println("FAILED at " + curr.officialType);
                System.exit(17);
            }
        }
        else if(curr.officialType.equals("COMMA")
                || curr.officialType.equals("int") || curr.officialType.equals("float")
                || curr.officialType.equals("void") || curr.officialType.equals("RPAREN"))
        {
        }
        else
        {
            System.out.println("FAILED at " + curr.officialType);
            System.exit(18);
        }
    }

    public static void CompoundStatement()
    {
        //System.out.println("CompoundStatement  ");
        if(curr.officialType.equals("LBRACE"))
        {
            next();
            LocalDeclarations();
            StatementList();
            if(curr.officialType.equals("RBRACE"))
            {
                next();
            }
            else
            {
                System.out.println("FAILED at " + curr.officialType);
                System.exit(19);
            }
        }
        else
        {
            System.out.println("FAILED at " + curr.officialType);
            System.exit(20);
        }
    }

    public static void LocalDeclarations()
    {
        // System.out.println("LocalDeclarations  ");
        if(curr.officialType.equals("int") || curr.officialType.equals("float") || curr.officialType.equals("void"))
        {

            VariableDeclaration();
            LocalDeclarations();
        }
        else if(curr.officialType.equals("LPAREN") || curr.officialType.equals("ID") ||
                curr.officialType.equals("NUM") || curr.officialType.equals("SEMICOLON") ||
                curr.officialType.equals("LBRACE") || curr.officialType.equals("if") ||
                curr.officialType.equals("while") || curr.officialType.equals("return") ||
                curr.officialType.equals("RBRACE"))
        {
        }
        else
        {
            System.out.println("FAILED at " + curr.officialType);
            System.exit(21);
        }
    }

    public static void VariableDeclaration()
    {
        // System.out.println("VariableDeclaration  ");
        if(curr.officialType.equals("int") || curr.officialType.equals("float") || curr.officialType.equals("void"))
        {
            TypeSpecifier();
            if(curr.officialType.equals("ID"))
            {
                next();
                DeclarationType();
            }
            else
            {
                System.out.println("FAILED at " + curr.officialType);
                System.exit(22);
            }
        }
        else
        {
            System.out.println("FAILED at " + curr.officialType);
            System.exit(23);
        }

    }

    public static void StatementList()
    {
        //System.out.println("StatementList  ");
        //( ID NUM ; { if while return else ε
        //missing ;
        if(curr.officialType.equals("if") || curr.officialType.equals("return") ||
                curr.officialType.equals("while") || curr.officialType.equals("ID")
                || curr.officialType.equals("LBRACE") || curr.officialType.equals("LPAREN") || curr.officialType.equals("NUM"))
        {
            Statement();
            StatementList();
        }
        else if(curr.officialType.equals("RBRACE"))
        {
        }
        else
        {
            System.out.println("FAILED at " + curr.officialType);
            System.exit(24);
        }
    }

    public static void Statement()
    {
        //System.out.println("Statement  ");
        if(curr.officialType.equals("ID") || curr.officialType.equals("LPAREN"))
        {
            ExpressionStatement();
        }
        else if(curr.officialType.equals("LBRACE"))
        {
            CompoundStatement();
        }
        else if(curr.officialType.equals("if"))
        {
            SelectionStatement();
        }
        else if(curr.officialType.equals("while"))
        {
            IterationStatement();
        }
        else if(curr.officialType.equals("return"))
        {
            ReturnStatement();
        }
        else
        {
            System.out.println("FAILED at " + curr.officialType);
            System.exit(25);
        }
    }

    public static void SelectionStatement()
    {
        //System.out.println("SelectionStatement  ");
        if(curr.officialType.equals("if"))
        {
            next();
            if(curr.officialType.equals("LPAREN"))
            {
                next();
                Expression();
                if(curr.officialType.equals("RPAREN"))
                {
                    next();
                    Statement();
                    ElseStatement();
                }
                else
                {
                    System.out.println("FAILED at " + curr.officialType);
                    System.exit(26);
                }
            }
            else
            {
                System.out.println("FAILED at " + curr.officialType);
                System.exit(27);
            }

        }
        else
        {
            System.out.println("FAILED at " + curr.officialType);
            System.exit(28);
        }
    }

    public static void ExpressionStatement()
    {
        //System.out.println("ExpressionStatement  ");
        if(curr.officialType.equals("ID"))
        {
            Expression();
            if(curr.officialType.equals("SEMICOLON"))
            {
                next();
            }
            else
            {
                System.out.println("FAILED at " + curr.officialType);
                System.exit(29);
            }
        }
        else if(curr.officialType.equals("SEMICOLON"))
        {
            next();
        }
        else
        {
            System.out.println("FAILED at " + curr.officialType);
            System.exit(30);
        }
    }

    public static void ElseStatement()
    {
        //System.out.println("ElseStatement  ");
        if(curr.officialType.equals("else"))
        {
            next();
            Statement();
        }
        else if(curr.officialType.equals("LPAREN") || curr.officialType.equals("ID") ||
                curr.officialType.equals("NUM") || curr.officialType.equals("SEMICOLON") ||
                curr.officialType.equals("LBRACE") || curr.officialType.equals("if") ||
                curr.officialType.equals("while") || curr.officialType.equals("return") ||
                curr.officialType.equals("RBRACE"))
        {
        }
        else
        {
            System.out.println("FAILED at " + curr.officialType);
            System.exit(31);
        }
    }

    public static void IterationStatement()
    {
        //System.out.println("IterationStatement  ");
        if(curr.officialType.equals("while"))
        {
            next();
            if(curr.officialType.equals("LPAREN"))
            {
                next();
                Expression();
                if(curr.officialType.equals("RPAREN"))
                {
                    next();
                    Statement();
                }
                else
                {
                    System.out.println("FAILED at " + curr.officialType);
                    System.exit(32);
                }
            }
            else
            {
                System.out.println("FAILED at " + curr.officialType);
                System.exit(33);
            }
        }
        else{
            System.out.println("FAILED at " + curr.officialType);
            System.exit(34);
        }
    }

    public static void ReturnStatement()
    {
        //System.out.println("ReturnStatement  ");
        if(curr.officialType.equals("return"))
        {
            next();
            ReturnVar();
        }
        else
        {
            System.out.println("FAILED at " + curr.officialType);
            System.exit(35);
        }

    }

    public static void ReturnVar()
    {
        //System.out.println("ReturnVar  ");
        if(curr.officialType.equals("SEMICOLON"))
        {
            next();
        }
        else
        {
            Expression();
            if(curr.officialType.equals("SEMICOLON"))
            {
                next();
            }
            else
            {
                System.out.println("FAILED at " + curr.officialType);
                System.exit(36);
            }
        }
    }

    public static void Expression()
    {
        //System.out.println("Expression  ");
        if(curr.officialType.equals("ID"))
        {
            next();
            IDFollow();
        }
        else if(curr.officialType.equals("LPAREN"))
        {
            next();
            Expression();
            if(curr.officialType.equals("RPAREN"))
            {
                next();
                ExpressionFollow();
            }
        }
        else if(curr.officialType.equals("NUM"))
        {
            next();
            ExpressionFollow();
        }
        else
        {
            System.out.println("FAILED at " + curr.officialType);
            System.exit(50);
        }
    }

    public static void VarType()
    {
        //System.out.println("VarType  ");
        if(curr.officialType.equals("LBRACK"))
        {
            next();
            Expression();
            if(curr.officialType.equals("RBRACK"))
            {
                next();
            }
            else
            {
                System.out.println("FAILED at " + curr.officialType);
                System.exit(37);
            }
        }
        else if(curr.officialType.equals("EQ")
                || curr.officialType.equals("TIMES") || curr.officialType.equals("DIVIDE")
                || curr.officialType.equals("PLUS") || curr.officialType.equals("MINUS")
                || curr.officialType.equals("LTE") || curr.officialType.equals("LT")
                || curr.officialType.equals("GT") || curr.officialType.equals("GTE")
                || curr.officialType.equals("DOUBLEEQ") || curr.officialType.equals("NOTEQ")
                || curr.officialType.equals("SEMICOLON") || curr.officialType.equals("RPAREN")
                || curr.officialType.equals("RBRACK") || curr.officialType.equals("COMMA"))
        {
        }
        else
        {
            System.out.println("FAILED at " + curr.officialType);
            System.exit(38);
        }
    }

    public static void VarFollow()
    {
        //System.out.println("VarFollow  ");
        if(curr.officialType.equals("EQ"))
        {
            next();
            Expression();
        }
        else if(curr.officialType.equals("TIMES") || curr.officialType.equals("DIVIDE")
                || curr.officialType.equals("PLUS") || curr.officialType.equals("MINUS")
                || curr.officialType.equals("LTE") || curr.officialType.equals("LT")
                || curr.officialType.equals("GT") || curr.officialType.equals("GTE")
                || curr.officialType.equals("DOUBLEEQ") || curr.officialType.equals("NOTEQ")
                || curr.officialType.equals("SEMICOLON") || curr.officialType.equals("RPAREN")
                || curr.officialType.equals("RBRACK") || curr.officialType.equals("COMMA"))
        {
            ExpressionFollow();
        }
        else
        {
            System.out.println("FAILED at " + curr.officialType);
            System.exit(39);
        }

    }

    public static void IDFollow()
    {
        //System.out.println("IDFollow  ");
        if(curr.officialType.equals("LBRACK") || curr.officialType.equals("EQ")
                || curr.officialType.equals("TIMES") || curr.officialType.equals("DIVIDE")
                || curr.officialType.equals("PLUS") || curr.officialType.equals("MINUS")
                || curr.officialType.equals("LTE") || curr.officialType.equals("LT")
                || curr.officialType.equals("GT") || curr.officialType.equals("GTE")
                || curr.officialType.equals("DOUBLEEQ") || curr.officialType.equals("NOTEQ")
                || curr.officialType.equals("SEMICOLON") || curr.officialType.equals("RPAREN")
                || curr.officialType.equals("RBRACK") || curr.officialType.equals("COMMA"))
        {
            VarType();
            VarFollow();
        }
        else if(curr.officialType.equals("LPAREN"))
        {
            next();
            Args();
            if(curr.officialType.equals("RPAREN"))
            {
                next();
                ExpressionFollow();
            }
            else
            {
                System.out.println("FAILED at " + curr.officialType);
                System.exit(40);
            }
        }
        else
        {
            System.out.println("FAILED at " + curr.officialType);
            System.exit(41);
        }
    }

    public static void ExpressionFollow()
    {
        //System.out.println("ExpressionFollow  ");
        TermLoop();
        AdditiveExpressionLoop();
        Relation();
    }

    public static void Var()
    {
        //System.out.println("Var  ");
        VarType();
    }

    public static void Relation()
    {
        //System.out.println("Relation  ");
        if(curr.officialType.equals("LTE") || curr.officialType.equals("LT")
                || curr.officialType.equals("GT") || curr.officialType.equals("GTE")
                || curr.officialType.equals("DOUBLEEQ") || curr.officialType.equals("NOTEQ"))
        {
            RelOp();
            AdditiveExpression();
        }
        else if(curr.officialType.equals("SEMICOLON") || curr.officialType.equals("RPAREN")
                || curr.officialType.equals("RBRACK") || curr.officialType.equals("COMMA"))
        {
        }
        else
        {
            System.out.println("FAILED at " + curr.officialType);
            System.exit(42);
        }
    }

    public static void RelOp()
    {
        //System.out.println("RelOp  ");
        if(curr.officialType.equals("LTE") || curr.officialType.equals("LT")
                || curr.officialType.equals("GT") || curr.officialType.equals("GTE")
                || curr.officialType.equals("DOUBLEEQ") || curr.officialType.equals("NOTEQ"))
        {
            next();
        }
        else
        {
            System.out.println("FAILED at " + curr.officialType);
            System.exit(43);
        }
    }

    public static void AdditiveExpression()
    {
        //System.out.println("AdditiveExpression  ");
        Factor();
        AdditiveExpressionLoop();
    }

    public static void AdditiveExpressionLoop()
    {
        //System.out.println("AdditiveExpressionLoop  ");
        if(curr.officialType.equals("PLUS") || curr.officialType.equals("MINUS"))
        {
            AddOp();
            Term();
            AdditiveExpressionLoop();
        }
        else if(curr.officialType.equals("LTE") || curr.officialType.equals("LT")
                || curr.officialType.equals("GT") || curr.officialType.equals("GTE")
                || curr.officialType.equals("DOUBLEEQ") || curr.officialType.equals("NOTEQ")
                || curr.officialType.equals("SEMICOLON") || curr.officialType.equals("RPAREN")
                || curr.officialType.equals("RBRACK") || curr.officialType.equals("COMMA"))
        {
        }
        else
        {
            System.out.println("FAILED at " + curr.officialType);
            System.exit(44);
        }


    }

    public static void AddOp()
    {
        //System.out.println("AddOp  ");
        if(curr.officialType.equals("PLUS") || curr.officialType.equals("MINUS"))
        {
            next();
        }
        else
        {
            System.out.println("FAILED at " + curr.officialType);
            System.exit(45);
        }
    }

    public static void Term()
    {
        //System.out.println("Term  ");
        Factor();
        TermLoop();
    }

    public static void TermLoop()
    {
        //System.out.println("TermLoop  ");
        if(curr.officialType.equals("TIMES") || curr.officialType.equals("DIVIDE"))
        {
            MulOp();
            Factor();
            TermLoop();
        }
        else if(curr.officialType.equals("PLUS") || curr.officialType.equals("MINUS")
                || curr.officialType.equals("LTE") || curr.officialType.equals("LT")
                || curr.officialType.equals("GT") || curr.officialType.equals("GTE")
                || curr.officialType.equals("DOUBLEEQ") || curr.officialType.equals("NOTEQ")
                || curr.officialType.equals("SEMICOLON") || curr.officialType.equals("RPAREN")
                || curr.officialType.equals("RBRACK") || curr.officialType.equals("COMMA"))
        {
        }
        else
        {
            System.out.println("FAILED at " + curr.officialType);
            System.exit(46);
        }
    }

    public static void MulOp()
    {
        //System.out.println("MulOp  ");
        if(curr.officialType.equals("TIMES") || curr.officialType.equals("DIVIDE"))
        {
            next();
        }
        else
        {
            System.out.println("FAILED at " + curr.officialType);
            System.exit(47);
        }
    }

    public static void Factor()
    {
        //System.out.println("Factor " + curr.officialType);
        if(curr.officialType.equals("LPAREN"))
        {
            next();
            Expression();
            if(curr.officialType.equals("RPAREN"))
            {
                next();
            }
        }
        else if(curr.officialType.equals("NUM"))
        {
            next();
        }
        else if(curr.officialType.equals("ID"))
        {
            next();
            if(curr.officialType.equals("LPAREN"))
            {
                Call();
            }
            else
            {
                Var();
            }
        }
        else
        {
            System.out.println("FAILED at " + curr.officialType);
            System.exit(49);
        }
    }

    public static void Call()
    {
        //System.out.println("Call  ");
        if(curr.officialType.equals("LPAREN"))
        {
            next();
            Args();
            if(curr.officialType.equals("RPAREN"))
            {
                next();
            }
            else
            {
                System.out.println("FAILED at " + curr.officialType);
                System.exit(50);
            }
        }
        else
        {
            System.out.println("FAILED at " + curr.officialType);
            System.exit(51);
        }
    }

    public static void Args()
    {
        // System.out.println("Args  ");
        if(curr.officialType.equals("RPAREN"))
        {
        }
        else
        {
            ArgsList();
        }
    }

    public static void ArgsList()
    {
        //System.out.println("ArgsList  ");
        Expression();
        ArgListLoop();
    }

    public static void ArgListLoop()
    {
        //System.out.println("ArgListLoop  ");
        if(curr.officialType.equals("COMMA"))
        {
            next();
            Expression();
            ArgListLoop();
        }
        else if(curr.officialType.equals("RPAREN"))
        {
        }
        else
        {
            System.out.println("FAILED at " + curr.officialType);
            System.exit(52);
        }
    }
    //</editor-fold>

    //<editor-fold desc="Lexical Analyzer">
    public static ArrayList<Token> lexiconParser(List<String> list){
        int commentStack = 0;
        // new
        ArrayList<Token> tokenArray = new ArrayList<Token>();

        lineloop :  for(String ComList : list){



            char[] charz = ComList.toCharArray();
            if(charz.length != 0){
                System.out.println("\nINPUT: " + ComList);
            }
            for(int ch = 0; ch < charz.length; ch++){
                if(charz[ch] == ' '){
                    continue;
                }



                if(charz[ch] == '"'){
                    System.out.println("\"");
                }

                if(charz[ch] == '/'){
                    if((ch < charz.length - 1) && (charz[ch+1] == '*')){
                        commentStack++;
                        ch++;
                        continue;
                    }else if((ch< charz.length - 1) && (charz[ch+1] == '/' )){
                        continue lineloop;
                    }
                }

                if(charz[ch] == '*'){
                    if((ch < charz.length - 1) && (charz[ch+1] == '/')){
                        if((commentStack > 0)){
                            commentStack--;
                            charz[ch+1] = ' ';
                            charz[ch] = ' ';


                            //continue;
                        }
                    }else if(commentStack == 0){
                        //System.out.println(charz[ch]);
                    }
                }





                if(commentStack == 0){

                    if(charz[ch] == '[')
                    {
                        squareStack++;
                        //System.out.println("Square Bracket Depth: " + squareStack);
                    }
                    if(charz[ch] == ']')
                    {
                        squareStack--;
                        //System.out.println("Square Bracket Depth: " + squareStack);
                    }
                    if(charz[ch] == '{')
                    {
                        curlyStack++;
                        //System.out.println("Curly Bracket Depth: " + curlyStack);
                    }
                    if(charz[ch] == '}')
                    {
                        curlyStack--;
                        //System.out.println("Curly Bracket Depth: " + curlyStack);
                    }
                    if(charz[ch] == '(')
                    {
                        roundStack++;
                        // System.out.println("Square Bracket Depth: " + roundStack);
                    }
                    if(charz[ch] == ')')
                    {
                        roundStack--;
                        //System.out.println("Square Bracket Depth: " + roundStack);
                    }





                    if(ch < charz.length - 1){

                        if((charz[ch] == '!') && (charz[ch+1] == '=')){
                            ch++;
                            System.out.println("!=");
                            // new
                            tokenArray.add(new Token("!=", "symbol"));
                            continue;
                        }

                        if(charz[ch] == '=')  {
                            if (charz[ch+1] == '='){
                                System.out.println("==");
                                // new
                                tokenArray.add(new Token("==", "symbol"));
                                ch++;
                                continue;
                            } else {
                                System.out.println("=");
                                // new
                                tokenArray.add(new Token("=", "symbol"));
                                WordBank.add("=");

                                continue;
                            }

                        } // else{System.out.println("=");}

                        if((charz[ch] == '>') && (charz[ch+1] == '=')){
                            System.out.println(">=");
                            // new
                            tokenArray.add(new Token(">=", "symbol"));
                            ch++;
                            continue;
                        }

                        if((charz[ch] == '<') && (charz[ch+1] == '=')){
                            System.out.println("<=");
                            // new
                            tokenArray.add(new Token("<=", "symbol"));
                            ch++;
                            continue;
                        }
                    }

                    if((charz[ch] == '*') || (charz[ch] == '/')){
                        System.out.println(charz[ch]);
                        // new
                        tokenArray.add(new Token(Character.toString(charz[ch]), "symbol"));
                    }
//                    else if(Character.getType(charz[ch]) == Character.MATH_SYMBOL){
//                        System.out.println(charz[ch]);
//                    }
                    else if(symbolCheck(charz[ch])){
                        System.out.println(charz[ch]);
                        // new
                        tokenArray.add(new Token(Character.toString(charz[ch]), "symbol"));
                    }


                    //WORD HANDLING
                    if(Character.isLetter(charz[ch])){
                        //System.out.print(charz[ch]);
                        int LetterCount = ch;
                        String BirdWord = "";
                        while((LetterCount < charz.length) && (Character.isLetter(charz[LetterCount])  || Character.isDigit(charz[LetterCount]))){
                            BirdWord += charz[LetterCount];
                            LetterCount++;
                        }

                        ch = LetterCount - 1;
                        if(BirdWord.matches(".*\\d.*")){

                            System.out.println("Error: " + BirdWord);
                            continue lineloop;
                        }else{
                            if(keywordCheck(BirdWord)){
                                System.out.println("Keyword:  " + BirdWord);
                                // new
                                tokenArray.add(new Token(BirdWord, "keyword"));
                            } else {
                                System.out.println("ID:  " + BirdWord + " Depth Level: " + curlyStack);
                                // new
                                tokenArray.add(new Token(BirdWord, "ID"));
                                WordBank.add(BirdWord);
                            }
                        }
                    }


                    //ERROR HANDLING
                    if(errorCheck(charz[ch]))
                    {
                        int LetterCount = ch;
                        String BirdErr = "";
                        do{
                            BirdErr += charz[LetterCount];
                            LetterCount++;
                        }while((LetterCount < charz.length) && (Character.isDigit(charz[LetterCount]) || Character.isLetter(charz[LetterCount])));
                        System.out.println("error: " + BirdErr);
                        //This could be a problem later
                        ch = LetterCount - 1 ;
                        continue;
                    }

                    
                    if((Character.isDigit(charz[ch]))||(charz[ch] == '.')){
                        //boolean Period = false, letterE = false, plusMinus = false;

                        int LetterCount = ch;
                        String BirdNum = "";
                        while((LetterCount < charz.length) && ((Character.isDigit(charz[LetterCount]) || charz[LetterCount] == '.' || charz[LetterCount] == 'E' || charz[LetterCount] == '+' || charz[LetterCount] == '-' || Character.isLetter(charz[LetterCount])))){
                            if(charz[LetterCount] == '+' || charz[LetterCount] == '-'){
                                if(!BirdNum.contains("E")){
                                    break;
                                }
                            }
                            BirdNum += charz[LetterCount];
                            LetterCount++;
                        }


                        if(BirdNum.contains(".") || BirdNum.contains("E")){
                            if(isFloat(BirdNum)){
                                System.out.println("Float: " + BirdNum);
                                // new
                                tokenArray.add(new Token(BirdNum, "floatNUM"));
                                WordBank.add(BirdNum);
                            }else{System.out.println("Error: " + BirdNum);

                            }
                        } else {
                            if(!BirdNum.contains("[a-zA-Z]+")){
                                System.out.println("int: " + BirdNum);
                                // new
                                tokenArray.add(new Token(BirdNum, "intNUM"));
                                WordBank.add(BirdNum);
                            }else{
                                System.out.println("error: " + BirdNum);
                            }
                        }
                        ch = LetterCount - 1;
                    }
                }
            }
        }

        return tokenArray;
    }

    public static boolean isFloat(String string) {
        String regex = "\\d*\\.?\\d+([E][+-]?\\d+)?";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(string);
        boolean b = m.matches();

        if(b) {
            return true;
        }
        else {
            return false;
        }
    }

    public static boolean keywordCheck(String BirdWord){
        //UPDATE THIS LATER
        String[] Keywords = {"float","int","else","return","void","while", "if", "return"};
        return Arrays.asList(Keywords).contains(BirdWord);
    }

    public static boolean errorCheck(char BirdError){
        //UPDATE LATER
        boolean match = false;
        char[] KeyErrors = {'@','!','_','$'};

        for(char Elements : KeyErrors){
            if(Elements == BirdError){
                match = true;
            }
        }
        return match;
    }

    public static boolean symbolCheck(char BirdChar){
        //UPDATE LATER
        boolean match =  false;
        char[] KeyElements = {'+','-','%','(',')','{','}',';',',','[',']','\\','=','<','>'};

        for(char Elements : KeyElements){
            if(Elements == BirdChar){
                match = true;
            }
        }
        return match;
    }


    //</editor-fold>

}

class Backpatches{

    String backpatchtype;
    int lineNumber;

    Backpatches(String backpatchtype, int lineNumber){
        this.lineNumber = lineNumber;
        this.backpatchtype = backpatchtype;

    }
}

class Quads{
    String op;
    String opnd1;
    String opnd2;
    String Result;
    int Linecount;
    boolean isBP = false;
    boolean isIfbp = false;
    boolean wasbackpatched = false;

    //Result = -1 means backpatch!
    Quads( int Linecount, String op, String opnd1, String opnd2, String Result){

        this.op = op;
        this.opnd1 = opnd1;
        this.opnd2 = opnd2;
        this.Linecount = Linecount;
        this.Result = Result;


    }

}

class Token {
    String token;   // what token says
    String type;    // type of token from input (ID, int, float, keywords, etc)
    String officialType;    // output type (int, void, ID, int, PLUS, MINUS, etc
    String funcType;
    boolean isMath = false;
    boolean changemarker = false;
    //String suicide;
    int ParameterCount = 0;
    int Tokencount = -1;
    int functionName = 0;
    int scopeCount = 0;
    int curlydepthlevel = Main.curlyStack;
    int rounddepthlevel = Main.roundStack;
    int squaredepthlevel = Main.squareStack;
    int epicdepthlevel = rounddepthlevel+squaredepthlevel;
    int depthName = 0;
    boolean isT = false;
    boolean isA = false;

    // given name for each token in token array
    Token(String token, String type) {
        this.token = token;
        this.type = type;

        setType();
    }

    // test token for the "term" function
    Token(String name) {
        this.officialType = name;
    }


    public  void setType() {
        // ids
        if (type.equalsIgnoreCase("ID"))
            officialType = "ID";
            // numbers
        else if (type.equalsIgnoreCase("int"))
            officialType = "int";
        else if (type.equalsIgnoreCase("float"))
            officialType = "float";
        else if (type.equalsIgnoreCase("intNUM"))
            officialType = "NUM";
        else if (type.equalsIgnoreCase("floatNUM"))
            officialType = "NUM";
            // other keywords: else if return void while
        else if (type.equalsIgnoreCase("keyword")) {
            if (token.equalsIgnoreCase("else"))
                officialType = "else";
            else if (token.equalsIgnoreCase("if"))
                officialType = "if";
            else if (token.equalsIgnoreCase("return"))
                officialType = "return";
            else if (token.equalsIgnoreCase("void"))
                officialType = "void";
            else if (token.equalsIgnoreCase("while"))
                officialType = "while";
            else if (token.equalsIgnoreCase("int"))
                officialType = "int";
            else if (token.equalsIgnoreCase("float"))
                officialType = "float";
        }
        // symbols: + - * / < <= > >= == != = ; , ( ) [ ] { }
        else if (type.equalsIgnoreCase("symbol")){
            if (token.equalsIgnoreCase("+"))
                officialType = "PLUS";
            else if (token.equalsIgnoreCase("-"))
                officialType = "MINUS";
            else if (token.equalsIgnoreCase("*"))
                officialType = "TIMES";
            else if (token.equalsIgnoreCase("/"))
                officialType = "DIVIDE";
            else if (token.equalsIgnoreCase("<"))
                officialType = "LT";
            else if (token.equalsIgnoreCase("<="))
                officialType = "LTE";
            else if (token.equalsIgnoreCase(">"))
                officialType = "GT";
            else if (token.equalsIgnoreCase(">="))
                officialType = "GTE";
            else if (token.equalsIgnoreCase("=="))
                officialType = "DOUBLEEQ";
            else if (token.equalsIgnoreCase("!="))
                officialType = "NOTEQ";
            else if (token.equalsIgnoreCase("="))
                officialType = "EQ";
            else if (token.equalsIgnoreCase(";"))
                officialType = "SEMICOLON";
            else if (token.equalsIgnoreCase(","))
                officialType = "COMMA";
            else if (token.equalsIgnoreCase("("))
                officialType = "LPAREN";
            else if (token.equalsIgnoreCase(")"))
                officialType = "RPAREN";
            else if (token.equalsIgnoreCase("["))
                officialType = "LBRACK";
            else if (token.equalsIgnoreCase("]"))
                officialType = "RBRACK";
            else if (token.equalsIgnoreCase("{"))
                officialType = "LBRACE";
            else if (token.equalsIgnoreCase("}"))
                officialType = "RBRACE";
        }
    }

    public String getType() {
        return officialType;
    }

    @Override
    public String toString() {
        return token;
    }
}