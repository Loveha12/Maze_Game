grammar GameConfig;

// ====================================================================
// PARSER RULES (lowercase) - Define the language structure
// ====================================================================

// 1. The root of the configuration file: three mandatory declarations, followed by features
config
    : declaration_block feature_block* EOF
    ;

// 1.1. Mandatory starting declarations
declaration_block
    : size_decl start_decl goal_decl
    ;

// 1.2. Declarations
size_decl
    : SIZE coordinate_pair
    ;

start_decl
    : START coordinate_pair
    ;

goal_decl
    : GOAL coordinate_pair
    ;

// 1.3. Reusable rule for (r,c) pairs
coordinate_pair
    : LPAREN INT COMMA INT RPAREN
    ;

// 2. Main structure for optional features (items, obstacles, plugins)
feature_block
    : item_definition
    | obstacle_definition
    | plugin_declaration
    | script_declaration
    ;

// 2.1. Item Definition
item_definition
    : ITEM STRING LBRACE
      at_declaration
      message_declaration
      RBRACE
    ;

// 2.2. Obstacle Definition
obstacle_definition
    : OBSTACLE LBRACE
      at_declaration
      requires_declaration
      RBRACE
    ;

// 2.3. Plugin Definition
plugin_declaration
    : PLUGIN CLASS_NAME
    ;

// 2.4. Script Definition
script_declaration
    : SCRIPT PYTHON_CODE
    ;

// 3. Declarations inside { ... } blocks
at_declaration
    : AT coordinate_pair (COMMA coordinate_pair)*
    ;

message_declaration
    : MESSAGE STRING
    ;

requires_declaration
    : REQUIRES string_list
    ;

string_list
    : STRING (COMMA STRING)*
    ;


// ====================================================================
// LEXER RULES (UPPERCASE) - Define the vocabulary
// ====================================================================

// --- Keywords ---
SIZE     : 'size';
START    : 'start';
GOAL     : 'goal';
ITEM     : 'item';
OBSTACLE : 'obstacle';
PLUGIN   : 'plugin';
AT       : 'at';
MESSAGE  : 'message';
REQUIRES : 'requires';
SCRIPT   : 'script';

// --- Symbols ---
LPAREN   : '(';
RPAREN   : ')';
LBRACE   : '{';
RBRACE   : '}';
COMMA    : ',';
DOT      : '.';

// --- Data Types ---
INT      : [0-9]+;
STRING   : '"' (~["\r\n])* '"';
DOUBLE   : [0-9]+ '.' [0-9]+; // literal dot wrapped in single quotes

// Used for the 'plugin' declaration.
CLASS_NAME : IDENTIFIER (DOT IDENTIFIER)*;

// Used as the base for CLASS_NAME.
IDENTIFIER : [a-zA-Z_] [a-zA-Z_0-9]*;

// Matches '!{...}', capturing all content until the first '}'.
// Uses a simpler approach assuming no nested '}' in the Python code
PYTHON_CODE : '!{' ( ~'}' )* '}' ;

// --- Ignored Tokens ---
WS       : [ \t\r\n]+ -> skip;
