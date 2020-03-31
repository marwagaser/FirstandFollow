
//T14_37-10683_Marwa_Gaser
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class CFG {
	String input;
	static HashMap<String, String> Firsttable;

	public CFG(String input) {
		this.input = input;
		Firsttable = new HashMap<String, String>();

	}

	public static String[] splitString(String s, String operator) {
		String[] sections = s.split(operator);
		return sections;
	}

	public void getFirst(HashMap<String, String> hmap) {
		boolean changed = false;
		Firsttable.clear();

		Set<String> keys = hmap.keySet();
		for (String key : keys) {
			String value = hmap.get(key);
			String[] sentential_form = splitString(value, ",");
			int length = sentential_form.length;

			for (int i = 0; i < length; i++) {
				Character c = sentential_form[i].charAt(0);
				int ascii = c;
				// 65-90 not upper case (Variables)
				if (!(ascii >= 65 && ascii <= 90)) {
					changed = true;
					if (Firsttable.containsKey(key))
						Firsttable.put(key, Firsttable.get(key) + "," + (c.toString()));
					else
						Firsttable.put(key, (c.toString()));

					String val = Firsttable.get(key);
					HashSet<String> hs = new HashSet<String>(Arrays.asList(val.split(",")));
					val = String.join(",", hs);
					Firsttable.put(key, val);
				} else {
					Firsttable.put(key, "");
				}
			}

		}

		while (changed) {
			changed = false;
			for (String key : keys) {
				String value = hmap.get(key);
				String[] sentential_form = splitString(value, ",");
				int length = sentential_form.length;
				for (int i = 0; i < length; i++) {
					String[] current = splitString(sentential_form[i], "");
					int epsilonCount = 0;
					for (int j = 0; j < current.length; j++) {
						Character x = current[j].charAt(0);
						int ascii = x;
						if (ascii >= 65 && ascii <= 90 && Firsttable.get(x.toString()).contains("e")) { // if upper case
							epsilonCount++;
						} else {
							epsilonCount = 0;
							break;
						}

					}
					if (epsilonCount == current.length) {
						// do the if logic
						if (!Firsttable.get(key).contains("e")) {
							String str = Firsttable.get(key) + ",e";
							HashSet<String> hs = new HashSet<String>(Arrays.asList(str.split(",")));
							String e = String.join(",", hs);
							Firsttable.put(key, e);
							changed = true;
						}
					}
					////
					for (int z = 0; z < length; z++) {
						for (int j = 0; j < current.length; j++) {
							Character x = current[j].charAt(0);
							int ascii = x;
							int epsilonPreceding = 0;
							if (j > 0) {
								for (int k = 0; k < j; k++) {
									Character t = current[k].charAt(0);
									int asciiT = t;
									if (asciiT >= 65 && asciiT <= 90 && Firsttable.get(t.toString()).contains("e")) { // if
																														// upper
																														// case
										epsilonPreceding++;
									} else {
										epsilonPreceding = 0;
										break;
									}
								}
							}

							if (j == 0 || epsilonPreceding == j) {

								if (Firsttable.containsKey(current[j])) {
									String dd = Firsttable.get(current[j]);
									if (dd.contains(",e")) {
										dd = dd.replace(",e", "");
									} else if (dd.contains("e")) {
										dd = dd.replace("e", "");
									}
									String[] check = splitString(dd, ",");
									for (int u = 0; u < check.length; u++) {
										if (!(Firsttable.get(key).contains(check[u]))) {
											String str = Firsttable.get(key) + "," + check[u];
											HashSet<String> hs = new HashSet<String>(Arrays.asList(str.split(",")));
											String e = String.join(",", hs);
											Firsttable.put(key, e);
											changed = true;
										}
									}
								} else if (!(ascii >= 65 && ascii <= 90)) {
									if (!Firsttable.get(key).contains(x.toString())) {
										String str = Firsttable.get(key) + "," + x;
										HashSet<String> hs = new HashSet<String>(Arrays.asList(str.split(",")));
										String e = String.join(",", hs);
										Firsttable.put(key, e);
										changed = true;
									}
								}

							}
						}
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
		getFirst(hmap);
		Set<String> keys = hmap.keySet();
		String finalAnswer = "";
		for (String key : keys) {
			String answer = Firsttable.get(key);
			HashSet<String> hs = new HashSet<String>(Arrays.asList(answer.split(",")));
			answer = String.join("", hs);
			finalAnswer += key + "," + answer + ";";
		}
		finalAnswer = finalAnswer.substring(0, finalAnswer.length() - 1);
		System.out.println(finalAnswer);
	}

	public static void main(String[] args) {

		CFG cfg = new CFG("L,SdL,S;T,aSb,iaLb,e;S,ScT,T");
		CFG cfg2 = new CFG("S,aTbS,e;T,aTb,e");
		CFG cfg3 = new CFG("S,SAB,SBC,e;A,aAa,e;B,bB,e;C,cC,e");
		CFG cfg4 = new CFG("S,AB;A,aA,b;B,CA;C,cC,d");
		CFG cfg5 = new CFG("S,lAr,a;A,lArB,aB;B,cSB,e");
		CFG cfg6 = new CFG("S,aA;A,SB,e;B,bA,cA");

		CFG cfg7 = new CFG("S,ABCDZ;A,a,e;B,b,e;C,c;D,d,e;Z,z,e");
		CFG cfg8 = new CFG("Z,TX;X,+TX-,e;T,FV;V,*FV,e;F,(Z),i");
		CFG cfg9 = new CFG("S,ACB,CbB,Ba;A,da,BC;B,g,e;C,h,e");
		CFG cfg10 = new CFG("S,Bb,Cd;B,aB,e;C,cC,e");
		CFG cfg11 = new CFG("S,aBDh;B,cC;C,bC,e;D,EF;E,g,e;F,f,e");
		CFG cfg12 = new CFG("S,A;A,aBF;F,dF,e;B,b;C,g");
		CFG cfg13 = new CFG("S,(L),a;L,SQ;Q,_SQ,e");
		CFG cfg14 = new CFG("S,AaAb,BbBa;A,e;B,e");
		CFG cfg15 = new CFG("E,TQ;Q,+TQ,e;T,FW;W,xFW,e;F,(E),i");
		CFG cfg16 = new CFG("S,ABC,CbB,Ba;A,dA,BC;B,g,e;C,h,e");

		cfg.First();
		cfg2.First();
		cfg3.First();
		cfg4.First();
		cfg5.First();
		cfg6.First();

		cfg7.First();
		cfg8.First();
		cfg9.First();
		cfg10.First();
		cfg11.First();
		cfg12.First();
		cfg13.First();
		cfg14.First();
		cfg15.First();
		cfg16.First();

	}
}
