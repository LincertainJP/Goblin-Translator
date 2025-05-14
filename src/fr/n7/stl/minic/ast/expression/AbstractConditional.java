/**
 * 
 */
package fr.n7.stl.minic.ast.expression;

import fr.n7.stl.minic.ast.SemanticsUndefinedException;
import fr.n7.stl.minic.ast.scope.Declaration;
import fr.n7.stl.minic.ast.scope.HierarchicalScope;
import fr.n7.stl.minic.ast.type.AtomicType;
import fr.n7.stl.minic.ast.type.Type;
import fr.n7.stl.minic.ast.type.TypeErrorException;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.TAMFactory;
import fr.n7.stl.util.Logger;

/**
 * Abstract Syntax Tree node for a conditional expression.
 * @author Marc Pantel
 *
 */
public class AbstractConditional<ExpressionKind extends Expression> implements Expression {

	/**
	 * AST node for the expression whose value is the condition for the conditional expression.
	 */
	protected Expression condition;
	
	/**
	 * AST node for the expression whose value is the then parameter for the conditional expression.
	 */
	protected ExpressionKind thenExpression;
	
	/**
	 * AST node for the expression whose value is the else parameter for the conditional expression.
	 */
	protected ExpressionKind elseExpression;
	
	/**
	 * Builds a binary expression Abstract Syntax Tree node from the left and right sub-expressions
	 * and the binary operation.
	 * @param _left : Expression for the left parameter.
	 * @param _operator : Binary Operator.
	 * @param _right : Expression for the right parameter.
	 */
	public AbstractConditional(Expression _condition, ExpressionKind _then, ExpressionKind _else) {
		this.condition = _condition;
		this.thenExpression = _then;
		this.elseExpression = _else;
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.expression.Expression#collect(fr.n7.stl.block.ast.scope.Scope)
	 */
	@Override
	public boolean collectAndPartialResolve(HierarchicalScope<Declaration> scope) {
		return this.condition.collectAndPartialResolve(scope) && this.thenExpression.collectAndPartialResolve(scope) 
				&& this.elseExpression.collectAndPartialResolve(scope);
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.expression.Expression#resolve(fr.n7.stl.block.ast.scope.Scope)
	 */
	@Override
	public boolean completeResolve(HierarchicalScope<Declaration> scope) {
		return this.condition.completeResolve(scope) && this.thenExpression.completeResolve(scope) 
				&& this.elseExpression.completeResolve(scope);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "(" + this.condition + " ? " + this.thenExpression + " : " + this.elseExpression + ")";
	}
	
	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Expression#getType()
	 */
	@Override
	public Type getType() {
		if(!(this.condition.getType().compatibleWith(AtomicType.BooleanType))) {
			Logger.error("La condition dans un opérateur ternaire doit être un booléen.");
			throw new TypeErrorException(this.condition.getType().toString() + " incompatible with AtomicType.BooleanType");
		} else if (!(this.elseExpression.getType().compatibleWith(this.thenExpression.getType()) )) {
			Logger.error("Dans un opérateur ternaire, le type de l'expression else "
					+ "et le type l'expression then doivent être compatibles");
			throw new TypeErrorException(this.elseExpression.getType().toString() + " incompatible with " 
					+ this.thenExpression.getType().toString());
		}
		return this.elseExpression.getType().merge(this.thenExpression.getType());
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Expression#getCode(fr.n7.stl.tam.ast.TAMFactory)
	 */
	@Override
	public Fragment getCode(TAMFactory factory) {
		Fragment fragAbsCond = factory.createFragment();
		int lNum = factory.createLabelNumber();
		fragAbsCond.append(this.condition.getCode(factory));
		fragAbsCond.add(factory.createJumpIf("if" + lNum, 1));
		fragAbsCond.append(this.elseExpression.getCode(factory));
		fragAbsCond.add(factory.createJump("fi" + lNum));

		Fragment fragAbsCondIf = factory.createFragment();
		fragAbsCondIf.append(this.thenExpression.getCode(factory));
		fragAbsCondIf.addPrefix("if" + lNum);
		fragAbsCondIf.addSuffix("fi" + lNum);

		fragAbsCond.append(fragAbsCondIf);
		
		return fragAbsCond;
	}
}
