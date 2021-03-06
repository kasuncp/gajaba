package org.gajaba.rule.compiler;

import org.antlr.runtime.tree.Tree;
import org.gajaba.rule.core.DSLEngine;
import org.gajaba.rule.parse.GajabaDSLLexer;

import javax.script.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GajabaDSLCompliedScript extends CompiledScript {

    public static final String CLIENTS = "clients";
    private DSLEngine engine;
    private Set<Tree> variables;
    private Class classes[];

    public GajabaDSLCompliedScript(DSLEngine engine, Set<Tree> vars) {
        this.engine = engine;
        this.variables = vars;

        int i = 1;
        classes = new Class[vars.size()+1];
        classes[0] = List.class;
        for (Tree next : vars) {
            Class c;
            if (next.getType() == GajabaDSLLexer.INPUT_VAR) {
                c = String.class;
            } else {
                c = Map.class;
            }
            classes[i++] = c;
        }
    }

    @Override
    public Object eval(ScriptContext context) throws ScriptException {
        Bindings bindings = context.getBindings(ScriptContext.ENGINE_SCOPE);
        return eval(bindings);
    }

    @Override
    public Object eval(Bindings bindings) throws ScriptException {

        List arguments = new ArrayList();

        arguments.add(bindings.get(CLIENTS));

        for (Tree next : variables) {
            arguments.add(bindings.get(next.getChild(0).getText()));
        }

        try {
            return engine.invokeFunction("main", classes, arguments.toArray());
        } catch (NoSuchMethodException e) {
            throw new ScriptException(e);
        }
    }

    @Override
    public Object eval() throws ScriptException {
        Bindings bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);
        return eval(bindings);
    }

    @Override
    public ScriptEngine getEngine() {
        return engine;
    }

}
