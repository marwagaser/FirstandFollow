import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

//T14_37-10683_Marwa_Gaser
public class CFG {
	String input;
	static HashMap<String, String> Firsttable;
	static HashMap<String, Integer> FirsttableFound;

	public CFG(String input) {
		this.input = input;
		Firsttable = new HashMap<String, String>();
		FirsttableFound = new HashMap<String, Integer>();
	}

	public static String[] splitString(String s, String operator) {
		String[] sections = s.split(operator);
		return sections;
	}

	public static boolean FirstTableDone(HashMap<String, String> hmap) {
		Set<String> keys = hmap.keySet();
		for (String key1 : keys) {
			if (!(FirsttableFound.get(key1) == 0)) {
				return false;
			}
		}
		return true;
	}

	public static void firstTerminals(HashMap<String, String> hmap) {
		Firsttable.clear();
		FirsttableFound.clear();
		Set<String> keys = hmap.keySet();
		for (String key : keys) {
			String value = hmap.get(key);
			String[] sentential_form = splitString(value, ",");
			int length = sentential_form.length;
			FirsttableFound.put(key, length);
			for (int i = 0; i < length; i++) {
				Character c = sentential_form[i].charAt(0);
				int ascii = c;
				// 65-90 upper case (Variables)
				if (!(ascii >= 65 && ascii <= 90)) {
					if (Firsttable.containsKey(key))
						Firsttable.put(key, Firsttable.get(key) + "," + (c.toString()));
					else
						Firsttable.put(key, (c.toString()));
					FirsttableFound.put(key, FirsttableFound.get(key) - 1);
				}
			}

		}

	}

	public static void getFirstof(HashMap<String, String> htable) {
		Set<String> keys = htable.keySet();
		for (String key : keys) {
			String value = htable.get(key);
			String[] sentential_form = splitString(value, ",");
			if (FirsttableFound.get(key) > 0)
				recursive(htable, key, sentential_form);
		}
	}

	public static void recursive(HashMap<String, String> htable, String key, String[] sentential_form) {

		if (FirstTableDone(htable)) {
			return;
		} else {
			////
			for (int i = 0; i < sentential_form.length; i++) {
				Character c = sentential_form[i].charAt(0);
				int ascii = c;
				if (ascii >= 65 && ascii <= 90 && !(c.toString().equals(key))) {
					if (Firsttable.containsKey(c.toString()) && FirsttableFound.get(c.toString()) == 0) {
						if (!Firsttable.containsKey(key)) {
							Firsttable.put(key, Firsttable.get(c.toString()));
						} else {
							String str = Firsttable.get(key) + "," + Firsttable.get(c.toString());
							HashSet<String> hs = new HashSet<String>(Arrays.asList(str.split(",")));
							String value = String.join(",", hs);
							Firsttable.put(key, value);
						}
						FirsttableFound.put(key, FirsttableFound.get(key) - 1);
					} else if (Firsttable.containsKey(c.toString()) && FirsttableFound.get(c.toString()) > 0) {
						recursive(htable, c.toString(), splitString(htable.get(c.toString()), ","));
					} else if (!(Firsttable.containsKey(c.toString()))) {
						recursive(htable, c.toString(), splitString(htable.get(c.toString()), ","));
					}
				} else if (ascii >= 65 && ascii <= 90 && (c.toString().equals(key))) {
					FirsttableFound.put(key, FirsttableFound.get(key) - 1);
				} else {
					continue;
				}
			}
		}
	}

