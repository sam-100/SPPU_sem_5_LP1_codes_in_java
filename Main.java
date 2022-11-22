import java.io.*;
import java.util.*;
import java.util.regex.*;

class LocationCounter
{
    int lc;
    LocationCounter()
    {
        lc=0;
    }
    public int getValue()
    {
        return lc;
    }
    
    public void setValue(int x)
    {
        lc=x;
    }
    
    public void increment()
    {
        lc++;
    }
}
class SymbolTableEntry
{
    public String name;
    public int address;
    SymbolTableEntry(String s, int a)
    {
        name=s;
        address=a;
    }
}
class SymbolTable
{
    SymbolTableEntry table[];
    int size;
    SymbolTable()
    {
        table= new SymbolTableEntry[50];
        size=0;
    }
    
    public void insert(String s, int a)
    {
        for(int i=0; i<size; i++)
        {
            if(table[i].name.equals(s))
            {
                System.out.println("Duplicate insert of symbol "+s+" is not possible!");
                return;
            }
        }
        table[size++]=new SymbolTableEntry(s,a);
        return;
    }
    
    public void insert(String s)
    {
        for(int i=0; i<size; i++)
        {
            if(table[i].name.equals(s))
            {
                System.out.println("Duplicate insert of symbol "+s+" is not possible!");
                return;
            }
        }
        table[size++]=new SymbolTableEntry(s,-1);
        return;
    }

    public void setValue(String s, int a)
    {
        
        for(int i=0; i<size; i++)
        {
            if(table[i].name.equals(s))
            {
                table[i].address=a;
                return;
            }
        }
        System.out.println("Symbol "+s+" not found in setValue()!");
        return;
    }
    
    public int getIndex(String s)
    {
        for(int i=0; i<size; i++)
        {
            if(table[i].name.equals(s))
                return i;
        }
        System.out.println("Symbol "+s+" not found in getIndex()!");
        return -1;
    }
    
    public int getValueAtIndex(int index)
    {
        return table[index].address;
    }
    
    public void print()
    {
        System.out.println("Symbol Table("+size+") -->");
        System.out.println("Symbol\tAddress");
        for(int i=0; i<size; i++)
        {
            System.out.println(table[i].name+"\t"+table[i].address);
        }
    }
    
    public void printToFile() throws Exception
    {
        File file = new File("Symbol_Table");
        if(file.exists())
            file.delete();
        file.createNewFile();

        FileWriter myWriter = new FileWriter(file);
        myWriter.write("Symbol\tAddress\n");
        for(int i=0; i<size; i++)
        {
            myWriter.write(table[i].name+"\t"+table[i].address);
            myWriter.write("\n");
            myWriter.flush();
        }
        myWriter.close();
    }
}
class LiteralTableEntry
{
    public int val, address;
    LiteralTableEntry(int v, int a)
    {
        val=v;
        address=a;
    }
    LiteralTableEntry(int v)
    {
        val=v;
        address=-1;
    }
}
class LiteralTable
{
    public LiteralTableEntry table[];
    public int poolTable[];
    public int size;
    public int curr_pool;
    LiteralTable()
    {
        table = new LiteralTableEntry[50];
        size=0;
        poolTable=new int[20];
        poolTable[0]=0;
        curr_pool=0;
    }
    public void insert(int n)
    {
        for(int i=poolTable[curr_pool]; i<size; i++)
        {
            if(table[i].val==n)
            {
                System.out.println("Literal "+n+" already exists.");
                return;
            }
        }
        table[size++]=new LiteralTableEntry(n);
    }
    
    public int getIndex(int n)
    {
        for(int i=poolTable[curr_pool]; i<size; i++)
        {
            if(table[i].val==n)
                return i;
            
        }
        return -1;
    }
    
    public int getValueAtIndex(int index)
    {
        return table[index].address;
    }

    
    public void changePool()
    {
        curr_pool++;
        poolTable[curr_pool]=size;
    }
    
    
    
