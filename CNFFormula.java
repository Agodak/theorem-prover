import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;


public class CNFFormula {
	
	public HashSet<Set<String>> formula =  new HashSet<Set<String>>();
	public CNFFormula(HashSet<Set<String>> formula)
	{
		this.formula = formula;
	}
	
	public CNFFormula(CNFFormula formula)
	{
		this(formula.formula);
	}
	
	public CNFFormula(String varname)
	{
		Set<String> set = new HashSet<String>();
		set.add(varname);
		formula.add(set);
	}
	
	public CNFFormula(String op, CNFFormula p)
	{
		CNFFormula f = new CNFFormula(new HashSet<Set<String>>());
		Iterator<Set<String>> iterator = p.formula.iterator();
		while(iterator.hasNext())
		{
			Set<String> s = iterator.next();
			Set<String> a = new HashSet<String>();
			Iterator<String> iterator2 = s.iterator();
			while(iterator2.hasNext())
			{
				String t = iterator2.next();
				if(t.charAt(0) == '-')
					t = t.substring(1);
				else
					t = "-".concat(t);
				a.add(t);
			}
			f.formula.add(a);
		}
		iterator = f.formula.iterator();
		HashSet<Set<String>> u = new HashSet<Set<String>>();
		Set<String> s = iterator.next();
		Iterator<String> iterator2 = s.iterator();
		while(iterator2.hasNext())
		{
			HashSet<String> h = new HashSet<String>();
			h.add(iterator2.next());
			u.add(h);
		}
		CNFFormula formula = new CNFFormula(u);
		while(iterator.hasNext())
		{
			s = iterator.next();
			u = new HashSet<Set<String>>();
			iterator2 = s.iterator();
			while(iterator2.hasNext())
			{
				HashSet<String> h = new HashSet<String>();
				h.add(iterator2.next());
				u.add(h);
			}
			CNFFormula formula2 = new CNFFormula(u);
			formula = distribute(formula, formula2);
		}
		this.formula = formula.formula;
	}
	
	public CNFFormula(String op, CNFFormula p, CNFFormula q)
	{
		//System.out.println(op);
		//and
		if(op.equals("&"))
		{
			this.formula = concatenate(p, q).formula;
			return;
		}
		//or
		if(op.equals("|"))
		{
			this.formula = distribute(p,q).formula;
			return;
		}
		//implication
		if(op.equals("=>"))
		{
			//System.out.println(new CNFFormula("-", p));
			//System.out.println(p);
			CNFFormula f = new CNFFormula("|", new CNFFormula("-", p), q);
			this.formula = f.formula;
			return;
		}
		//equivalence
		if(op.equals("<=>"))
		{
			CNFFormula f = new CNFFormula("&", new CNFFormula("=>", p, q), new CNFFormula("=>", q, p));
			this.formula = f.formula;
			return;
		}
	}
	
	private CNFFormula distribute(CNFFormula p, CNFFormula q) 
	{	
		HashSet<Set<String>> f = new HashSet<Set<String>>();
		Iterator<Set<String>> iterator = p.formula.iterator();
		if(p.formula.size()==1&&iterator.next().size()==1)
		{
			CNFFormula aux;
			aux = p;
			p = q;
			q = aux;
			iterator = p.formula.iterator();
		}
		iterator = p.formula.iterator();
		while(iterator.hasNext())
		{
			Iterator<Set<String>> iterator2 = q.formula.iterator();
			Set<String> s = iterator.next();
			while(iterator2.hasNext())
			{
				Set<String> t = iterator2.next();
				s = concatenate2(s,t);
			}
			f.add(s);
		}
		CNFFormula c = new CNFFormula(f);
		return c;
	}

	private Set<String> concatenate2(Set<String> s, Set<String> t) {
		Iterator<String> iterator = t.iterator();
		while(iterator.hasNext())
		{
			s.add(iterator.next());
		}
		return s;
	}

	private CNFFormula concatenate(CNFFormula p, CNFFormula q)
	{
		Iterator<Set<String>> iterator = q.formula.iterator();
		while(iterator.hasNext())
		{
			p.formula.add(iterator.next());
		}
		return p;
	}
	
	public String toString() 
	{
		String string = "{";
		Iterator<Set<String>> iterator = this.formula.iterator();
		while(iterator.hasNext())
		{
			string = string.concat("{");
			Iterator<String> iterator2 = iterator.next().iterator();
			while(iterator2.hasNext())
			{
				string = string.concat(iterator2.next()+ ",");
			}
			string = string.substring(0, string.length()-1);
			string = string.concat("},");
		}
		string = string.substring(0, string.length()-1);
		string = string.concat("}");
		return string;
	}

