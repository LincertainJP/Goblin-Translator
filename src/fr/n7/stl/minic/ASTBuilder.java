package fr.n7.stl.minic;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

import org.antlr.v4.runtime.Token;

import fr.n7.stl.minic.ast.Block;
import fr.n7.stl.minic.ast.expression.Couple;
import fr.n7.stl.minic.ast.expression.First;
import fr.n7.stl.minic.ast.expression.FunctionCall;
import fr.n7.stl.minic.ast.expression.Second;
import fr.n7.stl.minic.ast.expression.accessible.AccessibleConditional;
import fr.n7.stl.minic.ast.expression.accessible.AccessibleConversion;
import fr.n7.stl.minic.ast.expression.accessible.AccessibleExpression;
import fr.n7.stl.minic.ast.expression.accessible.AddressAccess;
import fr.n7.stl.minic.ast.expression.accessible.ArrayAccess;
import fr.n7.stl.minic.ast.expression.accessible.BinaryExpression;
import fr.n7.stl.minic.ast.expression.accessible.BinaryOperator;
import fr.n7.stl.minic.ast.expression.accessible.FieldAccess;
import fr.n7.stl.minic.ast.expression.accessible.IdentifierAccess;
import fr.n7.stl.minic.ast.expression.accessible.PointerAccess;
import fr.n7.stl.minic.ast.expression.accessible.Sequence;
import fr.n7.stl.minic.ast.expression.accessible.UnaryExpression;
import fr.n7.stl.minic.ast.expression.accessible.UnaryOperator;
import fr.n7.stl.minic.ast.expression.allocation.ArrayAllocation;
import fr.n7.stl.minic.ast.expression.allocation.PointerAllocation;
import fr.n7.stl.minic.ast.expression.assignable.ArrayAssignment;
import fr.n7.stl.minic.ast.expression.assignable.AssignableConversion;
import fr.n7.stl.minic.ast.expression.assignable.FieldAssignment;
import fr.n7.stl.minic.ast.expression.assignable.PointerAssignment;
import fr.n7.stl.minic.ast.expression.assignable.VariableAssignment;
import fr.n7.stl.minic.ast.expression.value.BooleanValue;
import fr.n7.stl.minic.ast.expression.value.CharacterValue;
import fr.n7.stl.minic.ast.expression.value.FloatingValue;
import fr.n7.stl.minic.ast.expression.value.IntegerValue;
import fr.n7.stl.minic.ast.expression.value.NullValue;
import fr.n7.stl.minic.ast.expression.value.StringValue;
import fr.n7.stl.minic.ast.instruction.Assignment;
import fr.n7.stl.minic.ast.instruction.Conditional;
import fr.n7.stl.minic.ast.instruction.Instruction;
import fr.n7.stl.minic.ast.instruction.Iteration;
import fr.n7.stl.minic.ast.instruction.Printer;
import fr.n7.stl.minic.ast.instruction.Return;
import fr.n7.stl.minic.ast.instruction.declaration.ConstantDeclaration;
import fr.n7.stl.minic.ast.instruction.declaration.FunctionDeclaration;
import fr.n7.stl.minic.ast.instruction.declaration.ParameterDeclaration;
import fr.n7.stl.minic.ast.instruction.declaration.TypeDeclaration;
import fr.n7.stl.minic.ast.instruction.declaration.VariableDeclaration;
import fr.n7.stl.minic.ast.scope.SymbolTable;
import fr.n7.stl.minic.ast.type.AtomicType;
import fr.n7.stl.minic.ast.type.CoupleType;
import fr.n7.stl.minic.ast.type.EnumerationType;
import fr.n7.stl.minic.ast.type.NamedType;
import fr.n7.stl.minic.ast.type.PartialArrayType;
import fr.n7.stl.minic.ast.type.PartialPointerType;
import fr.n7.stl.minic.ast.type.PartialType;
import fr.n7.stl.minic.ast.type.RecordType;
import fr.n7.stl.minic.ast.type.Type;
import fr.n7.stl.minic.ast.type.declaration.FieldDeclaration;
import fr.n7.stl.minic.ast.type.declaration.LabelDeclaration;
import fr.n7.stl.minic.parser.MiniCParser;
import fr.n7.stl.minic.parser.MiniCParser.AffectableArrayContext;
import fr.n7.stl.minic.parser.MiniCParser.AffectableConversionContext;
import fr.n7.stl.minic.parser.MiniCParser.AffectableFieldContext;
import fr.n7.stl.minic.parser.MiniCParser.AffectableIdentifiantContext;
import fr.n7.stl.minic.parser.MiniCParser.AffectablePointerContext;
import fr.n7.stl.minic.parser.MiniCParser.ArgumentsContext;
import fr.n7.stl.minic.parser.MiniCParser.AtomiqueContext;
import fr.n7.stl.minic.parser.MiniCParser.BlocContext;
import fr.n7.stl.minic.parser.MiniCParser.ChampContext;
import fr.n7.stl.minic.parser.MiniCParser.EtiquettesContext;
import fr.n7.stl.minic.parser.MiniCParser.ExpresionFloatContext;
import fr.n7.stl.minic.parser.MiniCParser.ExpressionAccessContext;
import fr.n7.stl.minic.parser.MiniCParser.ExpressionAdditiveContext;
import fr.n7.stl.minic.parser.MiniCParser.ExpressionAddressContext;
import fr.n7.stl.minic.parser.MiniCParser.ExpressionAndContext;
import fr.n7.stl.minic.parser.MiniCParser.ExpressionArrayAcessContext;
import fr.n7.stl.minic.parser.MiniCParser.ExpressionArrayAllocationContext;
import fr.n7.stl.minic.parser.MiniCParser.ExpressionCharacterContext;
import fr.n7.stl.minic.parser.MiniCParser.ExpressionConditionalContext;
import fr.n7.stl.minic.parser.MiniCParser.ExpressionContext;
import fr.n7.stl.minic.parser.MiniCParser.ExpressionConversionContext;
import fr.n7.stl.minic.parser.MiniCParser.ExpressionCoupleContext;
import fr.n7.stl.minic.parser.MiniCParser.ExpressionEqualityContext;
import fr.n7.stl.minic.parser.MiniCParser.ExpressionFalseContext;
import fr.n7.stl.minic.parser.MiniCParser.ExpressionFieldContext;
import fr.n7.stl.minic.parser.MiniCParser.ExpressionFirstContext;
import fr.n7.stl.minic.parser.MiniCParser.ExpressionFunctionCallContext;
import fr.n7.stl.minic.parser.MiniCParser.ExpressionInequalityContext;
import fr.n7.stl.minic.parser.MiniCParser.ExpressionIntContext;
import fr.n7.stl.minic.parser.MiniCParser.ExpressionMultiplicativeContext;
import fr.n7.stl.minic.parser.MiniCParser.ExpressionNotContext;
import fr.n7.stl.minic.parser.MiniCParser.ExpressionNullContext;
import fr.n7.stl.minic.parser.MiniCParser.ExpressionOppositeContext;
import fr.n7.stl.minic.parser.MiniCParser.ExpressionOrContext;
import fr.n7.stl.minic.parser.MiniCParser.ExpressionParentheseContext;
import fr.n7.stl.minic.parser.MiniCParser.ExpressionPointerAccessContext;
import fr.n7.stl.minic.parser.MiniCParser.ExpressionPointerAllocationContext;
import fr.n7.stl.minic.parser.MiniCParser.ExpressionSecondContext;
import fr.n7.stl.minic.parser.MiniCParser.ExpressionSequenceContext;
import fr.n7.stl.minic.parser.MiniCParser.ExpressionStringContext;
import fr.n7.stl.minic.parser.MiniCParser.ExpressionTrueContext;
import fr.n7.stl.minic.parser.MiniCParser.ExpressionsContext;
import fr.n7.stl.minic.parser.MiniCParser.IdentifiantContext;
import fr.n7.stl.minic.parser.MiniCParser.InstructionAffectationContext;
import fr.n7.stl.minic.parser.MiniCParser.InstructionAffichageContext;
import fr.n7.stl.minic.parser.MiniCParser.InstructionDeclarationContext;
import fr.n7.stl.minic.parser.MiniCParser.InstructionFunctionDeclarationContext;
import fr.n7.stl.minic.parser.MiniCParser.InstructionReturnContext;
import fr.n7.stl.minic.parser.MiniCParser.InstructionSiSinonContext;
import fr.n7.stl.minic.parser.MiniCParser.InstructionTypeDeclarationContext;
import fr.n7.stl.minic.parser.MiniCParser.ParametresContext;
import fr.n7.stl.minic.parser.MiniCParser.ProgrammeContext;
import fr.n7.stl.minic.parser.MiniCParser.TypeAtomicContext;
import fr.n7.stl.minic.parser.MiniCParser.TypeCoupleContext;
import fr.n7.stl.minic.parser.MiniCParser.TypeEnumContext;
import fr.n7.stl.minic.parser.MiniCParser.TypeNamedContext;
import fr.n7.stl.minic.parser.MiniCParser.TypeRecordContext;
import fr.n7.stl.minic.parser.MiniCParserBaseListener;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.Register;
import fr.n7.stl.tam.ast.TAMFactory;
import fr.n7.stl.tam.ast.impl.TAMFactoryImpl;
import fr.n7.stl.util.Pair;

