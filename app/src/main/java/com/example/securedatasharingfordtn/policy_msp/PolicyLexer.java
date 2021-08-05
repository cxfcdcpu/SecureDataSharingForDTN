package com.example.securedatasharingfordtn.policy_msp;

// Generated from Policy.g4 by ANTLR 4.9.2
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class PolicyLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.9.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, OperatorOR=2, OperatorAND=3, WHITESPACE=4, LPAR=5, RPAR=6, BinOperator=7, 
		NUM=8, WORD=9;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"T__0", "OperatorOR", "OperatorAND", "WHITESPACE", "LPAR", "RPAR", "BinOperator", 
			"NUM", "WORD"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'!'", null, null, null, "'('", "')'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, "OperatorOR", "OperatorAND", "WHITESPACE", "LPAR", "RPAR", 
			"BinOperator", "NUM", "WORD"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}


	public PolicyLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "Policy.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\13A\b\1\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\3\2\3\2"+
		"\3\3\3\3\3\3\3\3\5\3\34\n\3\3\4\3\4\3\4\3\4\3\4\3\4\5\4$\n\4\3\5\3\5\3"+
		"\5\3\5\3\6\3\6\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\5\b\66\n\b\3\t"+
		"\6\t9\n\t\r\t\16\t:\3\n\6\n>\n\n\r\n\16\n?\2\2\13\3\3\5\4\7\5\t\6\13\7"+
		"\r\b\17\t\21\n\23\13\3\2\5\5\2\13\f\17\17\"\"\3\2\62;\6\2\62;C\\aac|\2"+
		"G\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2"+
		"\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\3\25\3\2\2\2\5\33\3\2\2\2"+
		"\7#\3\2\2\2\t%\3\2\2\2\13)\3\2\2\2\r+\3\2\2\2\17\65\3\2\2\2\218\3\2\2"+
		"\2\23=\3\2\2\2\25\26\7#\2\2\26\4\3\2\2\2\27\30\7Q\2\2\30\34\7T\2\2\31"+
		"\32\7q\2\2\32\34\7t\2\2\33\27\3\2\2\2\33\31\3\2\2\2\34\6\3\2\2\2\35\36"+
		"\7C\2\2\36\37\7P\2\2\37$\7F\2\2 !\7c\2\2!\"\7p\2\2\"$\7f\2\2#\35\3\2\2"+
		"\2# \3\2\2\2$\b\3\2\2\2%&\t\2\2\2&\'\3\2\2\2\'(\b\5\2\2(\n\3\2\2\2)*\7"+
		"*\2\2*\f\3\2\2\2+,\7+\2\2,\16\3\2\2\2-.\7>\2\2.\66\7?\2\2/\60\7@\2\2\60"+
		"\66\7?\2\2\61\62\7?\2\2\62\66\7?\2\2\63\64\7>\2\2\64\66\7@\2\2\65-\3\2"+
		"\2\2\65/\3\2\2\2\65\61\3\2\2\2\65\63\3\2\2\2\66\20\3\2\2\2\679\t\3\2\2"+
		"8\67\3\2\2\29:\3\2\2\2:8\3\2\2\2:;\3\2\2\2;\22\3\2\2\2<>\t\4\2\2=<\3\2"+
		"\2\2>?\3\2\2\2?=\3\2\2\2?@\3\2\2\2@\24\3\2\2\2\b\2\33#\65:?\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}