/**
 *
 */
package fr.n7.stl.minic.ast.expression.assignable;

import fr.n7.stl.minic.ast.expression.AbstractPointer;
import fr.n7.stl.minic.ast.expression.accessible.AccessibleExpression;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.TAMFactory;

/**
 * Abstract Syntax Tree node for an expression whose computation assigns the content of a pointed cell.
 * @author Marc Pantel
 *
 */
public class PointerAssignment extends AbstractPointer<AccessibleExpression> implements AssignableExpression {

	/**
	 * Construction for the implementation of a pointer content assignment expression Abstract Syntax Tree node.
	 * @param _pointer Abstract Syntax Tree for the pointer expression in a pointer content assignment expression.
	 */
	public PointerAssignment(AccessibleExpression _pointer) {
		super(_pointer);
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.impl.PointerAccessImpl#getCode(fr.n7.stl.tam.ast.TAMFactory)
	 */
	@Override
	public Fragment getCode(TAMFactory factory) {
		Fragment fragPtrAss = factory.createFragment();
		fragPtrAss.append(this.pointer.getCode(factory));
		fragPtrAss.add(factory.createStoreI(this.pointer.getType().length()));
		return fragPtrAss;
	}

}
