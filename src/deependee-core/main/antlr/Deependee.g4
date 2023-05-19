grammar Deependee;

statements : statement (NEWLINE+ statement)* EOF?;

statement
    : dependency
    | constraint
    | COMMENT
    ;

dependency
    : ID '<-' value COMMENT?
    | ID '<-' external_call COMMENT?
    | function '<-' value COMMENT?
    | function '<-' external_call COMMENT?
    ;

constraint
    : ID '|' COMPARATOR value (rationale)? COMMENT?
    | function '|' COMPARATOR value (rationale)? COMMENT?
    ;

rationale : '|' STRING;

external_call: ID ':' STRING;

object
    :   '{' NEWLINE* pair ( NEWLINE* ',' NEWLINE* pair)* NEWLINE* '}'
    |   '{' NEWLINE* '}' // empty object
    ;
pair:   (STRING | ID | function) ':' value ;

array
    :   '[' NEWLINE* value ( NEWLINE* ',' NEWLINE* value)* NEWLINE* ']'
    |   '[' NEWLINE* ']' // empty array
    |   '[' NEWLINE* range NEWLINE* ']'
    ;

function : ID '(' value*(','value)* ')';

value
    :   STRING
    |   number
    |   ID
    |   function
    |   object
    |   array
    |   UNARY_OPERATOR value // unary expression
    |   value OPERATOR value // binary expression
    |   value '?' value ':' value // ternary expression
    |   value COMPARATOR value
    |   (STRING | ID | function | object | array) (ACCESSOR (NONZERO | STRING | ID | function))+
    |   'true'
    |   'false'
    |   '(' value ')'
    ;

range: value '..' value;

ID : ([a-z]+|[A-Z]+)+([a-z]|[A-Z]|[0-9]|[\-_])*;

OPERATOR
    : '+'  // addition, concatenation, boolean or
    | '*'  // multiplication, boolean and
    | '/'  // division, string split
    | '-'  // substraction, remove element
    | '%'  // modulo
    | '^'  // power
    | '??' // query, index, find, search
    ;

UNARY_OPERATOR
    : '!' // not, inverse
;

ACCESSOR
    : '.'  // accessor, call function on object, find at index, find at key
;

STRING :  '"' (ESC | ~["\\/])* '"' ;
COMMENT
    : '//' [.]*
    | '/*' [.]* '*/'
    ;

fragment ESC :   '\\' (["\\bfnrt] | UNICODE) ;
fragment UNICODE : 'u' HEX HEX HEX HEX HEX? ;
fragment HEX : [0-9a-fA-F] ;

COMPARATOR : '<' | '<=' | '=' | '=>' | '>';

number : NUMBER;
NUMBER
    :   '-'? INT '.' [0-9]+ EXP? // 1.35, 1.35E-9, 0.3, -4.5
    |   '-'? INT EXP             // 1e-10 -3e+4
    |   '-'? INT                 // -3, 45
    ;
NONZERO : [1-9];
INT :   '0' | NONZERO INT* ; // no leading zeros
fragment EXP :   ([Ee][+\-]) INT ;

NEWLINE: [\r][\n] | [\n];

WS : [ \t]+ -> skip ;