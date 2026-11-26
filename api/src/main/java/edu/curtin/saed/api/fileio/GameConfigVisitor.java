// package edu.curtin.saed.api.fileio;

// import edu.curtin.saed.assignment2.GameConfigBaseVisitor;
// import edu.curtin.saed.assignment2.GameConfigParser;
// import edu.curtin.saed.assignment2.GameConfigParser.Coordinate_pairContext;
// import edu.curtin.saed.assignment2.GameConfigParser.String_listContext;
// import edu.curtin.saed.assignment2.GameConfigParser.At_declarationContext;

// import java.util.ArrayList;
// import java.util.List;

// public class GameConfigVisitor extends GameConfigBaseVisitor<Void> {
    
//     private final ConfigReader reader;

//     public GameConfigVisitor(ConfigReader reader) {
//         this.reader = reader;
//     }

//     /**
//      * Helper to extract the (r, c) integers from a coordinate_pair context.
//      * @param ctx The ANTLR context for a single coordinate pair.
//      * @return An array of [row, column].
//      */
//     private int[] extractCoords(Coordinate_pairContext ctx) {
//         // INT tokens are at index 1 and 3 in: ( LPAREN INT COMMA INT RPAREN )
//         int row = Integer.parseInt(ctx.INT(0).getText());
//         int col = Integer.parseInt(ctx.INT(1).getText());
//         return new int[]{row, col};
//     }

//     /**
//      * Helper to extract the row/column locations from an at_declaration context.
//      * @param ctx The ANTLR context for an 'at' declaration.
//      * @return A list of [row, column] arrays.
//      */
//     private List<int[]> extractLocationList(At_declarationContext ctx) {
//         List<int[]> locations = new ArrayList<>();
//         for (Coordinate_pairContext coordCtx : ctx.coordinate_pair()) {
//             locations.add(extractCoords(coordCtx));
//         }
//         return locations;
//     }

//     // ====================================================================
//     // DECLARATIONS (size, start, goal)
//     // ====================================================================

//     @Override
//     public Void visitSize_decl(GameConfigParser.Size_declContext ctx) {
//         int[] coords = extractCoords(ctx.coordinate_pair());
//         // Validation for size: must be at least 1
//         if (coords[0] < 1 || coords[1] < 1) {
//             throw new IllegalArgumentException("Size row and column must be at least 1.");
//         }
//         reader.setSize(coords[0], coords[1]); // r=Height, c=Width
//         return null;
//     }

//     @Override
//     public Void visitStart_decl(GameConfigParser.Start_declContext ctx) {
//         int[] coords = extractCoords(ctx.coordinate_pair());
//         // NOTE: The visitor doesn't perform bounds checking (0 <= r < R, etc.). 
//         // This is usually done later in a validation phase, after 'size' is known.
//         reader.setStart(coords[0], coords[1]); // r=Y, c=X
//         return null;
//     }

//     @Override
//     public Void visitGoal_decl(GameConfigParser.Goal_declContext ctx) {
//         int[] coords = extractCoords(ctx.coordinate_pair());
//         reader.setGoal(coords[0], coords[1]); // r=Y, c=X
//         return null;
//     }

//     // ====================================================================
//     // FEATURES
//     // ====================================================================

//     @Override
//     public Void visitItem_definition(GameConfigParser.Item_definitionContext ctx) {
//         // 1. Get Item Name (STRING token after 'item')
//         String rawName = ctx.STRING().getText();
//         String itemName = rawName.substring(1, rawName.length() - 1); // Strip quotes

//         // 2. Get Message
//         String rawMessage = ctx.message_declaration().STRING().getText();
//         String message = rawMessage.substring(1, rawMessage.length() - 1); // Strip quotes

//         // 3. Get Locations
//         List<int[]> locations = extractLocationList(ctx.at_declaration());
        
//         // 4. Create and add an Item for each location
//         for (int[] coords : locations) {
//             reader.addItem(itemName, coords[0], coords[1], message); // r=Y, c=X
//         }
//         return null;
//     }

//     @Override
//     public Void visitObstacle_definition(GameConfigParser.Obstacle_definitionContext ctx) {
//         // 1. Get Required Items
//         String_listContext stringListCtx = ctx.requires_declaration().string_list();
//         List<String> requiredItems = new ArrayList<>();
//         for (org.antlr.v4.runtime.tree.TerminalNode stringNode : stringListCtx.STRING()) {
//             String rawString = stringNode.getText();
//             // Strip quotes
//             requiredItems.add(rawString.substring(1, rawString.length() - 1));
//         }

//         // 2. Get Locations
//         List<int[]> locations = extractLocationList(ctx.at_declaration());

//         // 3. Create and add an Obstacle for each location
//         for (int[] coords : locations) {
//             reader.addObstacle(coords[0], coords[1], requiredItems); // r=Y, c=X
//         }
//         return null;
//     }

//     @Override
//     public Void visitPlugin_declaration(GameConfigParser.Plugin_declarationContext ctx) {
//         // CLASS_NAME token after 'plugin'
//         String className = ctx.CLASS_NAME().getText();
//         reader.addPlugin(className);
//         return null;
//     }
    
//     @Override
//     public Void visitScript_declaration(GameConfigParser.Script_declarationContext ctx) {
//         // PYTHON_CODE token after 'script'
//         String rawCode = ctx.PYTHON_CODE().getText();
//         // The lexer rule is: '!{' (~'}')* '}'
//         // Strip '!{' and '}'
//         String pythonCode = rawCode.substring(2, rawCode.length() - 1).trim(); 

//         // NOTE: The user requested to simplify and assume the code won't contain a closing '}' 
//         // and to not worry about indentation removal unless desired. We'll leave the code as is.
//         reader.addScript(pythonCode);
//         return null;
//     }
// }