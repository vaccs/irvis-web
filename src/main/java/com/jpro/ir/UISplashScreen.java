package com.jpro.ir;

import javafx.application.Application;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.stage.*;

import com.jpro.webapi.JProApplication;

import org.vaccs.ir.value.CValue;

public class UISplashScreen extends JProApplication {

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage window) {
    window.initStyle(StageStyle.UNDECORATED);
    window.setTitle("VACCS Integer Representation");

    UIVariableRepresentation varRep = new UIVariableRepresentation();
    Scene scene = varRep.createDisplay(getWebAPI(), window, CValue.INT, "0");

    window.setScene(scene);
    window.show();
  }

}
