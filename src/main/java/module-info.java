module org.openjfx.gradle.javafx.test {
    requires javafx.controls;
    requires javafx.fxml;
    requires jpro.webapi;
    requires java.management;

    requires com.jfoenix;

    exports com.jpro.hellojpro;
    exports com.jpro.eevis;
    exports org.vaccs.eevis.ast;
    exports org.vaccs.eevis.driver;
    exports org.vaccs.eevis.iloc;
    exports org.vaccs.eevis.parser;
    exports org.vaccs.eevis.util;
    exports org.vaccs.eevis.vaccsio;
    exports org.vaccs.eevis.value;
    exports org.vaccs.eevis.visitor;
}