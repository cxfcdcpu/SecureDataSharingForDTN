grammar Policy;

policy        : expr EOF;
atom          : (node | LPAR expr RPAR);
term          : atom (Operator term)*;
expr          : term (Operator term)*;
leafNode      : '!'? (WORD|NUM);
leafCondition : WORD BinOperator NUM;
node          : leafNode | leafCondition;

fragment OperatorOR    : ('OR' | 'or');
fragment OperatorAND   : ('AND' | 'and');
Operator      : (OperatorOR | OperatorAND);
WHITESPACE    : (' ' | '\t' | '\r' | '\n') -> skip;
LPAR          : '(' ;
RPAR          : ')' ;
BinOperator   : '<=' | '>=' | '==' | '<>';
NUM           : [0-9]+;
WORD          : [A-Za-z0-9_]+;

