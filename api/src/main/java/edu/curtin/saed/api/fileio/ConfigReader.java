package edu.curtin.saed.api.fileio;

import edu.curtin.saed.api.model.Item;
import edu.curtin.saed.api.model.Obstacle;
import edu.curtin.saed.api.model.Player;
import edu.curtin.saed.api.controllers.GameDataController;
import edu.curtin.saed.api.model.GameData;
import edu.curtin.saed.api.model.GridArea;
import edu.curtin.saed.api.model.GridAreaIcon; // Required for Item/Obstacle constructors

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Reads game configuration (size, start, goal, items, obstacles) from a file,
 * handling specific formatting and ignoring surrounding text and whitespace.
 */
public class ConfigReader {

    private int gridWidth, gridHeight;
    private int startX, startY;
    private int goalX, goalY;
    
    private List<Item> items;
    private List<Obstacle> obstacles;

    private static final Pattern COORD_PATTERN = Pattern.compile("(\\d+)[^\\d]+(\\d+)");
    
    private static final Pattern QUOTED_STRING_PATTERN = Pattern.compile("\"([^\"]*)\"");

    private GameDataController dataController = new GameDataController();


    /**
     * Reads the configuration from the specified file.
     * * @param filename The path to the configuration file.
     * @throws IOException If the file cannot be read or if the format is incorrect.
     */
    public ConfigReader(String filename) throws IOException {
        items = new ArrayList<>();
        obstacles = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            int lineCount = 0;
            
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue; 

                if (lineCount < 3) {
                    parseInitialCoordLine(line, lineCount);
                    lineCount++;
                } else {
                    parseBlock(reader, line);
                }
            }
            
