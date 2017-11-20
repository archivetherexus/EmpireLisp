package EmpireLisp;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Tyrerexus
 * @date 11/20/17
 */
public class Environment {
    Map<String, Expression> map = new HashMap<>();
    Environment parent;

    public Environment(Environment parent) {
        this.parent = parent;
    };

    public Expression getVariable(String name) {
        Expression result = map.get(name);
        if (result != null) {
            return result;
        }
        else {
            if (parent != null) {
                return parent.getVariable(name);
            }
            else {
                System.out.println("ERROR: Unbound variable!: " + name); // TODO: Throw an exception instead.
                return null;
            }
        }
    }

    public void setVariable(String name, Expression value) {
        map.put(name, value);
    }
}
