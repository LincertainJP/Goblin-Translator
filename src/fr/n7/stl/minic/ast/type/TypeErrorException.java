package fr.n7.stl.minic.ast.type;

public class TypeErrorException extends RuntimeException {

	private static final long serialVersionUID = -6867019579737948135L;

	/**
	 * Runtime exception used to indicate when a semantics element must be implemented. 
	 * @param message Usage specific message that relates to the class and method.
	 */
	public TypeErrorException(String message) {
		super(message);
	}

}
