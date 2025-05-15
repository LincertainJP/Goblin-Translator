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
public class Conditional implements Instruction {


	protected Expression condition;
	protected Block thenBranch;
	protected Block elseBranch;

	public Conditional(Expression _condition, Block _then, Block _else) {
		this.condition = _condition;
		this.thenBranch = _then;
		this.elseBranch = _else;
	}

	public Conditional(Expression _condition, Block _then) {
		this.condition = _condition;
		this.thenBranch = _then;
		this.elseBranch = null;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "if (" + this.condition + " )" + this.thenBranch + ((this.elseBranch != null)?(" else " + this.elseBranch):"");
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.instruction.Instruction#collect(fr.n7.stl.block.ast.scope.Scope)
	 */
	@Override
	public boolean collectAndPartialResolve(HierarchicalScope<Declaration> scope) {
		if (this.elseBranch != null) {
			if (!this.elseBranch.collectAndPartialResolve(scope)) {
				return false;
			}
		}
		return (this.thenBranch.collectAndPartialResolve(scope) && this.condition.collectAndPartialResolve(scope));
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.instruction.Instruction#collect(fr.n7.stl.block.ast.scope.Scope)
	 */
	@Override
	public boolean collectAndPartialResolve(HierarchicalScope<Declaration> scope, FunctionDeclaration container) {
		if (this.elseBranch != null) {
			if (!this.elseBranch.collectAndPartialResolve(scope, container)) {
				return false;
			}
		}
		return (this.thenBranch.collectAndPartialResolve(scope, container) && this.condition.collectAndPartialResolve(scope));
	}
	

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.instruction.Instruction#resolve(fr.n7.stl.block.ast.scope.Scope)
	 */
	@Override
	public boolean completeResolve(HierarchicalScope<Declaration> scope) {
		if (this.elseBranch != null) {
			if (!this.elseBranch.completeResolve(scope)) {
				return false;
			}
		}
		return (this.thenBranch.completeResolve(scope) && this.condition.completeResolve(scope));
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Instruction#checkType()
	 */
	@Override
	public boolean checkType() {
		if (this.elseBranch != null) {
			if (!this.elseBranch.checkType()) {
				return false;
			}
		}
		if (!this.condition.getType().equalsTo(AtomicType.BooleanType)) {
			Logger.error("Erreur de Type: une condition doit être une variable booléenne.");
			return false;
		}
		return(thenBranch.checkType());
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Instruction#allocateMemory(fr.n7.stl.tam.ast.Register, int)
	 */
	@Override
	public int allocateMemory(Register register, int offset) {
		if (this.elseBranch == null) {
			this.thenBranch.allocateMemory(register, offset);
			return offset;
		}
		this.elseBranch.allocateMemory(register, offset);
		this.thenBranch.allocateMemory(register, offset);
		return offset;

	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Instruction#getCode(fr.n7.stl.tam.ast.TAMFactory)
	 */
	@Override
	public Fragment getCode(TAMFactory factory) {
		String lNum = String.valueOf(factory.createLabelNumber());
		Fragment fragCond = factory.createFragment();

		fragCond.append(this.condition.getCode(factory));
		fragCond.add(factory.createJumpIf("if" + lNum, 1));

		if (!(this.elseBranch == null)) {
			fragCond.append(this.elseBranch.getCode(factory));
		}
		fragCond.add(factory.createJump("fi" + lNum));

		Fragment fragIf = factory.createFragment();
		// intruction inutile, just pour le cas ou le fragment de code de thenBranch est vide.
		fragIf.add(factory.createPush(0));
		fragIf.append(this.thenBranch.getCode(factory));
		fragIf.addPrefix("if" + lNum);
		fragIf.addSuffix("fi" + lNum);

		fragCond.append(fragIf);

		return fragCond;
	}

}
