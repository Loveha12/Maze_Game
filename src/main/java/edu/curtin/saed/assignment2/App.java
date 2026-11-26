package edu.curtin.saed.assignment2;

import edu.curtin.saed.api.apiinterfaces.PluginListenerApi;
import edu.curtin.saed.api.controllers.GameDataController;
import edu.curtin.saed.api.controllers.GameLogic;
import edu.curtin.saed.api.fileio.ConfigReader;
import edu.curtin.saed.api.model.GameData;
import edu.curtin.saed.api.model.Item;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate; 
import java.time.format.DateTimeFormatter; 
import java.time.format.FormatStyle; 
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class App extends Application
{
    private static GameLogic logic = new GameLogic(); // API Game Logic controller call
    private static GameData data = new GameData(null, null, null, null, null, false); // Game Data model
    private static GameDataController dataController = new GameDataController(); // API Game Data controller call
    
    // List to hold all dynamically loaded plugin instances
    private static List<PluginListenerApi> loadedPlugins; 
    
    // Configuration for the plugin loading mechanism
    private static final String PLUGIN_DIR = 
        "./plugins/build/classes/java/main/edu/curtin/saed/gameplugins";
    private static final String PLUGIN_PACKAGE = "edu.curtin.saed.gameplugins";
    
    // Date tracking fields for localization and display (reintroduced)
    private static final Locale LOCALE = Locale.getDefault();
    private static LocalDate currentDate;
    private static Label dateLabel;
    
    public static void main(String[] args)
    {
        currentDate = LocalDate.now(); 

        loadedPlugins = loadAllPlugins();
        try {
            ConfigReader configReader = new ConfigReader(args[0]);
            data = configReader.inicializeGame();
        } catch (IOException e) {
            System.out.println("File IO failed, mismatching type");
        }

        launch();
    }

    @Override
    public void start(Stage stage)
    {
        for (PluginListenerApi plugin : loadedPlugins) {
            dataController.registerListener(data, plugin);
        }
        
        dateLabel = new Label();
        updateDateDisplay(); 

        // Test item used to stop null pointers
        Item testItem = new Item("Iron Dagger", null, "Basic but useful" );
        data.getPlayer().addItemToInventory(testItem);

        // --- INVENTORY LIST IMPLEMENTATION ---
        var inventoryListView = new ListView<Item>(data.getPlayer().getInventory());
        inventoryListView.setPlaceholder(new Label("No items in inventory.")); // Custom message for empty list

        inventoryListView.setCellFactory(new Callback<ListView<Item>, ListCell<Item>>() {
            @Override
            public ListCell<Item> call(ListView<Item> list) {
                return new ListCell<Item>() {
                    @Override
                    protected void updateItem(Item item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            VBox itemBox = new VBox(2);
                            
                            Label nameLabel = new Label(item.getItemName());
                            nameLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #333;");
                            
                            Label descriptionLabel = new Label(
                                item.getDescription() != null && !item.getDescription().isEmpty() 
                                ? item.getDescription() 
                                : "No description."
                            );
                            descriptionLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #666;");
                            
                            itemBox.getChildren().addAll(nameLabel, descriptionLabel);

                            setGraphic(itemBox);
                            setText(null);
                        }
                    }
                };
            }
        });

        Thread renderGui = new Thread(() -> {
            try {
                while (data.getRunning()) {
                    Platform.runLater(() -> {
                        data.getArea().requestLayout();
                    });
                    Thread.sleep(50);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Main gui thread interrupted: " + e.getMessage());
            }
        });

        var moveLeft = new Button("<");
        var moveUp = new Button("^");
        var moveDown = new Button("v");
        var moveRight = new Button(">");

        moveLeft.setOnAction((event) -> handleMove('<'));
        moveRight.setOnAction((event) -> handleMove('>'));
        moveDown.setOnAction((event) -> handleMove('v'));
        moveUp.setOnAction((event) -> handleMove('^'));

        stage.setOnCloseRequest((event) ->
        {
            String finalDate = currentDate.format(
                DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).withLocale(LOCALE)
            );
            
            System.out.println("Close button pressed");
            System.out.println("\n--- GAME ENDED ---");
            System.out.println("Total Days/Moves Elapsed: " + data.getDaysElapsed()); 
            System.out.println("Final Game Date: " + finalDate); 
            System.out.println("------------------\n");
            
            logic.exitGame(data);
            renderGui.interrupt();
        });

        var pluginToolbar = new ToolBar();
        for (PluginListenerApi listener : data.getListeners()) {
            var pluginButton = new Button(listener.getPluginName());
            pluginButton.setOnAction((event) ->
            {
                logic.firePluginActionSelected(data, listener.getPluginName());
            });
            pluginToolbar.getItems().add(pluginButton);
        }

        var toolbar = new ToolBar();
        toolbar.getItems().addAll(moveLeft, moveUp, moveDown, moveRight);

        var splitPane = new SplitPane();
        splitPane.getItems().addAll(data.getArea(), inventoryListView);
        splitPane.setDividerPositions(0.75); // 75% for the game area, 25% for the inventory

        stage.setTitle("== MAZE GAME ==");
        var contentPane = new BorderPane();
        contentPane.setTop(new VBox(pluginToolbar, dateLabel)); 
        contentPane.setCenter(splitPane);
        contentPane.setBottom(toolbar);

        var scene = new Scene(contentPane, 1200, 1000);

        // WASD movement. A filter is used so the keys work no matter which
        // control currently has focus (buttons, inventory list, etc.).
        scene.addEventFilter(KeyEvent.KEY_PRESSED, (event) ->
        {
            switch (event.getCode())
            {
                case W:
                    handleMove('^');
                    event.consume();
                    break;
                case A:
                    handleMove('<');
                    event.consume();
                    break;
                case S:
                    handleMove('v');
                    event.consume();
                    break;
                case D:
                    handleMove('>');
                    event.consume();
                    break;
                default:
                    break;
            }
        });
        stage.setScene(scene);
        stage.show();

        renderGui.start();
    }
    
    /**
     * Attempts a move in the given direction ('<', '>', '^' or 'v').
     * Shared by the on-screen buttons and the WASD key handler.
     */
    private static void handleMove(char direction) {
        currentDate = currentDate.plusDays(1); // Advance date first
        logic.movePlayer(direction, data);
        updateDateDisplay();
    }

    /**
     * Formats the current date and move count for display on the GUI.
     */
    private static void updateDateDisplay() {
        String formattedDate = currentDate.format(
            DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).withLocale(LOCALE)
        );
        
        dateLabel.setText(
            String.format("Current Game Date: %s ", 
                          formattedDate)
        );
    }
    
    private static List<PluginListenerApi> loadAllPlugins() {
        List<PluginListenerApi> plugins = new ArrayList<>();
        File pluginDir = new File(PLUGIN_DIR);

        if (!pluginDir.exists() || !pluginDir.isDirectory()) {
            System.err.println("Error: Plugin directory not found at " + PLUGIN_DIR);
            System.err.println("Please run './gradlew build' to compile plugins before running the application.");
            return plugins;
        }

        File[] classFiles = pluginDir.listFiles((dir, name) -> name.endsWith(".class"));
        
        if (classFiles == null) {
            System.err.println("Error: Could not read plugin directory contents.");
            return plugins;
        }

        for (File file : classFiles) {
            String className = file.getName().replace(".class", "");
            String fqClassName = PLUGIN_PACKAGE + "." + className;

            try {
                Class<?> cls = Class.forName(fqClassName);

                if (PluginListenerApi.class.isAssignableFrom(cls) && !cls.isInterface()) {
                    
                    Class<? extends PluginListenerApi> pluginClass = 
                        cls.asSubclass(PluginListenerApi.class);
                    
                    Constructor<? extends PluginListenerApi> constructor = pluginClass.getDeclaredConstructor();
                    PluginListenerApi plugin = constructor.newInstance();
                    
                    plugins.add(plugin);
                    System.out.println("Plugin loaded successfully: " + fqClassName);
                }
            } catch (ClassNotFoundException e) {
                System.err.println("Failed to load class definition for: " + fqClassName);
            } catch (NoSuchMethodException e) {
                System.err.println("Failed to load plugin " + fqClassName + ". Reason: No public no-argument constructor found.");
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
                System.err.println("Failed to instantiate plugin " + fqClassName + ". Reason: Constructor execution error.");
            } catch (ClassCastException e) {
                System.err.println("Failed to load plugin " + fqClassName + ". Reason: Class does not implement PluginListenerApi.");
            }
        }
        return plugins;
    }
}
