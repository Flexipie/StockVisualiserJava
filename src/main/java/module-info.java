module com.example.stockvisualiser {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires transitive javafx.graphics;
    requires transitive javafx.base;
    requires transitive java.sql;
    requires jbcrypt;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;

    opens com.example.stockvisualiser to javafx.fxml;
    opens com.example.stockvisualiser.controller to javafx.fxml;
    opens com.example.stockvisualiser.model to javafx.base;
    
    exports com.example.stockvisualiser;
    exports com.example.stockvisualiser.controller;
    exports com.example.stockvisualiser.model;
    exports com.example.stockvisualiser.database;
    exports com.example.stockvisualiser.service;
    exports com.example.stockvisualiser.util;
}