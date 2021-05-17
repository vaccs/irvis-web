module org.openjfx.gradle.javafx.test {
    requires javafx.controls;
    requires javafx.fxml;
    requires jpro.webapi;
    requires java.management;

    requires com.jfoenix;

    exports com.jpro.ir;
    exports org.vaccs.ir.util;
    exports org.vaccs.ir.value;
}