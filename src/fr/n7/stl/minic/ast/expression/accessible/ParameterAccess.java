/**
 *
 */
package fr.n7.stl.minic.ast.expression.accessible;

import fr.n7.stl.minic.ast.SemanticsUndefinedException;
import fr.n7.stl.minic.ast.expression.AbstractAccess;
import fr.n7.stl.minic.ast.instruction.declaration.ParameterDeclaration;
import fr.n7.stl.minic.ast.scope.Declaration;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.Register;
import fr.n7.stl.tam.ast.TAMFactory;

/**
 * Implementation of the Abstract Syntax Tree node for a variable access expression.
 * @author Marc Pantel
 */
public class ParameterAccess extends AbstractAccess {

	protected ParameterDeclaration declaration;

	/**
	 * Creates a variable use expression Abstract Syntax Tree node.
	 * @param _name Name of the used variable.
	 */
	public ParameterAccess(ParameterDeclaration _declaration) {
		this.declaration = _declaration;
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.expression.AbstractUse#getDeclaration()
	 */
	@Override
	public Declaration getDeclaration() {
		return this.declaration;
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.expression.AbstractUse#getCode(fr.n7.stl.tam.ast.TAMFactory)
	 */
	@Override
	public Fragment getCode(TAMFactory factory) {
		Fragment fragParAcc = factory.createFragment();
		fragParAcc.add(factory.createLoadA(Register.LB, this.declaration.getOffset()));
		return fragParAcc;
	}

}
