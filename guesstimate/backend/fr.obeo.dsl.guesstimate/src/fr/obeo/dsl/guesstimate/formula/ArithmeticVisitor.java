package fr.obeo.dsl.guesstimate.formula;

import com.google.common.collect.Lists;
import com.google.common.primitives.Doubles;

import java.util.List;

import org.petitparser.context.Result;

public class ArithmeticVisitor {

    public Object visit(Result r) {
        if (r.isSuccess()) {
            Object vals = r.get();
            return doVisitAndDispatch(vals);
        }
        return null;
    }

    /**
     * @param vals
     */
    private Object doVisitAndDispatch(Object vals) {
        List<Object> results = Lists.newArrayList();
        if (vals instanceof List) {
            for (Object child : (List<?>) vals) {
                dispatchOnSingleObject(results, child);
            }
        } else {
            dispatchOnSingleObject(results, vals);
        }
        Object result = aggregate(results);
        return result;
    }

    /**
     * @param results
     * @param child
     */
    private void dispatchOnSingleObject(List<Object> results, Object child) {
        if (child instanceof Character) {
            Object result = caseChar(((Character) child));
            if (result != null) {
                results.add(result);
            }

        } else if (child instanceof Number) {
            Object r = caseNumber((Number) child);
            if (r != null) {
                results.add(r);
            }
        } else if (child instanceof String) {
            Double doubleVal = Doubles.tryParse((String) child);
            if (doubleVal != null) {
                Object r = caseNumber(doubleVal);
                if (r != null) {
                    results.add(r);
                }
            } else {
                Object r = caseString((String) child);
                if (r != null) {
                    results.add(r);
                }
            }
        } else if (child instanceof List) {
            Object childRes = doVisitAndDispatch(child);
            if (childRes != null) {
                results.add(childRes);
            }
        } else {
            System.err.println("Unknown: " + child.getClass() + " / " + child);
        }
    }

    public Object aggregate(List<Object> results) {
        return results;
    }

    public Object caseString(String child) {
        return child;

    }

    public Object caseNumber(Number child) {
        return child;

    }

    public Object caseChar(Character child) {
        return child;
    }
}
