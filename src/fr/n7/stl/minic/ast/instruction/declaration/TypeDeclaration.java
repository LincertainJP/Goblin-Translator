package fr.n7.stl.minic.ast.instruction.declaration;

import fr.n7.stl.minic.ast.SemanticsUndefinedException;
import fr.n7.stl.minic.ast.instruction.Instruction;
import fr.n7.stl.minic.ast.scope.Declaration;
import fr.n7.stl.minic.ast.scope.HierarchicalScope;
import fr.n7.stl.minic.ast.type.EnumerationType;
import fr.n7.stl.minic.ast.type.Type;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.Register;
import fr.n7.stl.tam.ast.TAMFactory;
import fr.n7.stl.util.Logger;

/**
 * Implementation of the Abstract Syntax Tree node for a type declaration.
 * @author Marc Pantel
 *
 */
public class TypeDeclaration implements Declaration, Instruction {

	/**
	 * Name of the declared type
	 */
	private String name;

	/**
	 * AST node for the type associated to the name
	 */
	private Type type;

	/**
	 * Builds an AST node for a type declaration
	 * @param _name : Name of the declared type
	 * @param _type : AST node for the type associated to the name
	 */
	public TypeDeclaration(String _name, Type _type) {
		this.name = _name;
		this.type = _type;
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Declaration#getName()
	 */
	@Override
	public String getName() {
		return this.name;
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.instruction.Instruction#collect(fr.n7.stl.block.ast.scope.Scope)
	 */
	@Override
	public boolean collectAndPartialResolve(HierarchicalScope<Declaration> scope) {
		// throw new SemanticsUndefinedException( "Semantics collectAndPartialResolve is undefined in ConstantDeclaration.");
		if (!(scope.accepts(this))) {
			Logger.error("Déclaration de type impossible : le nom " + this.getName() + " est déjà déclaré !");
			return false;
		}
		scope.register(this);
		return true;
	}

	@Override
	public boolean collectAndPartialResolve(HierarchicalScope<Declaration> _scope, FunctionDeclaration _container) {
		throw new SemanticsUndefinedException( "Semantics collectAndPartialResolve is undefined in ConstantDeclaration.");

	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.instruction.Instruction#resolve(fr.n7.stl.block.ast.scope.Scope)
	 */
	@Override
	public boolean completeResolve(HierarchicalScope<Declaration> scope) {
		return this.type.completeResolve(scope);
	}

	/**
	 * Provide the type associated to a name in a type declaration.
	 * @return Type from the declaration.
	 */
	@Override
	public Type getType() {
		return this.type;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "typedef " + this.type + " " + this.name + ";\n";
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Instruction#checkType()
	 */
	@Override
	public boolean checkType() {
		return true;
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
	public Fragment getCode(TAMFactory _factory) {
		return _factory.createFragment();
	}

}