public class ASTBuilder extends MiniCParserBaseListener {

	private String output_path;
	private Block mainBlock;
	private String name;

	public ASTBuilder(String _output) {
		this.output_path = _output;
	}

	/**
	 * Start the compile phase : collect, resolve, memory allocation and
	 * code generation.
	 */
	public void startCompilation() {
		System.out.println(this.name + " " + this.mainBlock);
		SymbolTable tds = new SymbolTable();
		if (this.mainBlock.collectAndPartialResolve(tds)) {//if (this.mainBlock.collect(tds)) {
			System.out.println("collect succeeded");
			if (this.mainBlock.completeResolve(tds)) {//if (this.mainBlock.resolve(tds)) {
				System.out.println("Resolve succeeded.");

				if (this.mainBlock.checkType()) {
					System.out.println("Type verification succeeded.");

					System.out.println("Code generation ...");
					this.mainBlock.allocateMemory(Register.SB, 0);
					try {
						PrintWriter writer = new PrintWriter(output_path);
						TAMFactory factory = new TAMFactoryImpl();
						Fragment f = this.mainBlock.getCode(factory);
						f.add(factory.createHalt());
						//                        f.append(this.mainBlock.getFunctions(factory));
						writer.println(f);
						writer.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					System.out.println("Code generation finished");
				} else {
					System.out.println("Type verification failed.");
				}
			} else {
				System.out.println("Resolve failed." + tds);
			}
		} else {
			System.out.println("Collect failed : " + tds);
		}
	}

	@Override
	public void exitProgramme(ProgrammeContext ctx) {
		this.mainBlock = ctx.main.b;
		this.name = ctx.name.getText();
	}

	@Override
	public void exitBloc(BlocContext ctx) {
		List<Instruction> instructionList = new LinkedList<>();
		for (MiniCParser.InstructionContext iCtx : ctx.instructions) {
			if (iCtx.i != null) {
				instructionList.add(iCtx.i);
			}
		}
		ctx.b = new Block(instructionList);
	}

	@Override
	public void exitParametres(ParametresContext ctx) {
		List<ParameterDeclaration> listParameters = new LinkedList<>();
		if (ctx.ident1 != null && ctx.type1 != null) {
			String name1 = ctx.ident1.id.getLeft();
			Type type1 = completeType(ctx.ident1.id.getRight(), ctx.type1.t);
			listParameters.add(new ParameterDeclaration(name1, type1));
			for (int i = 0; i < ctx.suiteIdent.size(); i++) {
				String name = ctx.suiteIdent.get(i).id.getLeft();
				Type type = completeType(ctx.suiteIdent.get(i).id.getRight(), ctx.suiteType.get(i).t);
				listParameters.add(new ParameterDeclaration(name, type));
			}
		}
		ctx.l = listParameters;
	}

	private Type completeType(PartialType pType, Type type)  {
		Type res;
		if (pType == null) {
			res = type;
		} else {
			res = pType.complete(type);
		}
		return res;
	}

	@Override
	public void exitInstructionDeclaration(InstructionDeclarationContext ctx) {
		String name = ctx.identifiant().id.getLeft();
		Type type = completeType(ctx.identifiant().id.getRight(), ctx.type().t);
		if (ctx.DefinitionConstante() != null) {
			ctx.i = new ConstantDeclaration(name, type, ctx.expression().e);
		} else {
			ctx.i = new VariableDeclaration(name, type, ctx.expression().e);
		}
	}

	@Override
	public void exitInstructionTypeDeclaration(InstructionTypeDeclarationContext ctx) {
		String name = ctx.identifiant().id.getLeft();
		Type type = completeType(ctx.identifiant().id.getRight(), ctx.type().t);
		ctx.i = new TypeDeclaration(name, type);
	}

	@Override
	public void exitInstructionFunctionDeclaration(InstructionFunctionDeclarationContext ctx) {
		String name = ctx.identifiant().id.getLeft();
		Type returnType = completeType(ctx.identifiant().id.getRight(), ctx.type().t);
		List<ParameterDeclaration> parameters = ctx.parametres().l;
		Block body = ctx.bloc().b;
		ctx.i = new FunctionDeclaration(name, returnType, parameters, body);
	}

	@Override
	public void exitInstructionAffectation(InstructionAffectationContext ctx) {
		ctx.i = new Assignment(ctx.affectable().a, ctx.valeur.e);
	}

	@Override
	public void exitInstructionAffichage(InstructionAffichageContext ctx) {
		ctx.i = new Printer(ctx.expression().e);
	}

	@Override
	public void exitInstructionSiSinon(InstructionSiSinonContext ctx) {
		ctx.i = new Conditional(ctx.expression().e, ctx.alors.b, ctx.sinon.b);
	}
	@Override
	public void exitInstructionSi(MiniCParser.InstructionSiContext ctx) {
		ctx.i = new Conditional(ctx.expression().e, ctx.alors.b);
	}

	@Override
	public void exitInstructionIteration(MiniCParser.InstructionIterationContext ctx) {
		ctx.i = new Iteration(ctx.expression().e, ctx.faire.b);
	}

	@Override
	public void exitInstructionReturn(InstructionReturnContext ctx) {
		ctx.i = new Return(ctx.expression().e);
	}

	@Override
	public void exitAtomique(AtomiqueContext ctx) {
		if (ctx.TypeEntier() != null) {
			ctx.t = AtomicType.IntegerType;
		} else if (ctx.TypeFlottant() != null) {
			ctx.t = AtomicType.FloatingType;
		} else if (ctx.TypeBooleen() != null) {
			ctx.t = AtomicType.BooleanType;
		} else if (ctx.TypeCaractere() != null) {
			ctx.t = AtomicType.CharacterType;
		} else if (ctx.TypeChaine() != null) {
			ctx.t = AtomicType.StringType;
		} else if (ctx.TypeVide() != null) {
			ctx.t = AtomicType.VoidType;
		}
	}

	@Override
	public void exitChamp(ChampContext ctx) {
		String name = ctx.identifiant().id.getLeft();
		Type type = completeType(ctx.identifiant().id.getRight(), ctx.type().t);
		ctx.f = new FieldDeclaration(name, type);
	}

	@Override
	public void exitEtiquettes(EtiquettesContext ctx) {
		List<LabelDeclaration> listLabels = new LinkedList<>();
		listLabels.add(new LabelDeclaration(ctx.premiere.getText()));
		for (Token lCtx : ctx.suite) {
			listLabels.add(new LabelDeclaration(lCtx.getText()));
		}
		ctx.l = listLabels;
	}

	@Override
	public void exitTypeAtomic(TypeAtomicContext ctx) {
		ctx.t = ctx.atomique().t;
	}

	@Override
	public void exitTypeNamed(TypeNamedContext ctx) {
		ctx.t = new NamedType(ctx.Identificateur().getText());
	}

	@Override
	public void exitTypeCouple(TypeCoupleContext ctx) {
		ctx.t = new CoupleType(ctx.gauche.t, ctx.droite.t);
	}

	@Override
	public void exitTypeRecord(TypeRecordContext ctx) {
		List<FieldDeclaration> listDeclarations = new LinkedList<>();
		for (ChampContext cCtx : ctx.champs) {
			if (cCtx.f != null) {
				listDeclarations.add(cCtx.f);
			}
		}
		ctx.t = new RecordType(ctx.Identificateur().getText(), listDeclarations);
	}

	@Override
	public void exitTypeEnum(TypeEnumContext ctx) {
		ctx.t = new EnumerationType(ctx.Identificateur().getText(), ctx.etiquettes().l);
	}

	@Override
	public void exitAffectableIdentifiant(AffectableIdentifiantContext ctx) {
		ctx.a = new VariableAssignment(ctx.ident.getText());
	}

	@Override
	public void exitAffectableConversion(AffectableConversionContext ctx) {
		if (ctx.atomique() != null) {
			ctx.a = new AssignableConversion(ctx.affectable().a, ctx.atomique().t);
		} else if (ctx.Identificateur() != null) {
			ctx.a = new AssignableConversion(ctx.affectable().a, ctx.Identificateur().getText());
		} else {
			throw new RuntimeException("Both atomic and Identificateur are null in rule affectableConversion");
		}
	}

	@Override
	public void exitAffectableField(AffectableFieldContext ctx) {
		ctx.a = new FieldAssignment(ctx.affectable().a, ctx.Identificateur().getText());
	}

	@Override
	public void exitAffectableArray(AffectableArrayContext ctx) {
		ctx.a = new ArrayAssignment(ctx.affectable().a, ctx.expression().e);
	}

	@Override
	public void exitAffectablePointer(AffectablePointerContext ctx) {
		ctx.a = new PointerAssignment(ctx.expression().e);
	}

	@Override
	public void exitExpressions(ExpressionsContext ctx) {
		List<AccessibleExpression> listExpressions = new LinkedList<>();
		listExpressions.add(ctx.premiere.e);
		for (ExpressionContext eCtx : ctx.suite) {
			if (eCtx.e != null) {
				listExpressions.add(eCtx.e);
			}
		}
		ctx.l = listExpressions;
	}

	@Override
	public void exitArguments(ArgumentsContext ctx) {
		if (ctx.expressions() != null) {
			ctx.l = ctx.expressions().l;
		} else {
			ctx.l = new LinkedList<>();
		}
	}

	@Override
	public void exitExpressionAccess(ExpressionAccessContext ctx) {
		ctx.e = new IdentifierAccess(ctx.Identificateur().getText());
	}

	@Override
	public void exitExpressionAdditive(ExpressionAdditiveContext ctx) {
		BinaryOperator op;
		switch (ctx.op.getText()) {
		case "+":
			op = BinaryOperator.Add;
			break;
		case "-":
			op = BinaryOperator.Substract;
			break;
		default:
			throw new RuntimeException("Unkonwn operator");
		}
		ctx.e = new BinaryExpression(ctx.gauche.e, op, ctx.droite.e);
	}

	@Override
	public void exitExpressionMultiplicative(ExpressionMultiplicativeContext ctx) {
		BinaryOperator op;
		switch (ctx.op.getText()) {
		case "*":
			op = BinaryOperator.Multiply;
			break;
		case "/":
			op = BinaryOperator.Divide;
			break;
		case "%":
			op = BinaryOperator.Modulo;
			break;
		default:
			throw new RuntimeException("Unkonwn operator");
		}
		ctx.e = new BinaryExpression(ctx.gauche.e, op, ctx.droite.e);
	}

	@Override
	public void exitExpressionAnd(ExpressionAndContext ctx) {
		ctx.e = new BinaryExpression(ctx.gauche.e, BinaryOperator.And, ctx.droite.e);
	}

	@Override
	public void exitExpressionOr(ExpressionOrContext ctx) {
		ctx.e = new BinaryExpression(ctx.gauche.e, BinaryOperator.Or, ctx.droite.e);
	}

	@Override
	public void exitExpressionConditional(ExpressionConditionalContext ctx) {
		ctx.e = new AccessibleConditional(ctx.condition.e, ctx.alors.e, ctx.sinon.e);
	}

	@Override
	public void exitExpressionField(ExpressionFieldContext ctx) {
		ctx.e = new FieldAccess(ctx.expression().e, ctx.Identificateur().getText());
	}

	@Override
	public void exitExpressionFirst(ExpressionFirstContext ctx) {
		ctx.e = new First(ctx.expression().e);
	}

	@Override
	public void exitExpressionSecond(ExpressionSecondContext ctx) {
		ctx.e = new Second(ctx.expression().e);
	}

	@Override
	public void exitExpressionSequence(ExpressionSequenceContext ctx) {
		ctx.e = new Sequence(ctx.expressions().l);
	}

	@Override
	public void exitExpressionCouple(ExpressionCoupleContext ctx) {
		ctx.e = new Couple(ctx.gauche.e, ctx.droite.e);
	}

	@Override
	public void exitExpressionNot(ExpressionNotContext ctx) {
		ctx.e = new UnaryExpression(UnaryOperator.Negate, ctx.expr.e);
	}

	@Override
	public void exitExpressionFalse(ExpressionFalseContext ctx) {
		ctx.e = BooleanValue.False;
	}

	@Override
	public void exitExpressionFunctionCall(ExpressionFunctionCallContext ctx) {
		ctx.e = new FunctionCall(ctx.Identificateur().getText(), ctx.arguments().l);
	}

	@Override
	public void exitExpressionTrue(ExpressionTrueContext ctx) {
		ctx.e = BooleanValue.True;
	}

	@Override
	public void exitExpressionInt(ExpressionIntContext ctx) {
		ctx.e = new IntegerValue(ctx.Entier().getText());
	}

	@Override
	public void exitExpresionFloat(ExpresionFloatContext ctx) {
		ctx.e = new FloatingValue(ctx.Flottant().getText());
	}

	@Override
	public void exitExpressionCharacter(ExpressionCharacterContext ctx) {
		ctx.e = new CharacterValue(ctx.Caractere().getText());
	}

	@Override
	public void exitExpressionString(ExpressionStringContext ctx) {
		ctx.e = new StringValue(ctx.Chaine().getText());
	}

	@Override
	public void exitExpressionNull(ExpressionNullContext ctx) {
		ctx.e = NullValue.Null;
	}

	@Override
	public void exitExpressionEquality(ExpressionEqualityContext ctx) {
		BinaryOperator op;
		switch (ctx.op.getText()) {
		case "==":
			op = BinaryOperator.Equals;
			break;
		case "!=":
			op = BinaryOperator.Different;
			break;
		default:
			throw new RuntimeException("Unkonwn operator");
		}
		ctx.e = new BinaryExpression(ctx.gauche.e, op, ctx.droite.e);
	}

	@Override
	public void exitExpressionInequality(ExpressionInequalityContext ctx) {
		BinaryOperator op;
		switch (ctx.op.getText()) {
		case "<":
			op = BinaryOperator.Lesser;
			break;
		case "<=":
			op = BinaryOperator.LesserOrEqual;
			break;
		case ">":
			op = BinaryOperator.Greater;
			break;
		case ">=":
			op = BinaryOperator.GreaterOrEqual;
			break;
		default:
			throw new RuntimeException("Unkonwn operator");
		}
		ctx.e = new BinaryExpression(ctx.gauche.e, op, ctx.droite.e);
	}

	@Override
	public void exitExpressionParenthese(ExpressionParentheseContext ctx) {
		ctx.e = ctx.expression().e;
	}

	@Override
	public void exitExpressionOpposite(ExpressionOppositeContext ctx) {
		ctx.e = new UnaryExpression(UnaryOperator.Opposite, ctx.expression().e);
	}

	@Override
	public void exitExpressionAddress(ExpressionAddressContext ctx) {
		ctx.e = new AddressAccess(ctx.affectable().a);
	}

	@Override
	public void exitExpressionConversion(ExpressionConversionContext ctx) {
		if (ctx.atomique() != null) {
			ctx.e = new AccessibleConversion(ctx.expression().e, ctx.atomique().t);
		} else if (ctx.Identificateur() != null) {
			ctx.e = new AccessibleConversion(ctx.expression().e, ctx.Identificateur().getText());
		} else {
			throw new RuntimeException("Both atomic and Identificateur are null in rule ExpressionConversion");
		}
	}

	@Override
	public void exitExpressionPointerAccess(ExpressionPointerAccessContext ctx) {
		ctx.e = new PointerAccess(ctx.expression().e);
	}

	@Override
	public void exitExpressionArrayAcess(ExpressionArrayAcessContext ctx) {
		ctx.e = new ArrayAccess(ctx.tableau.e, ctx.indice.e);
	}

	@Override
	public void exitExpressionArrayAllocation(ExpressionArrayAllocationContext ctx) {
		ctx.e = new ArrayAllocation(ctx.type().t, ctx.expression().e);
	}

	@Override
	public void exitExpressionPointerAllocation(ExpressionPointerAllocationContext ctx) {
		ctx.e = new PointerAllocation(ctx.type().t);
	}

	@Override
	public void exitIdentifiant(IdentifiantContext ctx) {
		if (ctx.Identificateur() != null) {
			ctx.id = new Pair<>(ctx.Identificateur().getText(), null);
		} else if (ctx.ParentheseOuvrante() != null) {
			ctx.id = ctx.identifiant().id;
		} else {
			Pair<String, PartialType> ident = ctx.identifiant().id;
			PartialType newPartialType = (ctx.Asterisque() != null) ? new PartialPointerType() : new PartialArrayType();
			if (ident.getRight() == null) {
				ident.setRight(newPartialType);
			} else {
				ident.getRight().enrich(newPartialType);
			}
			ctx.id = ident;
		}
	}
}