	public boolean resolution(int k) 
	{
		Iterator<Set<String>> iterator = this.formula.iterator();
		HashSet<Set<String>> resFormula = new HashSet<Set<String>>();
		resFormula.addAll(this.formula);
		int ok = 0, j = 0;;
		while(iterator.hasNext())
		{
			++j;
			Set<String> set = iterator.next();
			Iterator<Set<String>> iterator2 = this.formula.iterator();
			int i = 0;
			while(iterator2.hasNext())
			{
				Set<String> set2 = iterator2.next();
				if(i < j)
				{
					++i;
					continue;
				}
				if(i <= k)
				{
					++i;
					continue;
				}
				Set<String> set3 = complementary(set, set2);
				if(!set3.isEmpty())
				{
					if(set3.contains("contradiction"))
						return false;
					else 
						if(this.formula.contains(set3))
							return true;
						else
						{
							ok++;
							if(resFormula.contains(set3))
								return true;
							resFormula.add(set3);
							if(contradiction(resFormula))
								return false;
						}
				}
			}
		}
		if(ok > 0)
		{
			CNFFormula f = new CNFFormula(resFormula);
			return f.resolution(ok);
		}
		return true;
	}

	private boolean contradiction(HashSet<Set<String>> resFormula)
	{
		Iterator<Set<String>> iterator = resFormula.iterator();
		while(iterator.hasNext())
		{
			Set<String> set = iterator.next();
			if(set.size() == 1)
			{
				String t = new String();
				for(String s : set)
					t = s;
				Iterator<Set<String>> iterator2 = resFormula.iterator();
				while(iterator2.hasNext())
				{
					Set<String> set2 = iterator2.next();
					if(set2.size() == 1)
					{
						if(t.startsWith("-"))
						{
							if(set2.contains(t.substring(1)))
							{
								System.out.println(set + ", " + set2 + " -> contradiction");
								return true;
							}
						}
						else
							if(set2.contains("-" + t))
							{
								System.out.println(set + ", " + set2 + " -> contradiction");
								return true;
							}
					}
				}
			}
		}
		return false;
	}

	private Set<String> complementary(Set<String> set3, Set<String> set4)
	{
		Set<String> set = new LinkedHashSet<String>();
		set.addAll(set3);
		Set<String> set2 = new LinkedHashSet<String>();
		set2.addAll(set4);
		Iterator<String> iterator = set.iterator();
		while(iterator.hasNext())
		{
			String string = iterator.next();
			if(string.startsWith("-"))
			{
				if(set2.contains(string.substring(1)))
				{
					System.out.print(set + ", " + set2);
					set.remove(string);
					set2.remove(string.substring(1));
					set.addAll(set2);
					if(set.isEmpty())
						set.add("contradiction");
					System.out.println(" -> " + set);
					return set;
				}
			}
			else
			{
				if(set2.contains("-" + string))
				{
					System.out.print(set + ", " + set2);
					set.remove(string);
					set2.remove("-" + string);
					set.addAll(set2);
					if(set.isEmpty())
						set.add("contradiction");
					System.out.println(" -> " + set);
					return set;
				}
			}
		}
		return new LinkedHashSet<String>();
	}

