/**
 *
 */
package fr.n7.stl.minic.ast.expression.accessible;

import fr.n7.stl.minic.ast.expression.assignable.AssignableExpression;
import fr.n7.stl.minic.ast.scope.Declaration;
import fr.n7.stl.minic.ast.scope.HierarchicalScope;
import fr.n7.stl.minic.ast.type.NamedType;
import fr.n7.stl.minic.ast.type.PointerType;
import fr.n7.stl.minic.ast.type.Type;
import fr.n7.stl.minic.ast.type.TypeErrorException;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.TAMFactory;

/**
 * Implementation of the Abstract Syntax Tree node for accessing an expression address.
 * @author Marc Pantel
 *
 */
public class AddressAccess implements AccessibleExpression {

	protected AssignableExpression assignable;

	public AddressAccess(AssignableExpression _assignable) {
		this.assignable = _assignable;
	}

	@Override
	public String toString() {
		return "& " + this.assignable.toString();
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.expression.Expression#collect(fr.n7.stl.block.ast.scope.Scope)
	 */
	@Override
	public boolean collectAndPartialResolve(HierarchicalScope<Declaration> scope) {
		return this.assignable.collectAndPartialResolve(scope);
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.expression.Expression#resolve(fr.n7.stl.block.ast.scope.Scope)
	 */
	@Override
	public boolean completeResolve(HierarchicalScope<Declaration> scope) {
		return this.assignable.completeResolve(scope);
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Expression#getType()
	 */
	@Override
	public Type getType() {
		Type type = this.assignable.getType();
		if (type instanceof NamedType nameT) {
			type = nameT.getType();
		}
		if (type instanceof PointerType ptrT) {
			return ptrT.getPointedType();
		}
		throw new TypeErrorException("tentative de déreferencer le type " +
				this.assignable.getType().toString() + " qui n'est pas un pointeur, opération impossible");
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Expression#getCode(fr.n7.stl.tam.ast.TAMFactory)
	 */
	@Override
	public Fragment getCode(TAMFactory factory) {
		Fragment fragAdrAcc = factory.createFragment();
		fragAdrAcc.append(this.assignable.getCode(factory));
		fragAdrAcc.add(factory.createLoadI(this.assignable.getType().length()));
		return fragAdrAcc;
	}

}
