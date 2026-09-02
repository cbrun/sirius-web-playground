package fr.obeo.dsl.guesstimate.formula;

import static org.petitparser.parser.primitive.CharacterParser.digit;
import static org.petitparser.parser.primitive.CharacterParser.letter;
import static org.petitparser.parser.primitive.CharacterParser.of;
import static org.petitparser.parser.primitive.CharacterParser.word;

import org.petitparser.parser.Parser;
import org.petitparser.parser.combinators.ChoiceParser;
import org.petitparser.tools.ExpressionBuilder;

public class ArithParser {

    public Parser createParser() {

        ExpressionBuilder builder = new ExpressionBuilder();
        Parser id = letter().seq(word().star()).flatten();

        ChoiceParser digOrVar = digit().or(id);
        builder.group().primitive(digOrVar.plus().seq(of('.').seq(digOrVar.plus()).optional()).flatten().trim()).wrapper(of('(').trim(), of(')').trim());
        // negation is a prefix operator
        builder.group().prefix(of('-').trim());

        // power is right-associative
        builder.group().right(of('^').trim());

        // multiplication and addition are left-associative
        builder.group().left(of('*').trim()).left(of('/').trim());
        builder.group().left(of('+').trim()).left(of('-').trim());
        Parser parser = builder.build().end();
        return parser;

    }
}
