package com.jpro.ir;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.math.*;
import java.util.List;

import com.jpro.webapi.WebAPI;

import javafx.beans.value.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.canvas.*;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.text.*;
import javafx.stage.*;
import org.vaccs.ir.util.ArchitectureType;
import org.vaccs.ir.value.CValue;
import org.vaccs.ir.value.CValueFactory;

public class UIVariableRepresentation {

  private Canvas canvas, canvasBottom;
  private GraphicsContext gc, gcBottom;

  public double fontSize = 1.0;
  private CValue topValue;
  private CValue bottomValue;
  private CValueFactory factory = new CValueFactory();
  private Scene scene;
  private boolean updateInProgress = false;

  private ComboBox<String> cboTopEndianness;
  private ComboBox<String> cboTopType;
  private TextField txtBytesTop;
  private TextField txtValueTop;

  private ComboBox<String> cboBottomEndianness;
  private ComboBox<String> cboBottomType;
  private TextField txtBytesBottom;
  private TextField txtValueBottom;

  public Scene createDisplay(WebAPI webapi, Stage window, String typeIn, String value) {

    window.setTitle("Integer Representations");
    window.initModality(Modality.APPLICATION_MODAL);

    topValue = factory.makeCValue(typeIn).addValue(value, typeIn);

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

    Button button = new Button("About");
    button.setOnAction(e -> {

      Label label = new Label(
          "Author: James Walker\nand Steve Carr\nThis work has been supported by the National Science Foundation under grants DUE-1245310,  DGE-1522883, DGE-1523017, IIS-1456763, and IIS-1455886.");
      label.setWrapText(true);
      label.setTextAlignment(TextAlignment.CENTER);

      Image image = new Image("file:assets/splash.png");
      ImageView imv = new ImageView();
      imv.setImage(image);

      VBox layout = new VBox();
      layout.setSpacing(50);
      layout.getChildren().addAll(imv, label);
      layout.setAlignment(Pos.CENTER);

      Scene aboutScene = new Scene(layout, 500, 500);
      final Stage aboutWindow = new Stage();
      aboutWindow.setScene(aboutScene);
      webapi.openStageAsPopup(aboutWindow);
    });

    grid.add(button, 0, 8, 1, 1);

    StackPane sp = new StackPane(grid);
    scene = new Scene(sp);
    return scene;
  }

  private String getOS() {
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

  private CValue createValue(CValue value, String newValue, String type, String endian) {
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

  private String getConvertedValue(CValue value, String typeS) {
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

  private void updateUI() {
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

  private int getIndexFromType(String type) {
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

  private String getMaxSizeByType(String type) {
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

  private String getMinSizeByType(String type) {
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

  private void drawVisualization(double width, double height, GraphicsContext gc, String value, String decValue,
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

}
