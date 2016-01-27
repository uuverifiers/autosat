package elimination;

import java.util.List;

import org.sat4j.specs.ContradictionException;


public interface PairAcceptanceTreeVisitor {
    void visit(List<Integer> v, List<Integer> w, int acceptVar)
                               throws ContradictionException;
}