	public static void checkEpsilons(HashMap<String, String> hmap) {
		// System.out.println(FirsttableFound);
		Set<String> keys = hmap.keySet();
		for (String key : keys) {
			String val = Firsttable.get(key);
			HashSet<String> hs = new HashSet<String>(Arrays.asList(val.split(",")));
			val = String.join(",", hs);
			Firsttable.put(key, val);
			String value = hmap.get(key);
			String[] sentential_form = splitString(value, ",");
			for (int i = 0; i < sentential_form.length; i++) {
				Character x = sentential_form[i].charAt(0);
				int ascii = x;
				if (ascii >= 65 && ascii <= 90) {
					if (Firsttable.get(x.toString()).contains("e")) {
						// apply epsilon substitution
						String str = Firsttable.get(x.toString()) + "," + Firsttable.get(key);
						hs = new HashSet<String>(Arrays.asList(str.split(",")));
						str = String.join(",", hs);
						Firsttable.put(key, str);
						while (sentential_form[i].length() > 1 && Firsttable.containsKey(x.toString())
								&& Firsttable.get(x.toString()).contains("e")) {
							str = Firsttable.get(x.toString()) + "," + Firsttable.get(key);
							hs = new HashSet<String>(Arrays.asList(str.split(",")));
							hs.remove("null");
							str = String.join(",", hs);
							Firsttable.put(key, str);
							sentential_form[i] = sentential_form[i].substring(1);
							x = sentential_form[i].charAt(0);
						}
						if (Firsttable.containsKey(x.toString()) && (!Firsttable.get(x.toString()).contains("e"))) {
							str = Firsttable.get(x.toString()) + "," + Firsttable.get(key);
							hs = new HashSet<String>(Arrays.asList(str.split(",")));
							hs.remove("e");
							hs.remove("null");

							str = String.join(",", hs);

							Firsttable.put(key, str);

						} else if (!Firsttable.containsKey(x.toString())) { // if lower case
							str = Firsttable.get(key) + "," + x;
							hs = new HashSet<String>(Arrays.asList(str.split(",")));
							hs.remove("null");
							hs.remove("e");
							str = String.join(",", hs);
							Firsttable.put(key, str);
						} else if (sentential_form[i].length() == 1 && Firsttable.containsKey(x.toString())
								&& (Firsttable.get(x.toString()).contains("e"))) {
							str = Firsttable.get(x.toString()) + "," + Firsttable.get(key);
							hs = new HashSet<String>(Arrays.asList(str.split(",")));
							hs.remove("null");
							FirsttableFound.put(key, 1);
							str = String.join(",", hs);
							Firsttable.put(key, str);
						}

					} else {
						continue;
					}
				}
			}

		}
	}

	public void First() {

		HashMap<String, String> hmap = new HashMap<String, String>();
		String[] CFGrules = splitString(this.input, ";");
		for (int i = 0; i < CFGrules.length; i++) {
			String key = CFGrules[i].substring(0, 1);
			String value = CFGrules[i].substring(2);
			hmap.put(key, value);

		}
		firstTerminals(hmap);
		getFirstof(hmap);
		checkEpsilons(hmap);
		Set<String> keys = hmap.keySet();
		String finalAnswer = "";
		for (String key : keys) {
			String answer = Firsttable.get(key);
			if (FirsttableFound.get(key) == 1) {
				answer += ",e";
			}
			HashSet<String> hs = new HashSet<String>(Arrays.asList(answer.split(",")));
			answer = String.join("", hs);
			finalAnswer += key + "," + answer + ";";
		}
		finalAnswer = finalAnswer.substring(0, finalAnswer.length() - 1);
		System.out.println(finalAnswer);
		// System.out.println(FirsttableFound);
	}

	public static void main(String[] args) {
		/*
		 * CFG cfg = new CFG("L,SdL,S;T,aSb,iaLb,e;S,ScT,T"); CFG cfg2 = new
		 * CFG("S,aTbS,e;T,aTb,e"); CFG cfg3 = new
		 * CFG("S,SAB,SBC,e;A,aAa,e;B,bB,e;C,cC,e"); CFG cfg4 = new
		 * CFG("S,AB;A,aA,b;B,CA;C,cC,d"); CFG cfg5 = new
		 * CFG("S,lAr,a;A,lArB,aB;B,cSB,e"); CFG cfg6 = new CFG("S,aA;A,SB,e;B,bA,cA");
		 */

		CFG cfg7 = new CFG("S,ABCDZ;A,a,e;B,b,e;C,c;D,d,e;Z,z,e");
		CFG cfg8 = new CFG("Z,TX;X,+TX-,e;T,FV;V,*FV,e;F,(Z),i");
		CFG cfg9 = new CFG("S,ACB,CbB,Ba;A,da,BC;B,g,e;C,h,e");
		CFG cfg10 = new CFG("S,Bb,Cd;B,aB,e;C,cC,e");

		/*
		 * cfg.First(); cfg2.First(); cfg3.First(); cfg4.First(); cfg5.First();
		 * cfg6.First();
		 */

		cfg7.First();
		cfg8.First();
		cfg9.First();
		cfg10.First();

	}
}
