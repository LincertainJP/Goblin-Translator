/**
 *
 */
package fr.n7.stl.minic.ast.instruction;

import java.security.InvalidParameterException;

import fr.n7.stl.minic.ast.SemanticsUndefinedException;
import fr.n7.stl.minic.ast.expression.Expression;
import fr.n7.stl.minic.ast.instruction.declaration.FunctionDeclaration;
import fr.n7.stl.minic.ast.scope.Declaration;
import fr.n7.stl.minic.ast.scope.HierarchicalScope;
import fr.n7.stl.minic.ast.type.Type;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.Register;
import fr.n7.stl.tam.ast.TAMFactory;

/**
 * Implementation of the Abstract Syntax Tree node for a return instruction.
 * @author Marc Pantel
 *
 */
public class Return implements Instruction {

	protected Expression value;

	protected FunctionDeclaration function;

	public Return(Expression _value) {
		this.value = _value;
		this.function = null;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return ((this.function != null)?("// Return in function : " + this.function.getName() + "\n"):"") + "return " + this.value + ";\n";
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.instruction.Instruction#collect(fr.n7.stl.block.ast.scope.Scope)
	 */
	@Override
	public boolean collectAndPartialResolve(HierarchicalScope<Declaration> scope) {
		return this.value.collectAndPartialResolve(scope);
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.instruction.Instruction#resolve(fr.n7.stl.block.ast.scope.Scope)
	 */
	@Override
	public boolean completeResolve(HierarchicalScope<Declaration> scope) {
		return this.value.completeResolve(scope);
	}

	@Override
	public boolean collectAndPartialResolve(HierarchicalScope<Declaration> _scope, FunctionDeclaration _container) {
		if (this.function == null) {
			this.function = _container;
		} else {
			throw new InvalidParameterException("Trying to set a function declaration to a return instruction when one has already been set.");
		}
		return this.collectAndPartialResolve(_scope);
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Instruction#checkType()
	 */
	@Override
	public boolean checkType() {
		Type typeReturn = this.value.getType();
		return this.function.getType().compatibleWith(typeReturn);
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Instruction#allocateMemory(fr.n7.stl.tam.ast.Register, int)
	 */
	@Override
	public int allocateMemory(Register _register, int _offset) {
		return 0;
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Instruction#getCode(fr.n7.stl.tam.ast.TAMFactory)
	 */
	@Override
	public Fragment getCode(TAMFactory factory) {
		Fragment fragFonRet = factory.createFragment();
		fragFonRet.append(value.getCode(factory));
		fragFonRet.add(factory.createJump(this.function.getName() +"retour"));
		return fragFonRet;
	}

}
