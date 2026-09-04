/*******************************************************************************
 * Copyright (c) 2026 Obeo.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Obeo - initial API and implementation
 *******************************************************************************/
package fr.obeo.dsl.guesstimate.formula;

import static org.petitparser.parser.primitive.CharacterParser.digit;
import static org.petitparser.parser.primitive.CharacterParser.letter;
import static org.petitparser.parser.primitive.CharacterParser.of;
import static org.petitparser.parser.primitive.CharacterParser.word;

import org.petitparser.context.Result;
import org.petitparser.parser.Parser;
import org.petitparser.parser.combinators.ChoiceParser;
import org.petitparser.tools.ExpressionBuilder;

/**
 * Parses the arithmetic formula language.
 *
 * @since 0.0.7
 */
public class ArithParser {

    private final Parser identifier = letter().seq(word().star()).flatten();

    private final Parser identifierParser = this.identifier.end();

    private final Parser parser = this.createParser();

    /**
     * Parses a formula.
     *
     * @param formula the formula to parse
     * @return the immutable parse result
     * @since 0.0.7
     */
    public Result parse(String formula) {
        return this.parser.parse(formula);
    }

    /**
     * Tests whether a value is a valid variable identifier in the formula language.
     *
     * @param value the value to test
     * @return whether the value is accepted as an identifier
     * @since 0.0.7
     */
    public boolean isValidIdentifier(String value) {
        return value != null && this.identifierParser.parse(value).isSuccess();
    }

    private Parser createParser() {

        ExpressionBuilder builder = new ExpressionBuilder();
        ChoiceParser digOrVar = digit().or(this.identifier);
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
