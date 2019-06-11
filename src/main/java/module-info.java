module AdrenalinaGame {

    requires javafx.base;
    requires javafx.controls;
    requires json.simple;
    requires java.rmi;

    exports it.polimi.ingsw.custom_exceptions;
    exports it.polimi.ingsw.server.model;
    exports it.polimi.ingsw.server.controller;
    exports it.polimi.ingsw.client.controller;
    exports it.polimi.ingsw.client.model;
    exports it.polimi.ingsw.client.view;
    exports it.polimi.ingsw.launcher;

}