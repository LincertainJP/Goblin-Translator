/**
 *
 */
package fr.n7.stl.minic.ast.instruction;

import fr.n7.stl.minic.ast.Block;
import fr.n7.stl.minic.ast.expression.Expression;
import fr.n7.stl.minic.ast.instruction.declaration.FunctionDeclaration;
import fr.n7.stl.minic.ast.scope.Declaration;
import fr.n7.stl.minic.ast.scope.HierarchicalScope;
import fr.n7.stl.minic.ast.type.AtomicType;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.Register;
import fr.n7.stl.tam.ast.TAMFactory;
import fr.n7.stl.util.Logger;

/**
 * Implementation of the Abstract Syntax Tree node for a conditional instruction.
 * @author Marc Pantel
 *
 */
public class Iteration implements Instruction {

	protected Expression condition;
	protected Block body;

	public Iteration(Expression _condition, Block _body) {
		this.condition = _condition;
		this.body = _body;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "while (" + this.condition + " )" + this.body;
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.instruction.Instruction#collect(fr.n7.stl.block.ast.scope.Scope)
	 */
	@Override
	public boolean collectAndPartialResolve(HierarchicalScope<Declaration> scope) {
		boolean ok = this.condition.collectAndPartialResolve(scope);
		if (!ok) {
			Logger.error("Erreur de Type: une condition doit être une variable booléenne.");
			return false;
		}
		return this.body.collectAndPartialResolve(scope);
	}


	@Override
	public boolean collectAndPartialResolve(HierarchicalScope<Declaration> scope, FunctionDeclaration container) {
		boolean ok = this.condition.collectAndPartialResolve(scope);
		if (!ok) {
			Logger.error("Erreur de Type: une condition doit être une variable booléenne.");
			return false;
		}
		return this.body.collectAndPartialResolve(scope, container);
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.instruction.Instruction#resolve(fr.n7.stl.block.ast.scope.Scope)
	 */
	@Override
	public boolean completeResolve(HierarchicalScope<Declaration> scope) {
		boolean ok = this.condition.completeResolve(scope);
		if (!ok) {
			Logger.error("Erreur de Type: une condition doit être une variable booléenne.");
			return false;
		}
		return this.body.completeResolve(scope);
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Instruction#checkType()
	 */
	@Override
	public boolean checkType() {
		if (!(this.condition.getType().equalsTo(AtomicType.BooleanType))) {
			Logger.error("La condition dans une boucle while doit être un booléen et pas un "+ this.condition.getType().toString());
			return false;
		}
		return this.body.checkType();
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Instruction#allocateMemory(fr.n7.stl.tam.ast.Register, int)
	 */
	@Override
	public int allocateMemory(Register register, int offset) {
		this.body.allocateMemory(register, offset);
		return offset;
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Instruction#getCode(fr.n7.stl.tam.ast.TAMFactory)
	 */
	@Override
	public Fragment getCode(TAMFactory factory) {
		String lNum = String.valueOf(factory.createLabelNumber());
		Fragment fragIter = factory.createFragment();

		fragIter.append(this.condition.getCode(factory));
		fragIter.add(factory.createJumpIf("finboucle" + lNum, 0));
		fragIter.addPrefix("deboucle" + lNum);

		Fragment fragBody = factory.createFragment();
		fragBody.append(this.body.getCode(factory));
		fragBody.add(factory.createJump("deboucle" + lNum));
		fragBody.addSuffix("finboucle" + lNum);

		fragIter.append(fragBody);

		return fragIter;	}

}
