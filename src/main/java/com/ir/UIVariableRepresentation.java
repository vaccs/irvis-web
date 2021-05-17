package com.ir;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.math.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import javafx.beans.value.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.canvas.*;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.text.*;
import javafx.stage.*;
import util.ArchitectureType;
import util.HttpsUtils;
import value.CValue;
import value.CValueFactory;

public class UIVariableRepresentation {

  private static String currentVersion = "";
  private static Canvas canvas, canvasBottom;
  private static GraphicsContext gc, gcBottom;

  public static double fontSize = 1.0;
  // private static VariableRepresentation representation;
  private static CValue topValue;
  private static CValue bottomValue;
  private static CValueFactory factory = new CValueFactory();
  public static Scene scene = null;
  private static boolean updateInProgress;
  private static Stage window;

  private static ComboBox<String> cboTopEndianness;
  private static ComboBox<String> cboTopType;
  private static TextField txtBytesTop;
  private static TextField txtValueTop;

  private static ComboBox<String> cboBottomEndianness;
  private static ComboBox<String> cboBottomType;
  private static TextField txtBytesBottom;
  private static TextField txtValueBottom;

  private static String versionURL;
  private static String tgzURL;
  private static String javafxVersion = "javafx-sdk-11.0.2";

  public static void display(String typeIn, String value) {
    versionURL = "https://github.com/vaccs/irvis-" + getOS() + "/blob/master/version.txt";
    tgzURL = "https://github.com/vaccs/irvis-" + getOS() + "/archive/";

    updateInProgress = false;

    window = new Stage();
    window.setTitle("Integer Representations");
    window.initModality(Modality.APPLICATION_MODAL);

    topValue = factory.makeCValue(typeIn).addValue(value, typeIn);

    BorderPane container = new BorderPane();

    GridPane grid = new GridPane();
    grid.setHgap(8);
    grid.setVgap(24);
    grid.setPadding(new Insets(10, 10, 10, 10));

    {
      ColumnConstraints column = new ColumnConstraints();
      column.setPercentWidth(25.0);
      grid.getColumnConstraints().add(column);
    }

    {
      ColumnConstraints column = new ColumnConstraints();
      column.setPercentWidth(25.0);
      grid.getColumnConstraints().add(column);
    }

    {
      ColumnConstraints column = new ColumnConstraints();
      column.setPercentWidth(50.0);
      grid.getColumnConstraints().add(column);
    }

    cboTopType = new ComboBox<>();
    cboTopType.getItems().addAll(CValue.CHAR, CValue.UCHAR, CValue.SHORT, CValue.USHORT, CValue.INT, CValue.UINT,
        CValue.LONG, CValue.ULONG);
    cboTopType.getSelectionModel().select(getIndexFromType(typeIn));
    cboTopType.setOnAction(e -> {
      if (!updateInProgress) {
        updateInProgress = true;
        topValue = createValue(topValue, getConvertedValue(topValue, cboTopType.getValue()), cboTopType.getValue(),
            cboTopEndianness.getValue());
        txtValueTop.setText(topValue.toString());
        updateInProgress = false;
        updateUI();
      }
    });
    grid.add(cboTopType, 0, 0, 1, 1);

    cboTopEndianness = new ComboBox<>();
    cboTopEndianness.getItems().addAll(ArchitectureType.BIG_ENDIAN, ArchitectureType.LITTLE_ENDIAN);
    cboTopEndianness.getSelectionModel().select(0);
    cboTopEndianness.setOnAction(e -> {
      if (!updateInProgress) {
        updateInProgress = true;
        topValue = createValue(topValue, topValue.toString(), cboTopType.getValue(), cboTopEndianness.getValue());
        txtValueTop.setText(topValue.toString());
        updateInProgress = false;
        updateUI();
      }
    });
    grid.add(cboTopEndianness, 1, 0, 1, 1);

    txtValueTop = new TextField(value);
    txtValueTop.textProperty().addListener((obs, oldValue, newValue) -> {
      if (!updateInProgress) {
        updateInProgress = true;
        topValue = createValue(topValue, newValue, cboTopType.getValue(), cboTopEndianness.getValue());
        txtValueTop.setText(newValue);
        updateUI();
        updateInProgress = false;
      }

    });
    grid.add(txtValueTop, 2, 0, 1, 1);

    txtBytesTop = new TextField(topValue.getHexValueWithError());
    grid.add(txtBytesTop, 0, 1, 3, 1);

    Button btnDecrementValue = new Button("Decrement value (-)");
    btnDecrementValue.setOnAction(e -> {
      topValue.decrement();
      txtValueTop.setText(topValue.toString()); // biv.toString()
      updateUI();
    });
    grid.add(btnDecrementValue, 0, 2, 1, 1);

    Button btnIncrementValue = new Button("Increment value (+)");
    btnIncrementValue.setOnAction(e -> {
      topValue.increment();
      txtValueTop.setText(topValue.toString()); // biv.toString()
      updateUI();
    });
    grid.add(btnIncrementValue, 1, 2, 1, 1);

    // Value lines
    canvas = new Canvas(500, 80);
    canvas.widthProperty().bind(window.widthProperty().multiply(0.9));
    canvas.heightProperty().bind(window.heightProperty().divide(8));
    gc = canvas.getGraphicsContext2D();
    canvas.widthProperty().addListener(observable -> drawVisualization(canvas.getWidth(), canvas.getHeight(), gc,
        topValue.getHexValue(), txtValueTop.getText(), cboTopType.getValue()));
    canvas.heightProperty().addListener(observable -> drawVisualization(canvas.getWidth(), canvas.getHeight(), gc,
        topValue.getHexValue(), txtValueTop.getText(), cboTopType.getValue()));
    grid.add(canvas, 0, 3, 3, 1);

    canvasBottom = new Canvas(500, 80);
    canvasBottom.widthProperty().bind(window.widthProperty().multiply(0.9));
    canvasBottom.heightProperty().bind(window.heightProperty().divide(8));
    gcBottom = canvasBottom.getGraphicsContext2D();
    canvasBottom.widthProperty()
        .addListener(observable -> drawVisualization(canvasBottom.getWidth(), canvasBottom.getHeight(), gcBottom,
            bottomValue.getHexValue(), txtValueBottom.getText(), cboBottomType.getValue()));
    canvasBottom.heightProperty()
        .addListener(observable -> drawVisualization(canvasBottom.getWidth() - 10, canvasBottom.getHeight() - 10,
            gcBottom, bottomValue.getHexValue(), txtValueBottom.getText(), cboBottomType.getValue()));
    grid.add(canvasBottom, 0, 4, 3, 1);

    // Bottom part
    Label lblInterpret = new Label("Interpret As:");
    grid.add(lblInterpret, 0, 5, 1, 1);

    cboBottomType = new ComboBox<>();
    cboBottomType.getItems().addAll(CValue.CHAR, CValue.UCHAR, CValue.SHORT, CValue.USHORT, CValue.INT, CValue.UINT,
        CValue.LONG, CValue.ULONG);
    cboBottomType.getSelectionModel().select(getIndexFromType(typeIn));
    cboBottomType.setOnAction(e -> {
      updateUI();
    });
    grid.add(cboBottomType, 0, 6, 1, 1);

    cboBottomEndianness = new ComboBox<>();
    cboBottomEndianness.getItems().addAll(ArchitectureType.BIG_ENDIAN, ArchitectureType.LITTLE_ENDIAN);
    cboBottomEndianness.getSelectionModel().select(0);
    cboBottomEndianness.setOnAction(e -> {
      updateUI();
    });
    grid.add(cboBottomEndianness, 1, 6, 1, 1);

    bottomValue = factory.makeCValue(cboBottomType.getValue()).addValue(topValue, cboBottomType.getValue())
        .addEndian(cboBottomEndianness.getValue());

    txtValueBottom = new TextField(bottomValue.toString());
    txtValueBottom.setEditable(false);
    grid.add(txtValueBottom, 2, 6, 1, 1);

    txtBytesBottom = new TextField(bottomValue.getHexValue());
    grid.add(txtBytesBottom, 0, 7, 3, 1);

    // View menu
    Menu viewMenu = new Menu("Menu");

    MenuItem menuIncreaseFontSize = new MenuItem("Increase Font Size");
    menuIncreaseFontSize.setOnAction(e -> {
      if (fontSize < 5.0)
        fontSize += 0.1;
      grid.setStyle("-fx-font-size: " + UIUtils.calculateFontSize(fontSize, scene.getWidth(), scene.getHeight()));
    });
    menuIncreaseFontSize.setAccelerator(new KeyCodeCombination(KeyCode.EQUALS, KeyCombination.CONTROL_DOWN));
    viewMenu.getItems().add(menuIncreaseFontSize);

    MenuItem menuDecreaseFontSize = new MenuItem("Decrease Font Size");
    menuDecreaseFontSize.setOnAction(e -> {
      if (fontSize > 0.1)
        fontSize -= 0.1;
      grid.setStyle("-fx-font-size: " + UIUtils.calculateFontSize(fontSize, scene.getWidth(), scene.getHeight()));
    });
    menuDecreaseFontSize.setAccelerator(new KeyCodeCombination(KeyCode.MINUS, KeyCombination.CONTROL_DOWN));
    viewMenu.getItems().add(menuDecreaseFontSize);

    MenuItem checkForUpdates = new MenuItem("Check for Updates");
    checkForUpdates.setOnAction(e -> {
      HttpsUtils hUtil = new HttpsUtils();
      String latestVersion = "";
      try {
        latestVersion = parseHTMLDataForVersion(hUtil.getDataFileContents(versionURL));
      } catch (IOException e1) {
        latestVersion = currentVersion;
      }
      try {
        if (latestIsNewer(latestVersion)) {
          askForDownload(hUtil, latestVersion);
          return;
        }
      } catch (URISyntaxException | IOException e1) {
      }

      Alert info = new Alert(AlertType.INFORMATION);
      info.setTitle("Check irvis version Dialog");
      info.setHeaderText("Your version is up-to-date.");
      info.setContentText("No irvis update needed.");
      info.showAndWait();

    });
    viewMenu.getItems().add(checkForUpdates);
    // Main menu bar
    MenuBar menuBar = new MenuBar();
    menuBar.getMenus().addAll(viewMenu);

    MenuItem menuQuit = new MenuItem("Quit");
    menuQuit.setOnAction(e -> {
      ButtonType yesButtonType = new ButtonType("Yes", ButtonData.YES);
      ButtonType noButtonType = new ButtonType("No", ButtonData.NO);
      Dialog<ButtonType> dialog = new Dialog<>();
      dialog.setTitle("Quit irvis Dialog");
      dialog.setContentText("Do you want to quit irvis?");
      dialog.getDialogPane().getButtonTypes().add(noButtonType);
      dialog.getDialogPane().getButtonTypes().add(yesButtonType);
      boolean disabled = false; // computed based on content of text fields, for example
      dialog.getDialogPane().lookupButton(yesButtonType).setDisable(disabled);
      dialog.getDialogPane().lookupButton(noButtonType).setDisable(disabled);
      dialog.showAndWait().filter(response -> response.getText() == "Yes").ifPresent(response -> System.exit(0));
    });
    viewMenu.getItems().add(menuQuit);

    container.setTop(menuBar);
    container.setCenter(grid);

    scene = new Scene(container, 800, 600);

    // scene size change listeners
    scene.widthProperty().addListener(new ChangeListener<Number>() {
      @Override
      public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth,
          Number newSceneWidth) {
        grid.setStyle("-fx-font-size: " + UIUtils.calculateFontSize(fontSize, scene.getWidth(), scene.getHeight()));
      }
    });
    scene.heightProperty().addListener(new ChangeListener<Number>() {
      @Override
      public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight,
          Number newSceneHeight) {
        grid.setStyle("-fx-font-size: " + UIUtils.calculateFontSize(fontSize, scene.getWidth(), scene.getHeight()));
      }
    });

    window.setScene(scene);
    window.showAndWait();
  }

  private static String parseHTMLDataForVersion(String dataFileContents) {
    String version = currentVersion;
    int index = dataFileContents.indexOf("<td id=\"LC1\"");
    String rest = dataFileContents.substring(index);
    index = rest.indexOf('>');
    version = rest.substring(index + 1, rest.indexOf("</td>"));

    return version;
  }

  private static void askForDownload(HttpsUtils hUtil, String newVersion) {

    ButtonType yesButtonType = new ButtonType("Yes", ButtonData.YES);
    ButtonType noButtonType = new ButtonType("No", ButtonData.NO);
    Dialog<ButtonType> dialog = new Dialog<>();
    dialog.setTitle("New irvis Version Dialog");
    dialog.setContentText("A new version (" + newVersion +") of irvis is available. Do you want to install it?");
    dialog.getDialogPane().getButtonTypes().add(noButtonType);
    dialog.getDialogPane().getButtonTypes().add(yesButtonType);
    boolean disabled = false; // computed based on content of text fields, for example
    dialog.getDialogPane().lookupButton(yesButtonType).setDisable(disabled);
    dialog.getDialogPane().lookupButton(noButtonType).setDisable(disabled);
    dialog.showAndWait().filter(response -> response.getText() == "Yes").ifPresent(response -> {
      String jarDir = "";
      try {
        jarDir = new File(UIVariableRepresentation.class.getProtectionDomain().getCodeSource().getLocation().toURI())
            .getParent();
      } catch (URISyntaxException e) {
        Alert info = new Alert(AlertType.INFORMATION);
        info.setTitle("Download irvis Dialog");
        info.setHeaderText("Unable to determine installation directory automatically.");
        info.setContentText("New irvis not installed.");
        info.showAndWait();
        return;
      }
      try {
        Alert info = new Alert(AlertType.INFORMATION);
        info.setTitle("Download irvis Dialog");
        info.setHeaderText("A new version of irvis wil download and install");
        info.setContentText("This may take awhile and irvis will restart when done");
        info.showAndWait();
        String dir = jarDir + System.getProperty("file.separator");
        hUtil.getTarGzipFile(tgzURL + newVersion + ".tar.gz", dir, javafxVersion, true);
        restartApplication();
      } catch (IOException e) {
        Alert info = new Alert(AlertType.INFORMATION);
        info.setTitle("Download irvis Dialog");
        info.setHeaderText("Error downloading and installing new version of irvis");
        info.setContentText("Installation failed.\n" + jarDir + System.getProperty("file.separator") + "\n" + tgzURL
            + newVersion + ".tar.gz");
        info.showAndWait();
        return;
      }
    });

  }

  private static boolean latestIsNewer(String latestVersion) throws URISyntaxException, IOException {
    String dir = new File(UIVariableRepresentation.class.getProtectionDomain().getCodeSource().getLocation().toURI())
        .getParent();
    currentVersion = Files.readString(Paths.get(dir, "version.txt")).strip();
    String[] latestVersionParts = latestVersion.split("\\.");
    String[] currentVersionParts = currentVersion.split("\\.");

    int latestVersionNum = Integer.parseInt(latestVersionParts[0]) * 10000
        + Integer.parseInt(latestVersionParts[1]) * 100 + Integer.parseInt(latestVersionParts[2]);
    int currentVersionNum = Integer.parseInt(currentVersionParts[0]) * 10000
        + Integer.parseInt(currentVersionParts[1]) * 100 + Integer.parseInt(currentVersionParts[2]);

    return latestVersionNum > currentVersionNum;
  }

  private static String getOS() {
    switch (System.getProperty("os.name")) {
      case "Mac OS X":
        return "MacOS";
      case "Linux":
        return "Linux";
      case "Windows 10":
        return "Windows";
      default:
        return "unsupportedOS";
    }
  }

  private static CValue createValue(CValue value, String newValue, String type, String endian) {
    try {
      value = factory.makeCValue(type).addValue(newValue, type).addEndian(endian);
    } catch (NumberFormatException e) {
      value.addValue("0");
      if (newValue.length() > 0 && (newValue.length() > 1 || newValue.charAt(0) != '-')) { // on an empty input
                                                                                           // don't display an error
        value.setFormatError();
        value.setErrorMessage("IRVis cannot express input in selected type", e.getLocalizedMessage());
      }
    }
    return value;
  }

  private static String getConvertedValue(CValue value, String typeS) {
    switch (typeS) {
      case CValue.CHAR:
        return Byte.toString(value.getByteValue());
      case CValue.UCHAR:
        return Integer.toString(value.getUnsignedByteValue());
      case CValue.SHORT:
        return Short.toString(value.getShortValue());
      case CValue.USHORT:
        return Integer.toString(value.getUnsignedShortValue());
      case CValue.INT:
        return Integer.toString(value.getIntValue());
      case CValue.UINT:
        return Long.toString(value.getUnsignedIntValue());
      case CValue.LONG:
        return Long.toString(value.getLongValue());
      case CValue.ULONG:
        return Long.toUnsignedString(value.getUnsignedLongValue());
    }
    return null; // should never reach here
  }

  private static void updateUI() {
    updateInProgress = true;
    txtBytesTop.setText(topValue.getHexValueWithError());

    String typeS = cboBottomType.getValue();

    bottomValue = factory.makeCValue(typeS).addValue(getConvertedValue(topValue, typeS), typeS)
        .addEndian(cboBottomEndianness.getValue());
    txtBytesBottom.setText(bottomValue.getHexValue());
    txtValueBottom.setText(bottomValue.toString());

    drawVisualization(canvas.getWidth(), canvas.getHeight(), gc, topValue.getHexValue(), txtValueTop.getText(),
        cboTopType.getValue());
    drawVisualization(canvasBottom.getWidth(), canvasBottom.getHeight(), gcBottom, txtBytesBottom.getText(),
        txtValueBottom.getText(), cboBottomType.getValue());
    updateInProgress = false;
  }

  private static int getIndexFromType(String type) {
    switch (type) {
      case CValue.CHAR:
        return 0;
      case CValue.UCHAR:
        return 1;
      case CValue.SHORT:
        return 2;
      case CValue.USHORT:
        return 3;
      case CValue.INT:
        return 4;
      case CValue.UINT:
        return 5;
      case CValue.LONG:
        return 6;
      case CValue.ULONG:
        return 7;
      default:
        assert false;
        return 8;
    }
  }

  private static String getMaxSizeByType(String type) {
    if (type.equals(CValue.CHAR))
      return UIUtils.architecture.getMaxCharLabel();
    else if (type.equals(CValue.UCHAR))
      return UIUtils.architecture.getMaxUnsignedCharLabel();
    else if (type.equals(CValue.SHORT))
      return UIUtils.architecture.getMaxShortLabel();
    else if (type.equals(CValue.USHORT))
      return UIUtils.architecture.getMaxUnsignedShortLabel();
    else if (type.equals(CValue.INT))
      return UIUtils.architecture.getMaxIntLabel();
    else if (type.equals(CValue.UINT))
      return UIUtils.architecture.getMaxUnsignedIntLabel();
    else if (type.equals(CValue.LONG))
      return UIUtils.architecture.getMaxLongLabel();
    else if (type.equals(CValue.ULONG))
      return UIUtils.architecture.getMaxUnsignedLongLabel();
    assert false;
    return "Unknown";
  }

  private static String getMinSizeByType(String type) {
    if (type.equals(CValue.CHAR))
      return UIUtils.architecture.getMinCharLabel();
    else if (type.equals(CValue.UCHAR))
      return UIUtils.architecture.getMinUnsignedCharLabel();
    else if (type.equals(CValue.SHORT))
      return UIUtils.architecture.getMinShortLabel();
    else if (type.equals(CValue.USHORT))
      return UIUtils.architecture.getMinUnsignedShortLabel();
    else if (type.equals(CValue.INT))
      return UIUtils.architecture.getMinIntLabel();
    else if (type.equals(CValue.UINT))
      return UIUtils.architecture.getMinUnsignedIntLabel();
    else if (type.equals(CValue.LONG))
      return UIUtils.architecture.getMinLongLabel();
    else if (type.equals(CValue.ULONG))
      return UIUtils.architecture.getMinUnsignedLongLabel();
    assert false;
    return "Unknown";
  }

  private static void drawVisualization(double width, double height, GraphicsContext gc, String value, String decValue,
      String type) {
    BigDecimal min = BigDecimal.ZERO;
    BigDecimal max = BigDecimal.ZERO;
    // figure out where we are between min and max values
    switch (type) {
      case CValue.CHAR:
        min = new BigDecimal(UIUtils.architecture.getMinCharValue());
        max = new BigDecimal(UIUtils.architecture.getMaxCharValue());
        break;
      case CValue.SHORT:
        min = new BigDecimal(UIUtils.architecture.getMinShortValue());
        max = new BigDecimal(UIUtils.architecture.getMaxShortValue());
        break;
      case CValue.INT:
        min = new BigDecimal(UIUtils.architecture.getMinIntValue());
        max = new BigDecimal(UIUtils.architecture.getMaxIntValue());
        break;
      case CValue.LONG:
        min = new BigDecimal(UIUtils.architecture.getMinLongValue());
        max = new BigDecimal(UIUtils.architecture.getMaxLongValue());
        break;
      case CValue.UCHAR:
        min = new BigDecimal(UIUtils.architecture.getMinUnsignedCharValue());
        max = new BigDecimal(UIUtils.architecture.getMaxUnsignedCharValue());
        break;
      case CValue.USHORT:
        min = new BigDecimal(UIUtils.architecture.getMinUnsignedShortValue());
        max = new BigDecimal(UIUtils.architecture.getMaxUnsignedShortValue());
        break;
      case CValue.UINT:
        min = new BigDecimal(UIUtils.architecture.getMinUnsignedIntValue());
        max = new BigDecimal(UIUtils.architecture.getMaxUnsignedIntValue());
        break;
      case CValue.ULONG:
        min = new BigDecimal(UIUtils.architecture.getMinUnsignedLongValue());
        max = new BigDecimal(UIUtils.architecture.getMaxUnsignedLongValue());
        break;
      default:
        System.err.println("Internal Error: invalid type " + type);
    }
    BigDecimal val;
    try {
      val = new BigDecimal(decValue);
    } catch (NumberFormatException e) {
      val = BigDecimal.ZERO;
    }
    min = min.abs();
    max = max.add(min);
    val = val.add(min);

    BigDecimal propIntermediate = val.divide(max, 5, RoundingMode.HALF_UP);
    double proportion = propIntermediate.doubleValue();

    // draw
    gc.clearRect(0, 0, width, height);

    gc.setLineWidth(5);

    gc.setFont(Font.font("Sans", FontWeight.BOLD, 20));
    gc.setTextAlign(TextAlignment.LEFT);

    /*
     * if (!type.contains("unsigned")) { gc.setFill(Color.rgb(255, 85, 0));
     * gc.fillText("Negative", 5, 15);
     * 
     * gc.setFill(Color.rgb(30, 180, 30)); gc.fillText("Positive", 5, 35); }
     * 
     * gc.setFill(Color.BLUE); gc.fillText("Current value", 5, 55); /*
     */

    if (!type.contains("unsigned"))
      gc.setStroke(Color.rgb(235, 85, 0));
    else
      gc.setStroke(Color.rgb(30, 180, 30));
    gc.strokeLine(0, height / 2.0, width / 2.0, height / 2.0);
    gc.setStroke(Color.rgb(30, 180, 30));
    gc.strokeLine(width / 2.0, height / 2.0, width, height / 2.0);

    gc.setStroke(Color.BLUE);
    gc.setFill(Color.BLUE);
    gc.setTextAlign(TextAlignment.CENTER);
    if (proportion > 0.9)
      gc.setTextAlign(TextAlignment.RIGHT);
    else if (proportion < 0.1)
      gc.setTextAlign(TextAlignment.LEFT);
    gc.fillText("0x" + value, width * proportion, height / 2.0 - 10);

    gc.setStroke(Color.rgb(255, 85, 0));
    gc.setFill(Color.rgb(255, 85, 0));
    gc.setTextAlign(TextAlignment.LEFT);
    gc.fillText(getMinSizeByType(type), 0, height / 2.0 + 20);
    gc.setTextAlign(TextAlignment.RIGHT);
    if (!type.contains("unsigned"))
      gc.fillText("-1", width / 2.0 - 10, height / 2.0 + 20);

    gc.setStroke(Color.rgb(30, 180, 30));
    gc.setFill(Color.rgb(30, 180, 30));
    gc.setTextAlign(TextAlignment.RIGHT);
    gc.fillText(getMaxSizeByType(type), width, height / 2.0 + 20);
    gc.setTextAlign(TextAlignment.LEFT);
    if (!type.contains("unsigned"))
      gc.fillText("0", width / 2.0 + 10, height / 2.0 + 20);

    gc.setFill(Color.BLUE);
    gc.setStroke(Color.BLUE);
    gc.strokeLine(width * proportion - 10, height / 2.0, width * proportion + 10, height / 2.0);
  }

  /**
   * Code modified from https://dzone.com/articles/programmatically-restart-java`
   * since that version did not work. Sun property pointing the main class and its
   * arguments. Might not be defined on non Hotspot VM implementations.
   */
  private static final String SUN_JAVA_COMMAND = "sun.java.command";

  /**
   * Restart the current Java application
   * 
   * @param runBeforeRestart some custom code to be run before restarting
   * @throws IOException
   */
  private static void restartApplication(/* Runnable runBeforeRestart */) throws IOException {
    try {
      // java binary
      String java = System.getProperty("java.home") + "/bin/java";
      // vm arguments
      List<String> vmArguments = ManagementFactory.getRuntimeMXBean().getInputArguments();
      String vmArgsOneLine = new String();
      for (String arg : vmArguments) {
        // if it's the agent argument : we ignore it otherwise the
        // address of the old application and the new one will be in conflict
        if (!arg.contains("-agentlib")) {
          vmArgsOneLine += (arg + " ");
        }
      }
      // init the command to execute, add the vm args
      String cmd = java + " " + vmArgsOneLine + " ";

      // program main and program arguments
      String[] mainCommand = System.getProperty(SUN_JAVA_COMMAND).split(" ");
      // program main is a jar
      cmd += "-jar " + new File(mainCommand[0]).getAbsolutePath();
      // finally add program arguments
      for (int i = 1; i < mainCommand.length; i++) {
        cmd += (" " + (mainCommand[i]));
      }
      // execute the command in a shutdown hook, to be sure that all the
      // resources have been disposed before restarting the application

      final String command = cmd;
      Runtime.getRuntime().addShutdownHook(new Thread() {
        @Override
        public void run() {
          try {
            Runtime.getRuntime().exec(command);
          } catch (IOException e) {
            Alert info = new Alert(AlertType.INFORMATION);
            info.setTitle("Download irvis Dialog");
            info.setHeaderText("Error restarting irvis");
            info.setContentText("Restart manually.");
            info.showAndWait();
            return;
          }
        }
      });
      // execute some custom code before restarting
      // if (runBeforeRestart != null) {
      // runBeforeRestart.run();
      // }
      // exit
      System.exit(0);
    } catch (Exception e) {
      Alert info = new Alert(AlertType.INFORMATION);
      info.setTitle("Download irvis Dialog");
      info.setHeaderText("Error restarting irvis");
      info.setContentText("Restart manually.");
      info.showAndWait();
      return;
    }
  }
}
