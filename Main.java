import java.io.*;
import java.util.*;

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
    
    public void printToFile()
    {
        File file = new File("Symbol_Table");
        try
        {
            if(file.createNewFile()==false)
            {
                System.out.println("File could not be created!");
                return;
            }
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
        catch(IOException e)
        {
            e.printStackTrace();
        }
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
    
    public void printToFile()
    {
        File file = new File("Literal_Table");
        try
        {
            if(file.createNewFile()==false)
            {
                System.out.println("File could not be created!");
                return;
            }
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
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }
    
    
}

public class Main
{
	public static void main(String[] args) {
		File input, intermediate, output;
		input=new File("input.asm");
		intermediate=new File("intermediate");
		output=new File("output.obj");
        
		if(input.exists()==false)
		{
		    System.out.println("Input file not found!");
		    return;
		}

		try 
		{
    		if(intermediate.createNewFile()==false)
    		{
    		    System.out.println("File not created 1!");
    		    return;
    		}
    		if(output.createNewFile()==false)
    		{
    		    System.out.println("File not created !");
    		    return;
    		}
        
		}
		catch(IOException e)
		{
		    e.printStackTrace();
		}
        // All the files created and initialized ...

        Scanner myReader;
        FileWriter myWriter;
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
        // Streams and data structures created
        
        try // pass 1 of assembler -->
        {
            myReader= new Scanner(input);
            myWriter= new FileWriter(intermediate);
            LocationCounter lc=new LocationCounter();
            while(myReader.hasNextLine()==true)
            {
                String tokens[];
                String i_line=myReader.nextLine();
                tokens=i_line.split(" ");

                for(int i=0; i<tokens.length; i++)
                    System.out.print(tokens[i]+"\t");
                System.out.println();
                // check for lables -->
                if(tokens[0].length()!=0)
                {
                    if(st.getIndex(tokens[0])==-1)
                        st.insert(tokens[0],lc.getValue());
                    else
                        st.setValue(tokens[0],lc.getValue());
                }
                
                // process mneumonic instruction -->
                String o_line= new String();
                int index;
                switch(tokens[1])
                {
                    // assembler directives -->
                    case "START":
                        o_line+="(AD,"+opcode.get(tokens[1])+") ";
                        o_line+="(C,"+Integer.parseInt(tokens[2])+")";
                        myWriter.write(o_line+"\n");
                        lc.setValue(Integer.parseInt(tokens[2]));
                        break;
                    case "END":
                        // process literals->
                        for(int i=lt.poolTable[lt.curr_pool]; i<lt.size; i++)
                        {
                            lt.table[i].address=lc.getValue();
                            // myWriter.write(lc.getValue()+": "+lt.table[i].val+"\n");
                            lc.increment();
                        }
                        // print IC->
                        o_line+="(AD,"+opcode.get(tokens[1])+") ";
                        myWriter.write(o_line+"\n");
                        lt.changePool();
                        break;
                    case "ORIGIN":
                        try 
                        {
                            lc.setValue(Integer.parseInt(tokens[2]));
                            o_line+="(AD,03) ";
                            o_line+="(C,"+tokens[2]+")";
                            myWriter.write(o_line+"\n");
                        }
                        catch(NumberFormatException e)
                        {
                            index = st.getIndex(tokens[2]);
                            int num =st.getValueAtIndex(index);
                            lc.setValue(num);
                            o_line+="(AD,03) ";
                            o_line+="(C,"+num+")";
                            myWriter.write(o_line+"\n");
                        }
                        break;
                    case "EQU":
                        // not getting what to do here lol :/ 
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
                        o_line+="("+"IS"+","+opcode.get(tokens[1])+") ";
                        myWriter.write(o_line+"\n");
                        lc.increment();
                        break;
                    case "ADD":
                    case "SUB":
                    case "MULT":
                    case "MOVER":
                    case "MOVEM":
                    case "COMP":
                    case "DIV":
                        o_line+="("+"IS"+","+opcode.get(tokens[1])+") ";
                        o_line+="("+Reg.get(tokens[2])+") ";
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
                            o_line+="(L,"+index+")";
                        }
                        else 
                        {
                            if(st.getIndex(tokens[3])==-1)
                            {
                                st.insert(tokens[3]);
                            }
                            index=st.getIndex(tokens[3]);
                            o_line+="(S,"+index+")";
                        }
                        
                        myWriter.write(o_line+"\n");
                        lc.increment();
                        break;
                    case "BC":
                        o_line+="("+"IS"+","+opcode.get(tokens[1])+") ";
                        o_line+="("+condCode.get(tokens[2])+") ";
                        if(st.getIndex(tokens[3])==-1)
                        {
                            st.insert(tokens[3]);
                        }
                        index=st.getIndex(tokens[3]);
                        o_line+="(S,"+index+")";
                        
                        myWriter.write(o_line+"\n");
                        lc.increment();
                        break;
                    case "READ":
                    case "PRINT":
                        o_line+="("+"IS"+","+opcode.get(tokens[1])+") ";
                        if(st.getIndex(tokens[2])==-1)
                        {
                            st.insert(tokens[2]);
                        }
                        index=st.getIndex(tokens[2]);
                        System.out.println("index = "+index);
                        o_line+="(S,"+index+")";
                        myWriter.write(o_line+"\n");
                        lc.increment();
                        break;
                        
                    // declarative statements -->
                    case "DC":
                        o_line+="("+"DL"+","+opcode.get(tokens[1])+") ";
                        o_line+="(C,"+tokens[2]+")";
                        myWriter.write(o_line+"\n");
                        lc.increment();
                        break;
                    case "DS":
                        o_line+="("+"DL"+","+opcode.get(tokens[1])+") ";
                        o_line+="(C,"+tokens[2]+")";
                        int length= Integer.parseInt(tokens[2]);
                        myWriter.write(o_line+"\n");
                        lc.setValue(lc.getValue()+length);
                        break;
                }
                
                myWriter.flush();
            }
            st.print();
            st.printToFile();
            lt.print();
            lt.printToFile();
    		myWriter.close();
    		myReader.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
		
		try // pass 2 of assembler -->
		{
		    myReader=new Scanner(intermediate);
		    myWriter=new FileWriter(output);
	        LocationCounter lc=new LocationCounter();
            		    
		    while(myReader.hasNextLine())
		    {
		        String i_line=myReader.nextLine();
		        String tokens[];
		        tokens= i_line.split(" ");
		        
		        for(int i=0; i<tokens.length; i++)
		        {
		            System.out.print("<"+tokens[i]+"> ");
		        }
		        System.out.println();
		        String o_line=new String();
		        String num;
		        switch(tokens[0].substring(1,3))
		        {
		            case "AD":
		                switch(tokens[0].substring(4,6))
		                {
		                    case "01":
		                        num=tokens[1].substring(3,tokens[1].length()-1);
		                        lc.setValue(Integer.parseInt(num));
		                        break;
		                    case "02":
		                        // nothing here ;/
		                        break;
		                    case "03":
		                        num=tokens[1].substring(3,tokens[1].length()-1);
		                        lc.setValue(Integer.parseInt(num));
		                        break;
		                }
		                
		                break;
		            case "DL":
		                switch(tokens[0].substring(4,6))
                        {
                            case "01":
                                o_line+=tokens[1].substring(3,tokens[1].length()-1);
                                myWriter.write(lc.getValue()+") "+o_line+"\n");
                                lc.increment();
                                break;
                            case "02":
                                num=tokens[1].substring(3,tokens[1].length()-1);
                                int n=Integer.parseInt(num);
                                for(int i=0; i<n; i++)
                                {
                                    o_line=new String();
                                    o_line+=lc.getValue();
                                    myWriter.write(o_line+")\n");
                                    lc.increment();
                                }
                                
                                break;
                        }		                
		                
		                break;
		            case "IS":
		                switch(tokens[0].substring(4,6))
                        {
                            case "00":
                                o_line+=tokens[0].substring(4,6);
                                myWriter.write(lc.getValue()+") "+o_line+"\n");
                                break;
                            case "01":
                            case "02":
                            case "03":
                            case "04":
                            case "05":
                            case "06":
                            case "07":
                            case "08":
                                o_line+=tokens[0].substring(4,6);
                                o_line+=" ";
                                o_line+=tokens[1].substring(1,2);
                                o_line+=" ";
                                num=tokens[2].substring(3,tokens[2].length()-1);
                                int index=Integer.parseInt(num);
                                switch(tokens[2].substring(1,2))
                                {
                                    case "S":
                                        o_line+=st.getValueAtIndex(index);
                                        myWriter.write(lc.getValue()+") "+o_line+"\n");
                                        break;
                                    case "L":
                                        o_line+=lt.getValueAtIndex(index);
                                        myWriter.write(lc.getValue()+") "+o_line+"\n");
                                        break;
                                }
                                break;
                            case "09":
                            case "10":
                                o_line+=tokens[0].substring(4,6);
                                o_line+=" ";
                                num=tokens[1].substring(3,tokens[1].length()-1);
                                index=Integer.parseInt(num);
                                switch(tokens[1].substring(1,2))
                                {
                                    case "S":
                                        o_line+=st.getValueAtIndex(index);
                                        myWriter.write(lc.getValue()+") "+o_line+"\n");
                                        break;
                                    case "L":
                                        o_line+=lt.getValueAtIndex(index);
                                        myWriter.write(lc.getValue()+") "+o_line+"\n");
                                        break;
                                }
                                break;
                        }		                
                        lc.increment();
                        break;
	            }
    		    myWriter.flush();
		    }
		    myReader.close();
		    myWriter.close();
		}
		catch(IOException e)
		{
		    e.printStackTrace();
		}
		
	}
}