    public void print()
    {
        System.out.println("----- LiteralTable ----- ");
        System.out.println("Literal\taddress");
        for(int i=0; i<size; i++)
        {
            System.out.println(table[i].val+"\t"+table[i].address);
        }
    }
    
    public void printToFile() throws IOException {
        File file = new File("Literal_Table");

        if(file.exists()) 
            file.delete();
        file.createNewFile();

        FileWriter myWriter = new FileWriter(file);
        myWriter.write("LiteralTable\n");
        myWriter.write("Symbol\tAddress\n");
        for(int i=0; i<size; i++)
        {
            myWriter.write(table[i].val+"\t"+table[i].address);
            myWriter.write("\n");
            myWriter.flush();
        }
        myWriter.write("\n\n");
        myWriter.write("PoolTable\n");
        myWriter.write("Pool\tIndex\n");
        for(int i=0; i<curr_pool; i++)
        {
            myWriter.write(i+"\t"+poolTable[i]);
            myWriter.write("\n");
            myWriter.flush();
        }
        myWriter.close();
    }
    
    
}

public class Main
{
	public static void main(String[] args) throws Exception {
        // Creating and initializing Scanner object to standerd input
            Scanner scan = new Scanner(System.in);

        // Taking file name as input
            String input_name=new String();
            System.out.print("Enter the name of input file: ");
            input_name = scan.nextLine();

        // Creating file objects 
            File input, intermediate, output;
            input=new File(input_name);
            intermediate=new File("intermediate");
            output=new File("output.txt");
            

        // Checking and creating new files
            if(input.exists()==false)
            {
                System.out.println("Input file not found!");
                scan.close();
                return;
            }

            if(intermediate.exists())
                intermediate.delete();
            intermediate.createNewFile();

            if(output.exists())
                output.delete();
            output.createNewFile();


        // Creating stream handlers 
            Scanner myReader;
            FileWriter myWriter;

        // Creating Data structure objects and initializing them
            LocationCounter lc=new LocationCounter();
            SymbolTable st= new SymbolTable();
            LiteralTable lt= new LiteralTable();
            HashMap<String, String> opcode = new HashMap<>();
            opcode.put("STOP", "00");
            opcode.put("ADD", "01");
            opcode.put("SUB", "02");
            opcode.put("MULT", "03");
            opcode.put("MOVER", "04");
            opcode.put("MOVEM", "05");
            opcode.put("COMP", "06");
            opcode.put("BC", "07");
            opcode.put("DIV", "08");
            opcode.put("READ", "09");
            opcode.put("PRINT", "10");
            opcode.put("START", "01");
            opcode.put("END", "02");
            opcode.put("ORIGIN", "03");
            opcode.put("EQU", "04");
            opcode.put("LTORG", "05");
            opcode.put("DC", "01");
            opcode.put("DS", "02");
            HashMap<String, Integer> Reg= new HashMap<>();
            Reg.put("AREG", 1);
            Reg.put("BREG", 2);
            Reg.put("CREG", 3);
            Reg.put("DREG", 4);
            HashMap<String, Integer> condCode= new HashMap<>();
            condCode.put("LT",1);
            condCode.put("LE",2);
            condCode.put("EQ",3);
            condCode.put("GT",4);
            condCode.put("GE",5);
            condCode.put("ANY",6);

        
        
        // Pass 1 Code -->
            // Setting file in/out stream handlers 
                myReader= new Scanner(input);
                myWriter= new FileWriter(intermediate);

            // Parsing each line of 'input' file, processing it to generate output and writing output to the 'intermediate' file
                while(myReader.hasNextLine()==true)
                {
                    // Reading a line and parsing it into tokens
                        String tokens[];
                        String i_line=myReader.nextLine();
                        tokens=i_line.split(" ");
                    // handling the lable if any -->
                        if(tokens[0].length()!=0)
                        {
                            if(st.getIndex(tokens[0])==-1)
                                st.insert(tokens[0],lc.getValue());
                            else
                                st.setValue(tokens[0],lc.getValue());
                        }
                    
                    int index;
                    
                    // Comapring the mneumonic in switch-case
                        switch(tokens[1])
                        {
                        // Handling assembler directives -->
                            case "START":
                                myWriter.write("(AD,"+opcode.get(tokens[1])+") ");
                                myWriter.write("(C,"+Integer.parseInt(tokens[2])+")");
                                lc.setValue(Integer.parseInt(tokens[2]));
                                myWriter.write("\n");
                            break;
                            case "END":
                                // Print intermediate code for "END" ->
                                    myWriter.write("(AD,"+opcode.get(tokens[1])+")"+"\n");
                                // process literals->
                                    for(int i=lt.poolTable[lt.curr_pool]; i<lt.size; i++)
                                    {
                                        lt.table[i].address=lc.getValue();
                                        myWriter.write("(DL,01) (C,"+lt.table[i].val+")\n");
                                        lc.increment();
                                    }
                                // Change Literal pool->
                                    lt.changePool();
                            break;
                            case "ORIGIN":
                                try 
                                {
                                    lc.setValue(Integer.parseInt(tokens[2]));
                                    myWriter.write("(AD,03) ");
                                    myWriter.write("(C,"+tokens[2]+")");
                                    myWriter.write("\n");
                                }
                                catch(NumberFormatException e)
                                {
                                    index = st.getIndex(tokens[2]);
                                    int num =st.getValueAtIndex(index);
                                    lc.setValue(num);
                                    myWriter.write("(AD,03) ");
                                    myWriter.write("(C,"+num+")");
                                    myWriter.write("\n");
                                }
                            break;
                            case "EQU":
                                try
                                {
                                    st.setValue(tokens[0],Integer.parseInt(tokens[2]));
                                }
                                catch(NumberFormatException nfe)
                                {
                                    
                                    index = st.getIndex(tokens[2]);
                                    int num= st.getValueAtIndex(index);
                                    st.setValue(tokens[0], num);
                                }
                            break;
                            case "LTORG":
                                // 
                                for(int i=lt.poolTable[lt.curr_pool]; i<lt.size; i++)
                                {
                                    lt.table[i].address=lc.getValue();
                                    myWriter.write("(DL,01) (C,"+lt.table[i].val+")\n");
                                    lc.increment();
                                }
                                lt.changePool();
                            break;
                                
                                
                        // Imperative statements -->
                            case "STOP":
                                myWriter.write("("+"IS"+","+opcode.get(tokens[1])+")\n");
                                lc.increment();
                            break;
                            case "ADD":
                            case "SUB":
                            case "MULT":
                            case "MOVER":
                            case "MOVEM":
                            case "COMP":
                            case "DIV":
                                myWriter.write("("+"IS"+","+opcode.get(tokens[1])+") ");
                                myWriter.write("("+Reg.get(tokens[2])+") ");
                                if(tokens[3].charAt(0)=='=')   //literal
                                {
                                    String num=new String();
                                    num=tokens[3].substring(2,tokens[3].length()-1);
                                    int val=Integer.parseInt(num);
                                    
                                    if(lt.getIndex(val)==-1)
                                    {
                                        lt.insert(val);
                                    }
                                    index=lt.getIndex(val);
                                    myWriter.write("(L,"+index+")");
                                }
                                else 
                                {
                                    if(st.getIndex(tokens[3])==-1)
                                    {
                                        st.insert(tokens[3]);
                                    }
                                    index=st.getIndex(tokens[3]);
                                    myWriter.write("(S,"+index+")");
                                }
                                
                                myWriter.write("\n");
                                lc.increment();
                            break;
                            case "BC":
                                myWriter.write("("+"IS"+","+opcode.get(tokens[1])+") ");
                                myWriter.write("("+condCode.get(tokens[2])+") ");
                                if(st.getIndex(tokens[3])==-1)
                                {
                                    st.insert(tokens[3]);
                                }
                                index=st.getIndex(tokens[3]);
                                myWriter.write("(S,"+index+")");
                                myWriter.write("\n");
                                lc.increment();
                            break;
                            case "READ":
                            case "PRINT":
                                myWriter.write("("+"IS"+","+opcode.get(tokens[1])+") ");
                                if(st.getIndex(tokens[2])==-1)
                                    st.insert(tokens[2]);
                                index=st.getIndex(tokens[2]);
                                myWriter.write("(S,"+index+")");
                                myWriter.write("\n");
                                lc.increment();
                            break;
                                
                        // declarative statements -->
                            case "DC":
                                myWriter.write("("+"DL"+","+opcode.get(tokens[1])+") ");
                                myWriter.write("(C,"+Integer.parseInt(tokens[2])+")");
                                myWriter.write("\n");
                                lc.increment();
                            break;
                            case "DS":
                                myWriter.write("("+"DL"+","+opcode.get(tokens[1])+") ");
                                myWriter.write("(C,"+tokens[2]+")");
                                int words = Integer.parseInt(tokens[2]);
                                myWriter.write("\n");
                                lc.setValue(lc.getValue()+words);
                            break;
                    }
                    
                    // Flushing myWriter to intermediate file
                        myWriter.flush();
                }
            
            // Printing the result 
                st.print();
                lt.print();
                st.printToFile();
                lt.printToFile();
            
            // Closing file in/out stream handlers     
                myWriter.close();
                myReader.close();

            
		
		
		// Pass 2 Code -->
            // Setting file in/out stream handlers 
                myReader=new Scanner(intermediate);
                myWriter=new FileWriter(output);

            // Parsing each line of 'intermediate' file, processing it to generate output and writing output to the 'output' file            
                while(myReader.hasNextLine())
                {
                    // Reading a line and parsing it into tokens
                        String i_line=myReader.nextLine();
                        String tokens[];
                        tokens= i_line.split(" ");
                    

                    String o_line=new String();
                    String num;
                    String type, code;
                    int operand1=0, operand2=0;
                    type=tokens[0].substring(1, 3);
                    code=tokens[0].substring(4,6);
                    if(tokens.length>=2)
                    {

                        if(tokens[1].length()==3)
                            operand1=Integer.parseInt(tokens[1].substring(1,2));
                        else
                        {
                            String table;
                            int index;
                            table=tokens[1].substring(1,2);
                            index=Integer.parseInt( tokens[1].substring(3, tokens[1].length()-1) );
                            switch(table)
                            {
                                case "S":
                                    operand1=st.getValueAtIndex(index);
                                break;
                                case "L":
                                    operand1=lt.getValueAtIndex(index);
                                break;
                                case "C":
                                    operand1=index;
                                break;
                            
                            }
                            }
                    }

                    if(tokens.length>=3)
                    {
                        String table;
                        int index;
                        table=tokens[2].substring(1,2);
                        index=Integer.parseInt( tokens[2].substring(3, tokens[2].length()-1) );
                        switch(table)
                        {
                            case "S":
                                operand2=st.getValueAtIndex(index);
                            break;
                            case "L":
                                operand2=lt.getValueAtIndex(index);
                            break;
                        }
                    }

                    // Comapring the opcode in switch-case
                        switch(type)
                        {
                            case "AD":
                                switch(code)
                                {
                                    case "01":
                                        lc.setValue(operand1);
                                        break;
                                    case "02":
                                        // nothing here ;/
                                        break;
                                    case "03":
                                        lc.setValue(operand1);
                                        break;
                                }
                                
                                break;
                            case "DL":
                                switch(code)
                                {
                                    case "01":
                                        myWriter.write(lc.getValue()+") "+operand1+"\n");
                                        lc.increment();
                                    break;
                                    case "02":
                                        for(int i=0; i<operand1; i++)
                                        {
                                            myWriter.write(lc.getValue()+")\n");
                                            lc.increment();
                                        }
                                    break;
                                }		                
                                
                                break;
                            case "IS":
                                myWriter.write(lc.getValue()+") "+code+" "+operand1+" "+operand2+"\n");
                                lc.increment();
                            break;
                        }
                    
                    // Flushing myWriter to intermediate file
                        myWriter.flush();
                }

            // Closing file in/out stream handlers     
                myReader.close();
                myWriter.close();

        // Closing Scanner object 
            scan.close();
	}
}

