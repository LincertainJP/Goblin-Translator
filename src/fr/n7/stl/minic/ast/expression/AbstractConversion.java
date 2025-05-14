/**
 *
 */
package fr.n7.stl.minic.ast.expression;

import fr.n7.stl.minic.ast.instruction.declaration.TypeDeclaration;
import fr.n7.stl.minic.ast.scope.Declaration;
import fr.n7.stl.minic.ast.scope.HierarchicalScope;
import fr.n7.stl.minic.ast.type.Type;
import fr.n7.stl.minic.ast.type.TypeErrorException;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.TAMFactory;
import fr.n7.stl.util.Logger;

/**
 * Common elements between left (Assignable) and right (Expression) end sides of assignments. These elements
 * share attributes, toString and getType methods.
 * @author Marc Pantel
 *
 */
public abstract class AbstractConversion<TargetType> implements Expression {

	protected TargetType target;
	protected Type type;
	protected String name;

	public AbstractConversion(TargetType _target, String _type) {
		this.target = _target;
		this.name = _type;
		this.type = null;
	}

	public AbstractConversion(TargetType _target, Type _type) {
		this.target = _target;
		this.name = null;
		this.type = _type;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		if (this.type == null) {
			return "(" + this.name + ") " + this.target;
		} else {
			return "(" + this.type + ") " + this.target;
		}
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Expression#getType()
	 */
	@Override
	public Type getType() {
		return this.type;
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.expression.Expression#collect(fr.n7.stl.block.ast.scope.Scope)
	 */
	@Override
	public boolean collectAndPartialResolve(HierarchicalScope<Declaration> scope) {
		if (!(this.target instanceof Expression)) {
			throw new TypeErrorException("On tue Marc Pantel, ce fils de chacal putride.");
		}else if (this.type == null) {
			if (!(scope.knows(this.name))) {
				Logger.error("Conversion impossible, le nom " + this.name + " est inconnu");
				return false;
			} else if (!(scope.get(this.name) instanceof TypeDeclaration)) {
				Logger.error("Conversion impossible, " + this.name + " n'est pas un type");
				return false;
			} else {
				return ((Expression) this.target).collectAndPartialResolve(scope);
			}
		} else {
			return ((Expression) this.target).collectAndPartialResolve(scope);
		}
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.expression.Expression#resolve(fr.n7.stl.block.ast.scope.Scope)
	 */
	@Override
	public boolean completeResolve(HierarchicalScope<Declaration> scope) {
		return ((Expression) this.target).completeResolve(scope);
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Expression#getCode(fr.n7.stl.tam.ast.TAMFactory)
	 */
	@Override
	public Fragment getCode(TAMFactory factory) {
		Fragment fragConv = factory.createFragment();
		fragConv.append(((Expression) this.target).getCode(factory));
		return fragConv;
	}

}
