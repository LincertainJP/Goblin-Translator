/**
 *
 */
package fr.n7.stl.minic.ast.instruction.declaration;

import java.util.Iterator;
import java.util.List;

import fr.n7.stl.minic.ast.Block;
import fr.n7.stl.minic.ast.SemanticsUndefinedException;
import fr.n7.stl.minic.ast.instruction.Instruction;
import fr.n7.stl.minic.ast.scope.Declaration;
import fr.n7.stl.minic.ast.scope.HierarchicalScope;
import fr.n7.stl.minic.ast.scope.SymbolTable;
import fr.n7.stl.minic.ast.type.Type;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.Register;
import fr.n7.stl.tam.ast.TAMFactory;
import fr.n7.stl.util.Logger;

/**
 * Abstract Syntax Tree node for a function declaration.
 * @author Marc Pantel
 */
public class FunctionDeclaration implements Instruction, Declaration {

	/**
	 * Name of the function
	 */
	protected String name;
	
	protected SymbolTable localTDS;


	/**
	 * AST node for the returned type of the function
	 */
	protected Type type;

	/**
	 * List of AST nodes for the formal parameters of the function
	 */
	protected List<ParameterDeclaration> parameters;

	/**
	 * @return the parameters
	 */
	public List<ParameterDeclaration> getParameters() {
		return parameters;
	}

	/**
	 * AST node for the body of the function
	 */
	protected Block body;

	/**
	 * Builds an AST node for a function declaration
	 * @param _name : Name of the function
	 * @param _type : AST node for the returned type of the function
	 * @param _parameters : List of AST nodes for the formal parameters of the function
	 * @param _body : AST node for the body of the function
	 */
	public FunctionDeclaration(String _name, Type _type, List<ParameterDeclaration> _parameters, Block _body) {
		this.name = _name;
		this.type = _type;
		this.parameters = _parameters;
		this.body = _body;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String _result = this.type + " " + this.name + "( ";
		Iterator<ParameterDeclaration> _iter = this.parameters.iterator();
		if (_iter.hasNext()) {
			_result += _iter.next();
			while (_iter.hasNext()) {
				_result += " ," + _iter.next();
			}
		}
		return _result + " )" + this.body;
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

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.instruction.Instruction#collect(fr.n7.stl.block.ast.scope.Scope)
	 */
	@Override
	public boolean collectAndPartialResolve(HierarchicalScope<Declaration> scope) {
		if (!scope.accepts(this)) {
			Logger.error("Impossible de déclaré la fonction de nom : " + this.name + ", le nom à déjà été déclaré.");
			return false;
		}
		scope.register(this);
		this.localTDS =  new SymbolTable(scope);
		for (ParameterDeclaration paramDecl : this.getParameters()) {
			if (!localTDS.accepts(paramDecl)) {
				Logger.error("Il y a 2 paramètres déclaré avec le nom " + paramDecl.getName() 
					+ " dans la déclaration de la fontion " + this.getName());
				return false;
			}
			localTDS.register(paramDecl);
		}
		boolean okBody = body.collectAndPartialResolve(scope, this);
		boolean okType = type.completeResolve(scope);
		return okBody && okType;
	}

	@Override
	public boolean collectAndPartialResolve(HierarchicalScope<Declaration> scope, FunctionDeclaration container) {
		if (!scope.accepts(this)) {
			Logger.error("Impossible de déclaré la fonction de nom : " + this.name + ", le nom à déjà été déclaré.");
			return false;
		}
		scope.register(this);
		this.localTDS =  new SymbolTable(scope);
		for (ParameterDeclaration paramDecl : this.getParameters()) {
			if (!localTDS.accepts(paramDecl)) {
				Logger.error("Il y a 2 paramètres déclaré avec le nom " + paramDecl.getName() 
					+ " dans la déclaration de la fontion " + this.getName());
				return false;
			}
			localTDS.register(paramDecl);
		}
		boolean okBody = body.collectAndPartialResolve(scope, this);
		boolean okType = type.completeResolve(scope);
		return okBody && okType;

	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.instruction.Instruction#resolve(fr.n7.stl.block.ast.scope.Scope)
	 */
	@Override
	public boolean completeResolve(HierarchicalScope<Declaration> scope) {
		boolean okBody = body.completeResolve(this.localTDS);
		boolean okType = type.completeResolve(scope);
		return okBody && okType;
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.instruction.Instruction#checkType()
	 */
	@Override
	public boolean checkType() {
		return this.body.checkType();
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.instruction.Instruction#allocateMemory(fr.n7.stl.tam.ast.Register, int)
	 */
	@Override
	public int allocateMemory(Register register, int offset) {
		int off = 0;
		for (ParameterDeclaration paramDecl : this.getParameters()) {
			off += paramDecl.getType().length();
		}
		body.allocateMemory(register.LB, off);
		for (ParameterDeclaration paramDecl : this.getParameters()) {
			paramDecl.offset = off;
			off -= paramDecl.getType().length();
		}
		return 0;
		}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.instruction.Instruction#getCode(fr.n7.stl.tam.ast.TAMFactory)
	 */
	@Override
	public Fragment getCode(TAMFactory factory) {
		Fragment fragFonDecl = factory.createFragment();
		fragFonDecl.add(factory.createJump(this.getName()+ "end"));
		fragFonDecl.addSuffix(this.getName()+ "start");
		fragFonDecl.append(this.body.getCode(factory));
		Fragment fragFonEnd = factory.createFragment();
		int off = 0;
		for (ParameterDeclaration paramDecl : this.getParameters()) {
			off += paramDecl.getType().length();
		}
		fragFonEnd.add(factory.createReturn(type.length(), off));
		fragFonEnd.addPrefix(name + "retour");
		fragFonEnd.addSuffix(this.getName()+ "end");
		fragFonDecl.append(fragFonEnd);
		return fragFonDecl;
	}

}
