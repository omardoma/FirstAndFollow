import java.util.*;

public class FirstAndFollow {
    public static final String EPSILON = "$";

    private static boolean isSubset(Set<String> setA, Set<String> setB) {
        if (setA == null || setB == null) {
            return false;
        }
        boolean aContainsEpsilon = false;
        boolean bContainsEpsilon = false;
        boolean returnValue;
        if (!setA.isEmpty() && setA.contains(EPSILON)) {
            aContainsEpsilon = true;
        } else {
            setA.add(EPSILON);
        }
        if (!setB.isEmpty() && setB.contains(EPSILON)) {
            bContainsEpsilon = true;
        } else {
            setB.add(EPSILON);
        }
        if (setB.containsAll(setA)) {
            returnValue = true;
        } else
            returnValue = false;
        if (!aContainsEpsilon) {
            setA.remove(EPSILON);
        }
        if (!bContainsEpsilon) {
            setB.remove(EPSILON);
        }
        return returnValue;
    }

    private static String[] parseProduction(String production) {
        return production.split("-");
    }

    public static Map<String, Set<String>> computeFirst(Grammar g) {
        Map<String, Set<String>> first = new LinkedHashMap<>();
        for (String t : g.getTerminals()) {
            first.put(t, new HashSet<>(Collections.singletonList(t)));
        }
        for (String v : g.getVariables()) {
            first.put(v, new HashSet<>(Collections.emptyList()));
        }

        boolean change = true;
        String[] productionArray;

        while (change) {
            change = false;
            for (Map.Entry<String, ArrayList<String>> entry : g.getRules().entrySet()) {
                for (String production : entry.getValue()) {
                    productionArray = parseProduction(production);

                    if (first.get(productionArray[0]).contains(EPSILON)) {
                        if (!first.get(entry.getKey()).contains(EPSILON)) {
                            first.get(entry.getKey()).add(EPSILON);
                            change = true;
                        }
                    }
                    // To handle rules containing left Recursion
                    if (productionArray[0].equals(entry.getKey())) {
                        for (int i = 0; i < productionArray.length - 1; i++) {
                            if (first.get(productionArray[i]).contains(EPSILON)) {
                                first.get(entry.getKey()).addAll(first.get(productionArray[i + 1]));
                            }
                        }
                    }

                    if (!first.get(productionArray[0]).isEmpty() && !isSubset(first.get(productionArray[0]), first.get(entry.getKey()))) {
                        first.get(entry.getKey()).addAll(first.get(productionArray[0]));
                        change = true;
                    }
                }
            }
        }
        return first;
    }

    public static Map<String, Set<String>> computeFollow(Grammar g) {
        Map<String, Set<String>> first = new LinkedHashMap<>(computeFirst(g));
        Map<String, Set<String>> follow = new LinkedHashMap<>();
        for (String v : g.getVariables()) {
            if (v.equals(g.getStartingVariable())) {
                follow.put(v, new HashSet<>(Collections.singleton(EPSILON)));
            } else
                follow.put(v, new HashSet<>(Collections.emptyList()));
        }

        for (String t : g.getTerminals()) {
            follow.put(t, new HashSet<>(Collections.emptyList()));
        }
        boolean change = true;
        while (change) {
            change = false;
            String[] productionArray;
            for (Map.Entry<String, ArrayList<String>> entry : g.getRules().entrySet()) {
                for (String production : entry.getValue()) {
                    productionArray = parseProduction(production);
                    if (productionArray.length >= 3) {
                        for (int i = productionArray.length; i > 1; i--) {
                            if (!isSubset(first.get(productionArray[productionArray.length - i + 1]), follow.get(productionArray[productionArray.length - i]))) {
                                follow.get(productionArray[productionArray.length - i]).addAll(first.get(productionArray[productionArray.length - i + 1]));
                                change = true;
                            }
                        }

                        if (g.getVariables().contains(productionArray[productionArray.length - 2])) {
                            if (first.get(productionArray[productionArray.length - 1]).contains(EPSILON)) {
                                follow.get(productionArray[productionArray.length - 2]).addAll(follow.get(productionArray[productionArray.length - 1]));
                            }
                        }
                    } else {
                        if (productionArray.length >= 2) {
                            for (int i = productionArray.length; i > 1; i--) {
                                if (first.get(productionArray[productionArray.length - i + 1]).contains(EPSILON)) {
                                    if (!isSubset(follow.get(entry.getKey()), follow.get(productionArray[productionArray.length - i]))) {
                                        follow.get(productionArray[productionArray.length - i]).addAll(follow.get(entry.getKey()));
                                        change = true;
                                    }
                                }
                            }
                        }
                    }

                    if (g.getVariables().contains(productionArray[productionArray.length - 1])) {
                        follow.get(productionArray[productionArray.length - 1]).addAll(follow.get(entry.getKey()));
                        follow.get(entry.getKey()).addAll(follow.get(productionArray[productionArray.length - 1]));
                    }
                }
            }
        }
        return follow;
    }

