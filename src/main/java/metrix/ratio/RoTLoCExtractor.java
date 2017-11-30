package metrix.ratio;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.stmt.TryStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;

import model.Measure;
import model.MeasureDataset;
import model.MetricSuite;

/**
 * Ratio of Try Block Lines of Code: 
 * The sum of all try blocks lines of code of given class divided by the number of line of codes of this class.  
 * @author Flavio Freitas
 *
 */
public class RoTLoCExtractor extends VoidVisitorAdapter<JavaParserFacade> {

	@Override
	public void visit(ClassOrInterfaceDeclaration declaration, JavaParserFacade javaParserFacade) {
		super.visit(declaration, javaParserFacade);

		if (!declaration.isInterface() && !declaration.isLocalClassDeclaration() && !declaration.isNestedType()) {
			double tryBlockLineOfCode = 0;
			String qualifiedName = javaParserFacade.getTypeDeclaration(declaration).getQualifiedName();
			for (TryStmt tryStmt : declaration.getChildNodesByType(TryStmt.class)) {
				tryBlockLineOfCode += tryStmt.getEnd().get().line - tryStmt.getBegin().get().line;
			}
			double classLoC = declaration.getEnd().get().line - declaration.getBegin().get().line;
			if (classLoC > 0) {
				tryBlockLineOfCode = tryBlockLineOfCode / classLoC;
			}
			BigDecimal valueAsBigDecimal = new BigDecimal(tryBlockLineOfCode).setScale(4, RoundingMode.HALF_EVEN);

			MeasureDataset.store(qualifiedName, new Measure(MetricSuite.RoTLoC, valueAsBigDecimal.doubleValue()));
		}
	}
}