package org.leonitousconforti.basilisk.core;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.leonitousconforti.basilisk.Basilisk;
import org.leonitousconforti.basilisk.Config;
import org.leonitousconforti.basilisk.detectors.Detector;

import de.milchreis.uibooster.UiBooster;
import de.milchreis.uibooster.components.Form;
import de.milchreis.uibooster.model.FilledForm;
import de.milchreis.uibooster.model.UiBoosterOptions;

/**
 * Creates the UI settings forms.
 */
@SuppressWarnings("checkstyle:Indentation")
public class UiSettingsForms {
    // Components
    private final UiBooster uiBooster;
    private final Basilisk basilisk;
    private final GameElementDetection gameElmDetection;
    private final Algorithms algorithmsManager;

    // Forms
    private final Form settingsForm;
    private final Form detectorSettings;
    private final Form algorithmSettings;
    private FilledForm profileCreationWizard;

    /**
     * Creates the UI settings forms.
     *
     * @param basil the ai of basilisk
     */
    public UiSettingsForms(Basilisk basil) {
        basilisk = basil;
        gameElmDetection = basilisk.getGameElementDetection();
        algorithmsManager = basilisk.getAlgorithmsManager();

        // Make the settings form
        uiBooster = new UiBooster(new UiBoosterOptions(UiBoosterOptions.Theme.DARK_THEME));
        settingsForm = createSettingsForm();

        // Detector specific settings
        detectorSettings = createDetectorSettingsForm();

        // Algorithm specific settings
        algorithmSettings = createAlgorithmsSettingsForm();
    }

    /**
     * Creates the main settings form.
     */
    private Form createSettingsForm() {
        return uiBooster.createForm("Basilisk Settings")
                // Status label
                .addLabel("Status")
                // Button to pause and resume the processing
                .addButton("Pause / Resume", () -> Config.paused = !Config.paused)
                .addButton("Quit program", () -> System.exit(0))
                // Detector form
                .addButton("Detector Settings", "additional detector setting", () -> getDetectorSettings().run())
                // Algorithm form
                .addButton("Algorithm Settings", "additional algorithm settings", () -> getAlgorithmSettings().run())
                // Open play snake page button
                .addButton("Google Snake", "open the play snake page", () -> basilisk.openGoogleSnakeWindow());
    }

    /**
     * Creates the detector settings form.
     */
    @SuppressWarnings({ "checkstyle:NoWhitespaceAfter", "checkstyle:MagicNumber" })
    private Form createDetectorSettingsForm() {
        return uiBooster.createForm("Detector Settings")
                // View loaded snake configurations button
                .addButton("View Loaded Snake Configurations", "view snake configurations", () -> {
                    // Make the 2d list
                    String[][] elms = new String[gameElmDetection.getLoadedSnakeDetectors().size()][3];

                    // Get the configurations loaded
                    for (int i = 0; i < gameElmDetection.getLoadedSnakeDetectors().size(); i++) {
                        Detector d = gameElmDetection.getLoadedSnakeDetectors().get(i);
                        elms[i] = new String[] { d.getName(), d.getColor().toString(), Float.toString(d.getHue()) };
                    }

                    // Show them as list
                    uiBooster.showTable(elms, Arrays.asList("Name", "Color", "Hue"), "Snake Configuration Profiles");
                })
                // View loaded apple configurations button
                .addButton("View Loaded Apple Configurations", "view apple configurations", () -> {
                    // Make the 2d list
                    String[][] elms = new String[gameElmDetection.getLoadedThingsToEatDetectors().size()][3];

                    // Get the loaded configurations
                    for (int i = 0; i < gameElmDetection.getLoadedThingsToEatDetectors().size(); i++) {
                        Detector d = gameElmDetection.getLoadedThingsToEatDetectors().get(i);
                        elms[i] = new String[] { d.getName(), d.getColor().toString(), Float.toString(d.getHue()) };
                    }

                    // Show them as list
                    uiBooster.showTable(elms, Arrays.asList("Name", "Color", "Hue"), "Apple Configuration Profiles");
                })
                // Add some spacing
                .addLabel("")
                // Configuration label
                .addLabel("Configurations")
                // Button for snake profile selection
                .addButton("Select snake profile", () -> {
                    // Get the loaded detectors
                    List<String> snakes = gameElmDetection.getLoadedSnakeDetectors().stream().map(d -> d.getName())
                            .collect(Collectors.toList());

                    // Show the loaded detectors in a selection window
                    String selection = uiBooster.showSelectionDialog("Select the snake detector", "Snake Detector",
                            snakes);
                    // Set the selection
                    if (selection != null) {
                        gameElmDetection.setSelectedSnakeDetector(selection);
                    }
                })
                // Button for apple profile selection
                .addButton("Select apple profile", () -> {
                    // Get the loaded detectors
                    List<String> apples = gameElmDetection.getLoadedThingsToEatDetectors().stream()
                            .map(d -> d.getName()).collect(Collectors.toList());

                    // Show the loaded detectors in a selection window
                    String selection = uiBooster.showSelectionDialog("Select the apple detector", "Apple Detector",
                            apples);
                    // Set the selection
                    if (selection != null) {
                        gameElmDetection.setSelectedThingToEatDetector(selection);
                    }
                })
                // Add some spacing
                .addLabel("")
                // Make new configuration profile button
                .addButton("Add New Configuration Profile", "add configuration", () -> {
                    // Info dialog
                    uiBooster.showInfoDialog(
                            "This wizard will walk you through adding a\nnew snake and apple configuration profile");

                    // Profile wizard form
                    profileCreationWizard = uiBooster.createForm("Profile Wizard")
                            // Get the names for the profiles
                            .addTextArea("What do you want to name this snake profile?")
                            .addTextArea("What do you want to name this apple profile?")

                            // Add button
                            .addButton("View example image", () -> uiBooster.showPicture("Example Image",
                                    basilisk.loadResource("exampleProfile", ".png")))
                            .show();

                    // Get user inputs
                    String snakeName = profileCreationWizard.getByIndex(0).asString();
                    String appleName = profileCreationWizard.getByIndex(1).asString();

                    // Show confirm dialog
                    uiBooster.showConfirmDialog(
                            // Message
                            "Basilisk will now automatically attempt to capture the game window."
                                    + "\nAt this time, make sure that the game window looks like the example image."
                                    + "\nYou can view the example image on the form.",
                            // Title
                            "Starting capture",

                            // On success click
                            () -> {
                                basilisk.createDetectorForCurrentGameConfig(snakeName, appleName);
                                uiBooster.showInfoDialog("Success! You can utilize the new detectors by selecting"
                                        + "\nthem under select apple and snake detectors");
                            },

                            // On failure click
                            () -> {
                                uiBooster.showWarningDialog("No new configuration added: action canceled by user",
                                        "Action canceled by user");
                            });

                });
    }

