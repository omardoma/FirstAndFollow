import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class Grammar {

    private String startingVariable;
    private Set<String> variables;
    private Set<String> terminals;
    private Map<String, ArrayList<String>> rules;

    public Grammar(String startingVariable, Set<String> variables, Set<String> terminals, Map<String, ArrayList<String>> rules) {
        this.startingVariable = startingVariable;
        this.variables = variables;
        this.terminals = terminals;
        this.rules = rules;
    }

    public String getStartingVariable() {
        return startingVariable;
    }

    public Set<String> getVariables() {
        return variables;
    }

    public Set<String> getTerminals() {
        return terminals;
    }

    public Map<String, ArrayList<String>> getRules() {
        return rules;
    }
}
