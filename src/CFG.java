
//T14_37_10683_Marwa_Gaser
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;

public class CFG {
	String input;
	static HashMap<String, String> Firsttable;
	static HashMap<String, String> Followtable;

	public CFG(String input) {
		this.input = input;
		Firsttable = new HashMap<String, String>();
		Followtable = new HashMap<String, String>();
	}

	public static String[] splitString(String s, String operator) {
		String[] sections = s.split(operator);
		return sections;
	}

	public void getFirst(LinkedHashMap<String, String> hmap) {
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
			for (String key : keys) { // for every rule in the CFG
				String value = hmap.get(key);
				String[] sentential_form = splitString(value, ","); // get its right hand side in an array
				int length = sentential_form.length;
				for (int i = 0; i < length; i++) { // for each sentential form in the RHS
					String[] current = splitString(sentential_form[i], ""); // populate an array with the letters of
																			// that sentential form
					int epsilonCount = 0; // a counter to count the number of variables that go to epsilon in that rule
					for (int j = 0; j < current.length; j++) { // for each letter in the sentential form
						Character x = current[j].charAt(0);
						int ascii = x; // get its ASCII
						if (ascii >= 65 && ascii <= 90 && Firsttable.get(x.toString()).contains("e")) { // if upper case
																										// and its first
																										// has epsilon
							epsilonCount++; // increment the epsilon counter
						} else { // otherwise
							epsilonCount = 0; // set the counter to zero
							break;
						}

					}
					if (epsilonCount == current.length) { // if the count of epsilons = the length of the sentential
															// form, this means that the LHS variable should have
															// epsilon in its First
						// do the if logic
						if (!Firsttable.get(key).contains("e")) { // if the LHS variable doesn't have epsilon in its
																	// First
							String str = Firsttable.get(key) + ",e"; // add epsilon to its First
							HashSet<String> hs = new HashSet<String>(Arrays.asList(str.split(",")));
							String e = String.join(",", hs);
							Firsttable.put(key, e);
							changed = true; // set the boolean to true as the Firsttable hash table was modified
						}
					}
					for (int z = 0; z < length; z++) { // For each sentential form
						for (int j = 0; j < current.length; j++) { // loop on every varaible/letter on the RHS of every
																	// sentential form
							Character x = current[j].charAt(0);
							int ascii = x;
							int epsilonPreceding = 0; // a counter that checks the number of epsilons preceding that
														// variable/letter
							if (j > 0) { // if the letter is at a position greater than the first
								for (int k = 0; k < j; k++) { // loop on every variable/terminal before it
									Character t = current[k].charAt(0);
									int asciiT = t;
									if (asciiT >= 65 && asciiT <= 90 && Firsttable.get(t.toString()).contains("e")) { // if
																														// variable
																														// has
																														// an
																														// epsilon
										epsilonPreceding++; // increment epsilonPreceding count
									} else { // otherwise
										epsilonPreceding = 0; // set it to zero
										break;
									}
								}
							}

							if (j == 0 || epsilonPreceding == j) { // if the variable or terminal is at position zero,
																	// or the all preceding letters have epsilon in
																	// their first

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

	public void getFollow(LinkedHashMap<String, String> hmap) {
		boolean changed = false;
		Followtable.clear();
		Set<String> keys = hmap.keySet();
		boolean setFirstVar = false;
		for (String key : keys) {
			if (setFirstVar == false) {
				Followtable.put(key, "$");
				setFirstVar = true;
				changed = true;
			} else {
				Followtable.put(key, "");
			}
		}
		while (changed) {

			changed = false;
			for (String key : keys) {
				String[] sentential_forms = splitString(hmap.get(key), ",");
				for (int i = 0; i < sentential_forms.length; i++) {
					String[] current = splitString(sentential_forms[i], ""); // word letter by letter
					for (int j = 0; j < current.length; j++) { // for each letter
						Character x = current[j].charAt(0);
						int ascii = x;
						if (ascii >= 65 && ascii <= 90) { // if its a variable
							if (j + 1 < current.length) {
								for (int p = j + 1; p < current.length; p++) {
									String newString;
									Character p1 = current[p].charAt(0);
									int asciiP = p1;
									if (asciiP >= 65 && asciiP <= 90) { // if p is a variable
										if (!(Firsttable.get(current[p]).contains("e"))) { // add and leave
											newString = (Firsttable.get(current[p]));
											String[] newS = splitString(newString, ",");
											for (int h = 0; h < newS.length; h++) {
												if (!(Followtable.get(current[j]).contains(newS[h]))) {
													if (Followtable.get(current[j]).equals("")) {
														String value = newS[h];
														Followtable.put(current[j], value);
														changed = true;
													} else {
														String value = Followtable.get(current[j]) + "," + newS[h];
														HashSet<String> hs = new HashSet<String>(
																Arrays.asList(value.split(",")));
														value = String.join(",", hs);
														Followtable.put(current[j], value);
														changed = true;
													}
												}

											}
											break;
										} else if ((Firsttable.get(current[p]).contains(",e"))) { // add and stay bc
																									// epsilon
											newString = (Firsttable.get(current[p]).replace(",e", ""));
											String[] newS = splitString(newString, ",");
											for (int h = 0; h < newS.length; h++) {
												if (!(Followtable.get(current[j]).contains(newS[h]))) {
													if (Followtable.get(current[j]).equals("")) {
														String value = newS[h];
														Followtable.put(current[j], value);
														changed = true;
													} else {
														String value = Followtable.get(current[j]) + "," + newS[h];
														HashSet<String> hs = new HashSet<String>(
																Arrays.asList(value.split(",")));
														value = String.join(",", hs);
														Followtable.put(current[j], value);
														changed = true;
													}
												}

											}
											if (p == current.length - 1) {
												String followLHS = Followtable.get(key);
												String[] LHSarr = splitString(followLHS, ",");
												for (int r = 0; r < LHSarr.length; r++) {
													if (!(Followtable.get(current[j]).contains(LHSarr[r]))) {
														if (Followtable.get(current[j]).equals("")) {
															Followtable.put(current[j], LHSarr[r]);
															changed = true;
														} else {
															String value = Followtable.get(current[j]) + ","
																	+ LHSarr[r];
															HashSet<String> hs = new HashSet<String>(
																	Arrays.asList(value.split(",")));
															value = String.join(",", hs);
															Followtable.put(current[j], value);
															changed = true;
														}
													}
												}
												// add the follow of parent to me too
											}
										} else if ((Firsttable.get(current[p]).contains("e"))) { // add and staybc
																									// epsilon
											newString = (Firsttable.get(current[p]).replace("e", ""));
											String[] newS = splitString(newString, ",");
											for (int h = 0; h < newS.length; h++) {
												if (!(Followtable.get(current[j]).contains(newS[h]))) {
													if (Followtable.get(current[j]).equals("")) {
														String value = newS[h];
														Followtable.put(current[j], value);
														changed = true;
													} else {
														String value = Followtable.get(current[j]) + "," + newS[h];
														HashSet<String> hs = new HashSet<String>(
																Arrays.asList(value.split(",")));
														value = String.join(",", hs);
														Followtable.put(current[j], value);
														changed = true;
													}
												}

											}
											if (p == current.length - 1) {

												String followLHS = Followtable.get(key);
												String[] LHSarr = splitString(followLHS, ",");
												for (int r = 0; r < LHSarr.length; r++) {
													if (!(Followtable.get(current[j]).contains(LHSarr[r]))) {
														if (Followtable.get(current[j]).equals("")) {
															Followtable.put(current[j], LHSarr[r]);
															changed = true;
														} else {
															String value = Followtable.get(current[j]) + ","
																	+ LHSarr[r];
															HashSet<String> hs = new HashSet<String>(
																	Arrays.asList(value.split(",")));
															value = String.join(",", hs);
															Followtable.put(current[j], value);
															changed = true;
														}
													}
												}
												// add the follow of parent to me too
											}
										}
									} else {

										if (!(Followtable.get(current[j]).contains(current[p]))) {
											if (Followtable.get(current[j]).equals("")) {
												String value = current[p];
												Followtable.put(current[j], value);
												changed = true;
											} else {
												String value = Followtable.get(current[j]) + "," + current[p];
												HashSet<String> hs = new HashSet<String>(
														Arrays.asList(value.split(",")));
												value = String.join(",", hs);
												Followtable.put(current[j], value);
												changed = true;
											}
										}

										break;
									}

								}
							} else if (j + 1 == current.length) {
								// if im the last var my follow is the parents follow
								String followLHS = Followtable.get(key);
								String[] LHSarr = splitString(followLHS, ",");
								for (int r = 0; r < LHSarr.length; r++) {
									if (!(Followtable.get(current[j]).contains(LHSarr[r]))) {
										if (Followtable.get(current[j]).equals("")) {
											Followtable.put(current[j], LHSarr[r]);
											changed = true;
										} else {
											String value = Followtable.get(current[j]) + "," + LHSarr[r];
											HashSet<String> hs = new HashSet<String>(Arrays.asList(value.split(",")));
											value = String.join(",", hs);
											Followtable.put(current[j], value);
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
	}

	public String First() {

		LinkedHashMap<String, String> hmap = new LinkedHashMap<String, String>();
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
			char answerArray[] = answer.toCharArray();
			Arrays.sort(answerArray);
			answer = new String(answerArray);
			finalAnswer += key + "," + answer + ";";
		}
		finalAnswer = finalAnswer.substring(0, finalAnswer.length() - 1);
		return finalAnswer;
	}

	public String Follow() {
		LinkedHashMap<String, String> hmap = new LinkedHashMap<String, String>();
		String[] CFGrules = splitString(this.input, ";");
		for (int i = 0; i < CFGrules.length; i++) {
			String key = CFGrules[i].substring(0, 1);
			String value = CFGrules[i].substring(2);

			hmap.put(key, value);

		}

		getFollow(hmap);
		Set<String> keys = hmap.keySet();
		String finalAnswer = "";
		for (String key : keys) {
			String answer = Followtable.get(key);

			HashSet<String> hs = new HashSet<String>(Arrays.asList(answer.split(",")));
			answer = String.join("", hs);
			char answerArray[] = answer.toCharArray();
			Arrays.sort(answerArray);
			answer = new String(answerArray);
			if (answer.contains("$")) {
				answer = answer.replace("$", "");
				answer = answer + "$";
			}

			finalAnswer += key + "," + answer + ";";
		}
		finalAnswer = finalAnswer.substring(0, finalAnswer.length() - 1);
		return finalAnswer;
	}

	public static void main(String[] args) {
		String input = "S,ScT,T;T,aSb,iaLb,e;L,SdL,S";
		CFG cfg = new CFG(input);
		String firstEncoding = cfg.First();
		String followEncoding = cfg.Follow();
		System.out.println("First: " + firstEncoding);
		System.out.println("Follow: " + followEncoding);
	}
}