    /**
     * Creates the algorithms settings form.
     */
    @SuppressWarnings("checkstyle:NoWhitespaceAfter")
    private Form createAlgorithmsSettingsForm() {
        return uiBooster.createForm("Algorithm Settings")
                // Load out-sourced algorithm button
                .addButton("External Algorithms", "load external algorithm", () -> {
                    uiBooster.showWarningDialog(
                            "If you don't know what you are doing, you are probably in the wrong place."
                                    + "\nConsult the wiki page for how you algorithm needs to be structure first.",
                            "Caution");

                    // Get the file of the algorithm
                    File algorithmFileToLoad = uiBooster.showFileSelection();

                    // Attempt to load it!
                    try {
                        algorithmsManager.loadOutSourcedAlgorithm(algorithmFileToLoad.getAbsolutePath());

                        // Show success message
                        uiBooster.showInfoDialog("You algorithm code has been loaded successfully!");
                    } catch (MalformedURLException | ClassNotFoundException | InstantiationException
                            | IllegalAccessException e) {
                        uiBooster.showErrorDialog(
                                "There was an error while loading your algorithm code.\n" + e.getMessage(), "Error!");
                        // e.printStackTrace();
                    }
                })
                // Add view current algorithms
                .addButton("Loaded Algorithms", "view loaded algorithms", () -> {
                    // Make the 2d list
                    String[][] elms = new String[algorithmsManager.getAllLoadedAlgorithms().length][2];

                    // Get the loaded algorithms
                    for (int i = 0; i < elms.length; i++) {
                        String name = algorithmsManager.getAllLoadedAlgorithms()[i];
                        String runner = algorithmsManager.getRunningAlgorithm().getName() == name ? "running"
                                : "stopped";
                        elms[i] = new String[] { name, runner };
                    }

                    // Show them as list
                    uiBooster.showTable(elms, Arrays.asList("Name", "Status"), "Loaded Algorithms");
                })
                // Set algorithm to run
                .addButton("Set Algorithm", "set algorithm to run", () -> {
                    // Get the loaded algorithms
                    String[] algorithms = algorithmsManager.getAllLoadedAlgorithms();

                    // Show the loaded detectors in a selection window
                    String selection = uiBooster.showSelectionDialog("Select the running algorithm",
                            "Running Algorithm", algorithms);

                    // Set the selection
                    if (selection != null) {
                        algorithmsManager.setRunningAlgorithm(selection);
                    }
                })
                // Actions manager inject settings
                .addLabel("Key Injection Mode")
                // Use java keyer
                .addButton("java keyer mode", () -> {
                    algorithmsManager.getActionsManager().setDefaultExecutor("keyer");
                })
                // Use JS websocket
                .addButton("javascript websocket mode", () -> {
                    algorithmsManager.getActionsManager().setDefaultExecutor("websocket");
                })
                // View number of total connections
                .addButton("num of client on websocket server", () -> {
                    uiBooster.showInfoDialog(
                            "There are " + algorithmsManager.getActionsManager().getNumberOfWebsocketConnections()
                                    + " connections to the websocket server");
                })
                // Clear the actions queue
                .addButton("Action Queue", "clear the actions queue", () -> {
                    algorithmsManager.getActionsManager().wipe();
                });
    }

    /**
     * @return the general settings form
     */
    public Form getSettingsForm() {
        return settingsForm;
    }

    /**
     * @return the detector settings form
     */
    public Form getDetectorSettings() {
        return detectorSettings;
    }

    /**
     * @return the algorithm settings form
     */
    public Form getAlgorithmSettings() {
        return algorithmSettings;
    }
}
