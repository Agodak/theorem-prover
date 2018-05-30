import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;


public class Parser
{

	public static void main(String[] args) 
	{
		Path path = Paths.get("input.txt");
		Stack<String> stack = new Stack<String>();
		List<String> string =  new LinkedList<String>();
		try 
		{
			string = Files.readAllLines(path);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		Iterator<String> iterator = string.iterator(); 	
		while(iterator.hasNext())
		{
			String s = new String();
			String p = iterator.next();
			int i=0;
			Stack<CNFFormula> formulas = new Stack<CNFFormula>();
			while(i<p.length())
			{
				s = new String();
				if((p.charAt(i)>='A' && p.charAt(i)<='Z')||(p.charAt(i)>='0'&&p.charAt(i)<='9'))
				{
					while((p.charAt(i)>='A' && p.charAt(i)<='Z')||(p.charAt(i)>='0'&&p.charAt(i)<='9'))
					{
						s = s.concat(String.valueOf(p.charAt(i)));
						++i;
						if(i>=p.length())
							break;
					}
					formulas.push(new CNFFormula(s));
					
				}
				else
				{
					String v = new String();
					boolean ok = false;
					switch (p.charAt(i)) 
					{
						case '-':
							++i;
							char[] t = {'-'};
							v = String.copyValueOf(t);
							break;
						case '&':
							++i;
							char[] t1 = {'&'};
							v = String.copyValueOf(t1);
							break;
						case '|':
							++i;
							char[] t2 = {'|'};
							v = String.copyValueOf(t2);
							break;
						case '=':
							++i;++i;
							char[] t3 = {'=','>'};
							v = String.copyValueOf(t3);
							break;
						case '<':
							++i;++i;++i;
							char[] t4 = {'<','=','>'};
							v = String.copyValueOf(t4);
							break;
						case '(':
							++i;
							stack.push("(");
							ok = true;
							break;
						case ')':
							++i;
							while(stack.peek()!="(")
							{
								String x = stack.pop();
								if(x.equals("-"))
								{
									//System.out.println("I am here");
									CNFFormula f = formulas.pop();
									f = new CNFFormula("-", f);
									formulas.push(f);
								}
								else
								{
									CNFFormula a = formulas.pop();
									CNFFormula b = formulas.pop();
									formulas.push(new CNFFormula(x, b, a));
								}
							}
							stack.pop();
							ok = true;
							break;
						default:
							break;
					}
					if(!ok)
					{
						if(stack.empty())
						{
							stack.push(v);
						}
						else
						{
							while(!stack.empty()&&precedence(stack.peek())>=precedence(v))
							{
								String x = stack.pop();
								if(x.equals("-"))
								{
									//System.out.println("I am here");
									CNFFormula f = formulas.pop();
									f = new CNFFormula("-", f);
									formulas.push(f);
								}
								else
								{
									//System.out.println("I am here");
									CNFFormula a = formulas.pop();
									CNFFormula b = formulas.pop();
									formulas.push(new CNFFormula(x, b, a));
								}
							}
							stack.push(v);
						}
					}
				}
				//System.out.println(formulas);
			}
			while(!stack.empty())
			{
				String x = stack.pop();
				if(x.equals("-"))
				{
					CNFFormula f = formulas.pop();
					f = new CNFFormula("-", f);
					formulas.push(f);
				}
				else
				{
					CNFFormula a = formulas.pop();
					CNFFormula b = formulas.pop();
					formulas.push(new CNFFormula(x, b, a));
				}
			}
			System.out.println("CNF: " + formulas.peek());
			System.out.println();
			System.out.println("Enter the number coressponding to the desired action: 1. Resolution 2. DPLL 3. Comparison");
			Scanner scanner = new Scanner(System.in);
			int n = scanner.nextInt();
			scanner.close();
			CNFFormula formula = formulas.peek();
			if(n == 1)
			{
				if(formula.resolution(0))
					System.out.println("Satisfiable");
				else
					System.out.println("Unsatisfiable");
			}
			if(n == 2)
				if(!formula.dpll(new LinkedHashMap<String, Boolean>()))
					System.out.println("UNSAT");
			if(n == 3)
			{
				java.lang.management.ThreadMXBean bean = ManagementFactory.getThreadMXBean();
				long initial = bean.isCurrentThreadCpuTimeSupported() ? bean.getCurrentThreadUserTime() : 0L;
				if(!formula.dpll(new LinkedHashMap<String, Boolean>()))
					System.out.println("UNSAT");
				long first = bean.isCurrentThreadCpuTimeSupported() ? bean.getCurrentThreadUserTime() : 0L;
				System.out.println("DPLL: " + (first-initial));
				if(formula.resolution(0))
					System.out.println("Satisfiable");
				else
					System.out.println("Unsatisfiable");
				long second = bean.isCurrentThreadCpuTimeSupported() ? bean.getCurrentThreadUserTime() : 0L;
				System.out.println("Resolution: " + (second-first));
			}
		}
	}

	private static int precedence(String peek)
	{
		if(peek.equals("-"))
			return 3;
		if(peek.equals("&"))
			return 2;
		if(peek.equals("|"))
			return 2;
		if(peek.equals("=>"))
			return 1;
		return 0;
	}

}
