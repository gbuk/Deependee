grammar Deependee;

expression
    : dependency
    | constraint
    | COMMENT
    ;
dependency
    : leftIdentifier=ID '<-' rightIdentifierValue=value rightIdentifierComment=COMMENT?
    | leftFunction=function '<-' rightFunctionValue=value rightFunctionComment=COMMENT?
    ;
constraint
    : leftIdentifier=ID '|' rightIdentifierComparator=COMPARATOR rightIdentifierValue=value rightIdentifierComment=COMMENT?
    | leftFunction=function '|' leftFunctionComparator=COMPARATOR leftFunctionValue=value leftFunctionComment=COMMENT?
    ;


object
    :   '{' pair (',' pair)* '}'
    |   '{' '}' // empty object
    ;
pair:   ID ':' value ;

array
    :   '[' first=value (',' next=value)* ']'
    |   '[' ']' // empty array
    ;

function : ID '(' value*(','value)* ')';

value
    :   STRING
    |   NUMBER
    |   ID
    |   function
    |   object
    |   array
    |   value operation
    |   'true'
    |   'false'
    ;

operation: OPERATOR value;

ID
    : ([a-z]+|[A-Z]+)+([a-z]+|[A-Z]|[0-9]|[\-_]+)*('.'ID)*
    ;

OPERATOR
    : '+' // addition, concatenation, boolean or
    | '*' // multiplication, boolean and
    | '/'
    | '-'
    | '!'
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

NUMBER
    :   '-'? INT '.' [0-9]+ EXP? // 1.35, 1.35E-9, 0.3, -4.5
    |   '-'? INT EXP             // 1e10 -3e4
    |   '-'? INT                 // -3, 45
    ;
fragment INT :   '0' | [1-9] [0-9]* ; // no leading zeros
fragment EXP :   [Ee] [+\-]? INT ; // \- since - means "range" inside [...]

NEWLINE: [\r][\n] | [\n];

WS : [ \t\r\n]+ -> skip ;