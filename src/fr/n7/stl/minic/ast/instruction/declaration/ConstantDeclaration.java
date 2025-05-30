/**
 *
 */
package fr.n7.stl.minic.ast.instruction.declaration;

import fr.n7.stl.minic.ast.expression.Expression;
import fr.n7.stl.minic.ast.instruction.Instruction;
import fr.n7.stl.minic.ast.scope.Declaration;
import fr.n7.stl.minic.ast.scope.HierarchicalScope;
import fr.n7.stl.minic.ast.type.Type;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.Register;
import fr.n7.stl.tam.ast.TAMFactory;
import fr.n7.stl.util.Logger;

/**
 * Implementation of the Abstract Syntax Tree node for a constant declaration instruction.
 * @author Marc Pantel
 *
 */
public class ConstantDeclaration implements Instruction, Declaration {

	/**
	 * Name of the constant
	 */
	protected String name;

	/**
	 * AST node for the type of the constant
	 */
	protected Type type;

	/**
	 * AST node for the expression that computes the value of the constant
	 */
	protected Expression value;

	/**
	 * Builds an AST node for a constant declaration
	 * @param _name : Name of the constant
	 * @param _type : AST node for the type of the constant
	 * @param _value : AST node for the expression that computes the value of the constant
	 */
	public ConstantDeclaration(String _name, Type _type, Expression _value) {
		this.name = _name;
		this.type = _type;
		this.value = _value;
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Declaration#getName()
	 */
	@Override
	public String getName() {
		return this.name;
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Declaration#getType()
	 */
	@Override
	public Type getType() {
		return this.type;
	}

	/**
	 * Provide the value associated to a name in constant declaration.
	 * @return Value from the declaration.
	 */
	public Expression getValue() {
		return this.value;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "const " + this.type + " " + this.name + " = " + this.value + ";\n";
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.instruction.Instruction#collect(fr.n7.stl.block.ast.scope.Scope)
	 */
	@Override
	public boolean collectAndPartialResolve(HierarchicalScope<Declaration> scope) {
		boolean result = this.value.collectAndPartialResolve(scope);
		if (scope.accepts(this)) {
			scope.register(this);
			return result;
		} else {
			Logger.error("La constante :\"" + this.name + "\" est déjà déclarée.");
			return false;
		}
	}

	@Override
	public boolean collectAndPartialResolve(HierarchicalScope<Declaration> scope, FunctionDeclaration container) {
		boolean result = this.value.collectAndPartialResolve(scope);
		if (scope.accepts(this)) {
			scope.register(this);
			return result;
		} else {
			Logger.error("La constante :\"" + this.name + "\" a déjà été déclarée dans la fonction : " + container.getName());
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.instruction.Instruction#resolve(fr.n7.stl.block.ast.scope.Scope)
	 */
	@Override
	public boolean completeResolve(HierarchicalScope<Declaration> scope) {
		return this.type.completeResolve(scope) && this.value.completeResolve(scope);
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Instruction#checkType()
	 */
	@Override
	public boolean checkType() {
		Type t_value = this.value.getType();
		if(!(this.type.compatibleWith(t_value))) {
			Logger.error("le type déclaré de la constante : " + this.name + "(" + this.type.toString()
			+ ") et le type de la valeur attribuée (" + t_value.toString() + ") ne sont pas compatibles.");
			return false;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Instruction#allocateMemory(fr.n7.stl.tam.ast.Register, int)
	 */
	@Override
	public int allocateMemory(Register register, int offset) {
		return offset;
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Instruction#getCode(fr.n7.stl.tam.ast.TAMFactory)
	 */
	@Override
	public Fragment getCode(TAMFactory _factory) {
		Fragment fragConst = _factory.createFragment();
		return fragConst;
	}

}
