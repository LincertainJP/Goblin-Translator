
package fr.n7.stl.minic;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import fr.n7.stl.minic.parser.MiniCLexer;
import fr.n7.stl.minic.parser.MiniCParser;

class Driver {

	public static void main(String[] args) throws Exception {
		String[] files;
		if (args.length == 0) {
			System.out.println("Using default file : input.txt");
			files = new String[1];
			files[0] = "input.txt";
		} else {
			files = args;
		}
		for (String name : files) {
			String fname = name.split("\\.")[0];
			try {
				CharStream input = CharStreams.fromFileName(name);
				ParsingErrorListener errorListener = new ParsingErrorListener();
				MiniCLexer lexer = new MiniCLexer(input);
				lexer.addErrorListener(errorListener);
				CommonTokenStream tokens = new CommonTokenStream(lexer);
				MiniCParser parser = new MiniCParser(tokens);
				parser.addErrorListener(errorListener);
				ParseTree tree = parser.programme();
				ParseTreeWalker walker = new ParseTreeWalker();
				ASTBuilder astBuilder = new ASTBuilder(fname + ".tam");
				System.out.println("Using input : " + fname + ".txt");
				System.out.println("Using output : " + fname + ".tam");
				walker.walk(astBuilder, tree);
				if (errorListener.hasError()) {
					System.err.println("Errors in parsing phase.");
				} else {
					astBuilder.startCompilation();
				}
			} catch (FileNotFoundException e) {
				System.err.println("File : " + name + " not found");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}