grammar Deependee;

statements : statement (NEWLINE statement)*;

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
    : ID '|' COMPARATOR value COMMENT?
    | function '|' COMPARATOR value COMMENT?
    ;

external_call: ID ':' STRING;

object
    :   '{' pair (',' pair)* '}'
    |   '{' '}' // empty object
    ;
pair:   ID ':' value ;

array
    :   '[' value (',' value)* ']'
    |   '[' ']' // empty array
    ;

function : ID '(' value*(','value)* ')';

value
    :   STRING
    |   number
    |   ID
    |   function
    |   object
    |   array
    |   OPERATOR value // unary expression
    |   value OPERATOR value // binary expression
    |   value '?' value ':' value // ternary expression
    |   value COMPARATOR value
    |   'true'
    |   'false'
    ;

ID
    : ([a-z]+|[A-Z]+)+([a-z]+|[A-Z]|[0-9]|[\-_]+)*('.'ID)*
    ;

OPERATOR
    : '+' // addition, concatenation, boolean or
    | '*' // multiplication, boolean and
    | '/' // division, string split
    | '-' // substraction, remove element
    | '!' // not, inverse
    | '%' // modulo
    | '^' // power
    | '??' // query, index, find, search
    ;


STRING :  '"' (ESC | ~["\\])* '"' ;
COMMENT
    : '//' [.]*
    | '/*' [.]* '*/'
    ;

fragment ESC :   '\\' (["\\/bfnrt] | UNICODE) ;
fragment UNICODE : 'u' HEX HEX HEX HEX ;
fragment HEX : [0-9a-fA-F] ;

COMPARATOR : '<' | '<=' | '=' | '=>' | '>';

number : NUMBER;
NUMBER
    :   '-'? INT '.' [0-9]+ EXP? // 1.35, 1.35E-9, 0.3, -4.5
    |   '-'? INT EXP             // 1e10 -3e4
    |   '-'? INT                 // -3, 45
    ;
fragment INT :   '0' | [1-9] [0-9]* ; // no leading zeros
fragment EXP :   [Ee] [+\-]? INT ; // \- since - means "range" inside [...]

NEWLINE: [\r][\n] | [\n];

WS : [ \t\r]+ -> skip ;