	public boolean dpll(LinkedHashMap<String, Boolean> interpretation)
	{
		if(models(interpretation))
		{
			System.out.println(interpretation);
			return true;
		}
		Iterator<Set<String>> iterator = this.formula.iterator();
		while(iterator.hasNext())
		{
			Set<String> emptySet = iterator.next();
			if(emptySet.isEmpty())
				return false;
		}
		boolean ok = true;
		while(ok)
		{
			ok = false;
			String t = new String();
			iterator = this.formula.iterator();
			Set<String> set = iterator.next();
			if(this.formula.size()==1&&set.size()==1)
			{
				for(String s : set)
					t = s;
				String string;
				if(t.startsWith("-"))
				{
					string = t.substring(1);
					interpretation.put(string, false);
				}
				else
				{
					string = "-" + t;
					interpretation.put(t, true);
				}
				System.out.println("Unit propagation: " + t + " " + this.formula);
				System.out.println(interpretation);
				return true;
			}
			while(iterator.hasNext())
			{
				set = iterator.next();
				if(set.size() == 1)
				{
					ok = true;
					for(String s : set)
						t = s;
					System.out.println("Unit propagation: " + t + " " + this.formula);
					break;
				}
			}
			if(!ok)
				break;
			String string = new String();
			if(t.startsWith("-"))
			{
				string = t.substring(1);
				interpretation.put(string, false);
			}
			else
			{
				string = "-" + t;
				interpretation.put(t, true);
			}
			iterator = this.formula.iterator();
			while(iterator.hasNext()&&!t.isEmpty())
			{
				set = iterator.next();
				if(set.contains(t))
				{
					iterator.remove();
				}
				else
				{
					Iterator<String> iterator2 = set.iterator();
					while(iterator2.hasNext())
					{
						String string2 = iterator2.next();
						if(string.equals(string2))
							iterator2.remove();
					}
				}

			}
			iterator = this.formula.iterator();
			while(iterator.hasNext())
			{
				if(iterator.next().isEmpty())
				{
					System.out.println(this.formula);
					return false;
				}
			}
		}
		Set<String> literals = getLiterals();
		for(String string : literals)
		{
			if(pureLiteral(string))
			{
				interpretation.put(string, true);
				iterator = this.formula.iterator();
				while(iterator.hasNext())
				{
					Set<String> set = iterator.next();
					if(set.contains(string))
						iterator.remove();
				}
				System.out.println("Pure Literal: " + string + " " + this.formula);
			}
			else
			{
				string = "-" + string;
				if(pureLiteral(string))
				{
					interpretation.put(string.substring(1), false);
					iterator = this.formula.iterator();
					while(iterator.hasNext())
					{
						Set<String> set = iterator.next();
						if(set.contains(string))
							iterator.remove();
					}
					System.out.println("Pure Literal: " + string + " " + this.formula);
				}
			}
		}
		for(String string : literals)
		{
			if(!interpretation.containsKey(string))
			{
				HashSet<Set<String>> copy = this.formula;
				interpretation.put(string, true);
				System.out.println("Splitting: " + this.formula + " " + string + "=true");
				iterator = this.formula.iterator();
				while(iterator.hasNext())
				{
					Set<String> set = iterator.next();
					if(set.contains(string))
						iterator.remove();
					else
					{
						Iterator<String> iterator2 = set.iterator();
						while(iterator2.hasNext())
						{
							String string2 = iterator2.next();
							if(("-" + string).equals(string2))
								iterator2.remove();
						}
					}
				}
				if(dpll(interpretation))
					return true;
				else
				{
					interpretation.remove(string);
					this.formula = copy;
				}
				interpretation.put(string, false);
				System.out.println("Splitting: " + this.formula + " " + string + "=false");
				string = "-" + string;
				while(iterator.hasNext())
				{
					Set<String> set = iterator.next();
					if(set.contains(string))
						iterator.remove();
					else
					{
						Iterator<String> iterator2 = set.iterator();
						while(iterator2.hasNext())
						{
							String string2 = iterator2.next();
							if(string.substring(1).equals(string2))
								iterator2.remove();
						}
					}
				}
				if(dpll(interpretation))
					return true;
				else
				{
					interpretation.remove(string);
					this.formula = copy;
				}
			}
		}
		System.out.println(this.formula);
		return false;
	}

	private boolean pureLiteral(String string)
	{
		boolean ok = false;
		if(string.startsWith("-"))
		{
			Iterator<Set<String>> iterator = this.formula.iterator();
			while(iterator.hasNext())
			{
				Set<String> set = iterator.next();
				if(set.contains(string))
					ok = true;
				if(set.contains(string.substring(1)))
					return false;
			}
		}
		else
		{
			Iterator<Set<String>> iterator = this.formula.iterator();
			while(iterator.hasNext())
			{
				Set<String> set = iterator.next();
				if(set.contains(string))
					ok = true;
				if(set.contains("-" + string))
					return false;
			}
		}
		return ok;
	}

	private Set<String> getLiterals()
	{
		Set<String> literals = new HashSet<String>();
		Iterator<Set<String>> iterator = this.formula.iterator();
		while(iterator.hasNext())
		{
			Set<String> set = iterator.next();
			Iterator<String> iterator2 = set.iterator();
			while(iterator2.hasNext())
			{
				String string = iterator2.next();
				if(string.startsWith("-"))
					string = string.substring(1);
				literals.add(string);
			}
		}
		return literals;
	}

	private boolean models(LinkedHashMap<String, Boolean> interpretation)
	{
		Iterator<Set<String>> iterator = this.formula.iterator();
		while(iterator.hasNext())
		{
			Set<String> set = iterator.next();
			Iterator<String> iterator2 = set.iterator();
			while(iterator2.hasNext())
			{
				String string = iterator2.next();
				if(string.startsWith("-"))
					string = string.substring(1);
				if(!interpretation.containsKey(string))
					return false;
			}
		}
		return true;
	}
}