    public static void toStringFirst(Grammar g, Map<String, Set<String>> first) {
        System.out.println("First:");
        for (String v : g.getVariables()) {
            System.out.println(v + " = " + first.get(v));
        }
        System.out.println();
    }

    public static void toStringFollow(Grammar g, Map<String, Set<String>> follow) {
        System.out.println("Follow:");
        for (String v : g.getVariables()) {
            System.out.println(v + " = " + follow.get(v));
        }
        System.out.println();
    }

    public static void main(String[] args) {
        /*
            Grammar1
            S -> aSb | T
            T -> aT | $
         */
        String startingVariable1 = "S";
        Set<String> v1 = new HashSet<>();
        v1.add("S");
        v1.add("T");
        Set<String> t1 = new HashSet<>();
        t1.add("a");
        t1.add("b");
        t1.add(EPSILON);
        Map<String, ArrayList<String>> rules1 = new LinkedHashMap<>();
        ArrayList<String> list11 = new ArrayList<>();
        list11.add("a-S-b");
        list11.add("T");
        rules1.put("S", list11);
        ArrayList<String> list12 = new ArrayList<>();
        list12.add("a-T");
        list12.add(EPSILON);
        rules1.put("T", list12);
        Grammar g1 = new Grammar(startingVariable1, v1, t1, rules1);
        toStringFirst(g1, computeFirst(g1));
        toStringFollow(g1, computeFollow(g1));

      /* Grammar2
        E -> TE'
        E' -> +TE' | $
        T -> FT'
        T' -> *FT'|$
        F -> (E)|id
      */
        String startingVariable2 = "E";
        Set<String> v2 = new HashSet<>();
        v2.add("E");
        v2.add("E'");
        v2.add("T");
        v2.add("T'");
        v2.add("F");
        Set<String> t2 = new HashSet<>();
        t2.add("+");
        t2.add("*");
        t2.add(")");
        t2.add("(");
        t2.add("id");
        t2.add(EPSILON);
        Map<String, ArrayList<String>> rules2 = new HashMap<>();
        ArrayList<String> list21 = new ArrayList<>();
        list21.add("T-E'");
        rules2.put("E", list21);
        ArrayList<String> list22 = new ArrayList<>();
        list22.add("+-T-E'");
        list22.add(EPSILON);
        rules2.put("E'", list22);
        ArrayList<String> list23 = new ArrayList<>();
        list23.add("F-T'");
        rules2.put("T", list23);
        ArrayList<String> list24 = new ArrayList<>();
        list24.add("*-F-T'");
        list24.add(EPSILON);
        rules2.put("T'", list24);
        ArrayList<String> list25 = new ArrayList<>();
        list25.add("(-E-)");
        list25.add("id");
        rules2.put("F", list25);
        Grammar g2 = new Grammar(startingVariable2, v2, t2, rules2);
        toStringFirst(g2, computeFirst(g2));
        toStringFollow(g2, computeFollow(g2));

 /*  Grammar3 with left recursion
        S -> (L) | a
        L -> L,S | S
      */
        System.out.println("Grammar 3 with left recursion");
        String startingVariable3_1 = "S";
        Set<String> v3_1 = new HashSet<>();
        v3_1.add("S");
        v3_1.add("L");
        Set<String> t3_1 = new HashSet<>();
        t3_1.add("(");
        t3_1.add(")");
        t3_1.add(",");
        t3_1.add("a");
        t3_1.add(EPSILON);
        Map<String, ArrayList<String>> rules3_1 = new HashMap<>();
        ArrayList<String> list31_1 = new ArrayList<>();
        list31_1.add("(-L-)");
        list31_1.add("a");
        rules3_1.put("S", list31_1);
        ArrayList<String> list32_1 = new ArrayList<>();
        list32_1.add("L-,-S");
        list32_1.add("S");
        rules3_1.put("L", list32_1);
        Grammar g3_1 = new Grammar(startingVariable3_1, v3_1, t3_1, rules3_1);
        toStringFirst(g3_1, computeFirst(g3_1));
        toStringFollow(g3_1, computeFollow(g3_1));

        /* Grammar3
        S -> (L) | a
        L -> (L)L' | aL'
        L' -> ,SL' | $
        */
        String startingVariable3 = "S";
        Set<String> v3 = new HashSet<>();
        v3.add("S");
        v3.add("L");
        v3.add("L'");
        Set<String> t3 = new HashSet<>();
        t3.add("(");
        t3.add(")");
        t3.add(",");
        t3.add("a");
        t3.add(EPSILON);
        Map<String, ArrayList<String>> rules3 = new HashMap<>();
        ArrayList<String> list31 = new ArrayList<>();
        list31.add("(-L-)");
        list31.add("a");
        rules3.put("S", list31);
        ArrayList<String> list32 = new ArrayList<>();
        list32.add("(-L-)-L'");
        list32.add("a-L'");
        rules3.put("L", list32);
        ArrayList<String> list33 = new ArrayList<>();
        list33.add(",-S-L'");
        list33.add(EPSILON);
        rules3.put("L'", list33);
        Grammar g3 = new Grammar(startingVariable3, v3, t3, rules3);
        toStringFirst(g3, computeFirst(g3));
        toStringFollow(g3, computeFollow(g3));

        /*Grammar 4
        S -> aS'
        S' -> SXS' | $
        X -> + | *
        */
        String startingVariable4 = "S";
        Set<String> v4 = new HashSet<>();
        v4.add("S");
        v4.add("S'");
        v4.add("X");
        Set<String> t4 = new HashSet<>();
        t4.add("*");
        t4.add("+");
        t4.add("a");
        t4.add(EPSILON);
        Map<String, ArrayList<String>> rules4 = new HashMap<>();
        ArrayList<String> list41 = new ArrayList<>();
        list41.add("a-S'");
        rules4.put("S", list41);
        ArrayList<String> list42 = new ArrayList<>();
        list42.add("S-X-S'");
        list42.add(EPSILON);
        rules4.put("S'", list42);
        ArrayList<String> list43 = new ArrayList<>();
        list43.add("+");
        list43.add("*");
        rules4.put("X", list43);
        Grammar g4 = new Grammar(startingVariable4, v4, t4, rules4);
        toStringFirst(g4, computeFirst(g4));
        toStringFollow(g4, computeFollow(g4));

         /*Grammar 4 with left recursion
        S -> SS+ , SS* , a
        */
        System.out.println("Grammar 4 with left recursion");
        String startingVariable4_1 = "S";
        Set<String> v4_1 = new HashSet<>();
        v4_1.add("S");
        Set<String> t4_1 = new HashSet<>();
        t4_1.add("*");
        t4_1.add("+");
        t4_1.add("a");
        t4_1.add(EPSILON);
        Map<String, ArrayList<String>> rules4_1 = new HashMap<>();
        ArrayList<String> list41_1 = new ArrayList<>();
        list41_1.add("S-S-+");
        list41_1.add("S-S-*");
        list41_1.add("a");
        rules4_1.put("S", list41_1);
        Grammar g4_1 = new Grammar(startingVariable4_1, v4_1, t4_1, rules4_1);
        toStringFirst(g4_1, computeFirst(g4_1));
        toStringFollow(g4_1, computeFollow(g4_1));


        /*Grammar 5
        S -> SAB | SBC
        A -> aAa | $
        B -> bB | $
        C -> cC | $
        */
        String startingVariable5 = "S";
        Set<String> v5 = new HashSet<>();
        v5.add("S");
        v5.add("A");
        v5.add("B");
        v5.add("C");
        Set<String> t5 = new HashSet<>();
        t5.add("a");
        t5.add("b");
        t5.add("c");
        t5.add(EPSILON);
        Map<String, ArrayList<String>> rules5 = new LinkedHashMap<>();
        ArrayList<String> list51 = new ArrayList<>();
        list51.add("S-A-B");
        list51.add("S-B-C");
        list51.add(EPSILON);
        rules5.put("S", list51);
        ArrayList<String> list52 = new ArrayList<>();
        list52.add("a-A-a");
        list52.add(EPSILON);
        rules5.put("A", list52);
        ArrayList<String> list53 = new ArrayList<>();
        list53.add("b-B");
        list53.add(EPSILON);
        rules5.put("B", list53);
        ArrayList<String> list54 = new ArrayList<>();
        list54.add("c-C");
        list54.add(EPSILON);
        rules5.put("C", list54);
        Grammar g5 = new Grammar(startingVariable5, v5, t5, rules5);
        toStringFirst(g5, computeFirst(g5));
        toStringFollow(g5, computeFollow(g5));

        /*Grammar 6
        S -> 0T1S | $
        T -> 0T1 | $
        */
        String startingVariable6 = "S";
        Set<String> v6 = new HashSet<>();
        v6.add("S");
        v6.add("T");
        Set<String> t6 = new HashSet<>();
        t6.add("0");
        t6.add("1");
        t6.add(EPSILON);
        Map<String, ArrayList<String>> rules6 = new LinkedHashMap<>();
        ArrayList<String> list61 = new ArrayList<>();
        list61.add("0-T-1-S");
        list61.add(EPSILON);
        rules6.put("S", list61);
        ArrayList<String> list62 = new ArrayList<>();
        list62.add("0-T-1");
        list62.add(EPSILON);
        rules6.put("T", list62);
        Grammar g6 = new Grammar(startingVariable6, v6, t6, rules6);
        toStringFirst(g6, computeFirst(g6));
        toStringFollow(g6, computeFollow(g6));
    }

}
