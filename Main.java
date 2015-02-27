import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Main {

    public static List<String> WordBank = new ArrayList<String>();
    public static Map<String,String> balls = new HashMap<String, String>();
    public static List<String> AllBank = new ArrayList<String>();
    public static List<String> EqualBank = new ArrayList<String>();
    public static List<String> numBank = new ArrayList<String>();
    public static List<Token> tokenArray = new ArrayList<Token>();
    public static Token curr;
    static int count, token;

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


        //IDbank();


        token = 0;
        Token toke = new Token("$");
        toke.officialType = "$";
        toke.token = "$";
        tokenArray.add(toke);
        for(Token t : tokenArray) {
           // System.out.println(t.toString() + " " +  t.getType());
        }
        curr = tokenArray.get(0);
        System.out.println("////////////////////////////////////////////////////////////////////");
        System.out.println("Start: " + curr.officialType);
        Program();
        //System.out.println("Success!");
    }

    public static void next() {
        try{
            token++;
            curr = tokenArray.get(token);
           // System.out.println("next " + curr.officialType);
        } catch( NoSuchElementException e) {
            curr=null;
        }
    }





    public static void Program()
    {

        //program -> declaration-list
        DeclarationList();
    }

    public static void DeclarationList()
    {

        //declaration-list -> declaration declaration-loop
        Declaration();
        DeclarationLoop();
    }

    public static void DeclarationLoop()
    {

        //declaration-list2 -> declaration declaration-loop | empty

        if(curr.officialType.equals("int") || curr.officialType.equals("float") || curr.officialType.equals("void"))
        {
            Declaration();
            DeclarationLoop();
        }
        else if(curr.officialType.equals("$"))
        {

            System.out.println("Success!");
            System.exit(0);
        }
        else
        {
            System.out.println("FAILED at " + curr.officialType);
            System.exit(0);
        }
    }

    public static void TypeSpecifier()
    {

        //typeSpecifier -> int | float | void
        if(curr.officialType.equals("int") || curr.officialType.equals("float") || curr.officialType.equals("void"))
        {
            next();
        }
        else
        {
            System.out.println("FAILED at " + curr.officialType);
            System.exit(0);
        }
    }

    public static void Declaration()
    {
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
                System.exit(0);
            }
        }
        else
        {
            System.out.println("FAILED at " + curr.officialType);
            System.exit(0);
        }
    }

    public static void oneDeclaration()
    {
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
                System.exit(0);
            }
        }
        else
        {
            System.out.println("FAILED at " + curr.officialType);
            System.exit(0);
        }
    }

    public static void DeclarationType()
    {
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
                        System.exit(0);
                    }
                }
                else
                {
                    System.out.println("FAILED at " + curr.officialType);
                    System.exit(0);
                }
            }
            else
            {
                System.out.println("FAILED at " + curr.officialType);
                System.exit(0);
            }
        }
        else
        {
            System.out.println("FAILED at " + curr.officialType);
            System.exit(0);
        }
    }

    public static void Params()
    {
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
                System.exit(0);
            }
        }
        else
        {
            System.out.println("FAILED at " + curr.officialType);
            System.exit(0);
        }

    }

    public static void Parameter()
    {
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
            System.exit(0);
        }
    }

    public static void ParamLoop()
    {
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
            System.exit(0);
        }
    }

    public static void Param()
    {
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
                System.exit(0);
            }
        }
        else
        {
            System.out.println("FAILED at " + curr.officialType);
            System.exit(0);
        }
    }

    public static void ParamType()
    {
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
                System.exit(0);
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
            System.exit(0);
        }
    }

    public static void CompoundStatement()
    {
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
                System.exit(0);
            }
        }
        else
        {
            System.out.println("FAILED at " + curr.officialType);
            System.exit(0);
        }
    }

    public static void LocalDeclarations()
    {
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
            System.exit(0);
        }
    }

    public static void VariableDeclaration()
    {
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
                System.exit(0);
            }
        }
        else
        {
            System.out.println("FAILED at " + curr.officialType);
            System.exit(0);
        }

    }

    public static void StatementList()
    {
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
            System.exit(0);
        }
    }

    public static void Statement()
    {
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
            System.exit(0);
        }
    }

    public static void SelectionStatement()
    {
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
                    System.exit(0);
                }
            }
            else
            {
                System.out.println("FAILED at " + curr.officialType);
                System.exit(0);
            }

        }
        else
        {
            System.out.println("FAILED at " + curr.officialType);
            System.exit(0);
        }
    }

    public static void ExpressionStatement()
    {
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
                System.exit(0);
            }
        }
        else if(curr.officialType.equals("SEMICOLON"))
        {
            next();
        }
        else
        {
            System.out.println("FAILED at " + curr.officialType);
            System.exit(0);
        }
    }

    public static void ElseStatement()
    {
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
            System.exit(0);
        }
    }

    public static void IterationStatement()
    {
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
                    System.exit(0);
                }
            }
            else
            {
                System.out.println("FAILED at " + curr.officialType);
                System.exit(0);
            }
        }
        else{
            System.out.println("FAILED at " + curr.officialType);
            System.exit(0);
        }
    }

    public static void ReturnStatement()
    {
        if(curr.officialType.equals("return"))
        {
            next();
            ReturnVar();
        }
        else
        {
            System.out.println("FAILED at " + curr.officialType);
            System.exit(0);
        }

    }

    public static void ReturnVar()
    {
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
                System.exit(0);
            }
        }
    }

    public static void Expression()
    {
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
        }
        else
        {
            System.out.println("FAILED at " + curr.officialType);
            System.exit(0);
        }
    }

    public static void VarType()
    {
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
                System.exit(0);
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
            System.exit(0);
        }
    }

    public static void VarFollow()
    {
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
            System.exit(0);
        }

    }

    public static void IDFollow()
    {
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
                System.exit(0);
            }
        }
        else
        {
            System.out.println("FAILED at " + curr.officialType);
            System.exit(0);
        }
    }

    public static void ExpressionFollow()
    {
        TermLoop();
        AdditiveExpressionLoop();
        Relation();
    }

    public static void Var()
    {
        VarType();
    }

    public static void Relation()
    {
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
            System.exit(0);
        }
    }

    public static void RelOp()
    {
        if(curr.officialType.equals("LTE") || curr.officialType.equals("LT")
                || curr.officialType.equals("GT") || curr.officialType.equals("GTE")
                || curr.officialType.equals("DOUBLEEQ") || curr.officialType.equals("NOTEQ"))
        {
            next();
        }
        else
        {
            System.out.println("FAILED at " + curr.officialType);
            System.exit(0);
        }
    }

    public static void AdditiveExpression()
    {
        Factor();
        AdditiveExpressionLoop();
    }

    public static void AdditiveExpressionLoop()
    {
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
            System.exit(0);
        }


    }

    public static void AddOp()
    {
        if(curr.officialType.equals("PLUS") || curr.officialType.equals("MINUS"))
        {
            next();
        }
        else
        {
            System.out.println("FAILED at " + curr.officialType);
            System.exit(0);
        }
    }

    public static void Term()
    {
        Factor();
        TermLoop();
    }

    public static void TermLoop()
    {
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
            System.exit(0);
        }
    }

    public static void MulOp()
    {
        if(curr.officialType.equals("TIMES") || curr.officialType.equals("DIVIDE"))
        {
            next();
        }
        else
        {
            System.out.println("FAILED at " + curr.officialType);
            System.exit(0);
        }
    }

    public static void Factor()
    {
        System.out.println("Factor " + curr.officialType);
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
            System.exit(0);
        }
    }

    public static void Call()
    {
        System.out.println("FUUUUCCCKKKK");
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
                System.exit(0);
            }
        }
        else
        {
            System.out.println("FAILED at " + curr.officialType);
            System.exit(0);
        }
    }

    public static void Args()
    {
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
        Expression();
        ArgListLoop();
    }

    public static void ArgListLoop()
    {
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
            System.exit(0);
        }
    }

    public static ArrayList<Token> lexiconParser(List<String> list){
        int roundStack = 0;
        int curlyStack = 0;
        int squareStack = 0;
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



    public static void IDbank(){
        System.out.println(WordBank);
        int big = WordBank.size();
        for(int i = 1; i < big - 1; i++)
        {
            if(WordBank.get(i) == "="){
                balls.put(WordBank.get(i-1),WordBank.get(i+1));
            }
        }
        System.out.println("");
        System.out.println("////////////////////////////");
        for (Map.Entry<String,String> entry : balls.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            // do stuff
            System.out.println(key + " = " + value);
        }
    }


    // ========================================================================
    public static void rdParser(ArrayList<Token> tokenArray) {
        //next = tokenArray.get(0);

    }
}

class Token {
    String token;   // what token says
    String type;    // type of token from input (ID, int, float, keywords, etc)
    String officialType;    // output type (int, void, ID, int, PLUS, MINUS, etc

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





///**************/
///*************************
// i = 333;        ******************/       */
//
//        iiii = 3@33;
//
//int g 4 cd (int u, int v)      {
//        if(v == >= 0) return/*a comment*/ u;
//else ret_urn gcd(vxxxxxxvvvvv, u-u/v*v);
//       /* u-u/v*v == u mod v*/
//!
//        }
//
//        return void while       void main()