            if (lineCount < 3) {
                throw new IOException("Configuration file is missing one or more required lines (size, start, goal).");
            }

        } catch (IOException e) {
            throw e;
        }
    }
    
    /**
     * Parses the size, start, or goal coordinate line.
     */
    private void parseInitialCoordLine(String line, int lineCount) throws IOException {
        String lowerLine = line.toLowerCase();
        int[] coords = readCoordinates(line); 
                
        if (coords.length != 2) {
            throw new IOException("Invalid coordinate format in line: " + line);
        }
        
        switch (lineCount) {
            case 0: // size (X, Y)
                if (!lowerLine.startsWith("size")) {
                    throw new IOException("Expected 'size' configuration on line 1. Found: " + lowerLine);
                }
                gridWidth = coords[0];
                gridHeight = coords[1];
                break;
            case 1: // start (X, Y)
                if (!lowerLine.startsWith("start")) {
                     throw new IOException("Expected 'start' configuration on line 2. Found: " + lowerLine);
                }
                startX = coords[0];
                startY = coords[1];
                break;
            case 2: // goal (X, Y)
                if (!lowerLine.startsWith("goal")) {
                     throw new IOException("Expected 'goal' configuration on line 3. Found: " + lowerLine);
                }
                goalX = coords[0];
                goalY = coords[1];
                break;
        }
    }
    
    /**
     * Identifies and delegates to the appropriate block reader method.
     */
    private void parseBlock(BufferedReader reader, String line) throws IOException {
        String lowerLine = line.toLowerCase();
        
        if (lowerLine.startsWith("item")) {
            readItemBlock(reader, line);
        } else if (lowerLine.startsWith("obstacle")) {
            readObstacleBlock(reader, line);
        }
    }

    /**
     * Reads a multi-line Item configuration block.
     */
    private void readItemBlock(BufferedReader reader, String headerLine) throws IOException {
        List<String> itemNames = readQuotedStrings(headerLine);
        if (itemNames.isEmpty()) {
            throw new IOException("Item block missing name: " + headerLine);
        }
        String itemName = itemNames.get(0);
        
        List<int[]> locations = new ArrayList<>();
        String message = "";
        String icon = "";
        
        String line;
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty()) continue;

            if (line.equals("}")) break; // End of block
            
            String lowerLine = line.toLowerCase();

            if (lowerLine.startsWith("at")) {
                locations.addAll(readCoordinatesList(line));
            } else if (lowerLine.startsWith("message")) {
                List<String> messages = readQuotedStrings(line);
                if (!messages.isEmpty()) {
                    message = messages.get(0);
                }
            } else if (lowerLine.startsWith("icon")) {
                List<String> icons = readQuotedStrings(line);
                if (!icons.isEmpty()) {
                    icon = icons.get(0);
                }
            }
        }
        
        for (int[] coords : locations) {
            InputStream itemImageStream = GameData.class.getClassLoader().getResourceAsStream(icon);
            Item item = new Item(itemName, new GridAreaIcon(coords[0], coords[1], 0.0, 1.0, itemImageStream, itemName), message);
            items.add(item); 
        }
    }

    /**
     * Reads a multi-line Obstacle configuration block.
     */
    private void readObstacleBlock(BufferedReader reader, String headerLine) throws IOException {
        List<String> obstacleNames = readQuotedStrings(headerLine);
        if (obstacleNames.isEmpty()) {
            throw new IOException("Obstacle block missing name: " + headerLine);
        }
        String obstacleName = obstacleNames.get(0);

        List<int[]> locations = new ArrayList<>();
        List<String> requiredItems = new ArrayList<>();
        
        String line;
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty()) continue;

            if (line.equals("}")) break; // End of block
            
            String lowerLine = line.toLowerCase();

            if (lowerLine.startsWith("at")) {
                locations.addAll(readCoordinatesList(line));
            } else if (lowerLine.startsWith("requires")) {
                requiredItems.addAll(readQuotedStrings(line));
            }
        }
        // Create an Obstacle object for every location specified
        for (int[] coords : locations) {
            InputStream obstacleImageStream;
            if (obstacleName.equals("Door")) {
                obstacleImageStream = GameData.class.getClassLoader().getResourceAsStream("door.png");
            } else if (obstacleName.equals("Monster")) {
                obstacleImageStream = GameData.class.getClassLoader().getResourceAsStream("shambling-mound.png");
            } else {
                obstacleImageStream = GameData.class.getClassLoader().getResourceAsStream("obstacle.png");
            }
            Obstacle obstacle = new Obstacle(new GridAreaIcon(coords[0], coords[1], 0.0, 1.0, obstacleImageStream, ""), requiredItems);
            obstacles.add(obstacle); 
        }
    }


    /**
     * Extracts the two integer coordinates from a string using regex (for single pair parsing).
     * For example, "start ( 1 , 5 )" returns [1, 5].
     */
    private int[] readCoordinates(String line) {
        Matcher matcher = COORD_PATTERN.matcher(line);
        if (matcher.find()) {
            try {
                int x = Integer.parseInt(matcher.group(1));
                int y = Integer.parseInt(matcher.group(2));
                return new int[]{x, y};
            } catch (NumberFormatException e) {
                System.err.println("Error parsing numbers in line: " + line);
                return new int[0];
            }
        }
        return new int[0];
    }
    
    /**
     * Extracts all (X, Y) coordinate pairs from a string, e.g., "at (1,1), (2,3)".
     */
    private List<int[]> readCoordinatesList(String line) {
        List<int[]> coordsList = new ArrayList<>();
        Matcher matcher = COORD_PATTERN.matcher(line);
        
        while (matcher.find()) {
             try {
                int x = Integer.parseInt(matcher.group(1));
                int y = Integer.parseInt(matcher.group(2));
                coordsList.add(new int[]{x, y});
            } catch (NumberFormatException e) {
                System.err.println("Error parsing numbers in coordinate list line: " + line);
                break; // Stop parsing if a bad number is found
            }
        }
        return coordsList;
    }
    
    /**
     * Extracts all quoted strings (e.g., item names, messages, required item names).
     */
    private List<String> readQuotedStrings(String line) {
        List<String> strings = new ArrayList<>();
        Matcher matcher = QUOTED_STRING_PATTERN.matcher(line);
        
        while (matcher.find()) {
            strings.add(matcher.group(1));
        }
        return strings;
    }

    public GameData inicializeGame() {
    GridArea area = new GridArea(gridWidth, gridHeight);
        Player player = new Player(new GridAreaIcon(
            startX,   // x
            startY,   // y
            0.0, // rotation (degrees)
            1.0, // scale
            GameData.class.getClassLoader().getResourceAsStream("player.png"),  // Image (InputStream)
            "Player"),
            1);
        GameData data = new GameData(player, 
            (new GridAreaIcon(
            goalX,   // x
            goalY,   // y
            0.0, // rotation (degrees)
            1.0, // scale
            GameData.class.getClassLoader().getResourceAsStream("goal.png"),  // Image (InputStream)
            "Goal")), area, obstacles, items, true);
        data.getArea().getIcons().add(data.getPlayer().getIcon());
        data.getArea().getIcons().add(data.getGoal());
        for (Obstacle obstacle : obstacles) {
            data.getArea().getIcons().add(obstacle.getIcon());
        }
        for (Item item : items) {
            data.getArea().getIcons().add(item.getIcon());
        }
        data.getArea().setStyle("-fx-background-color: #1f1f1fff;");
        for(int i = 1; i < data.getArea().getIcons().size(); i++) {
            data.getArea().getIcons().get(i).setShown(false);
        }
        dataController.revealSurroundingTile(data, data.getPlayer().getIcon().getX(), data.getPlayer().getIcon().getY(), data.getPlayer().getVision());
        System.out.println("Game Initialized!");
        return data;
  }
}

