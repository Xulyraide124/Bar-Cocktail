module com.loveinabottle.barcocktail {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.loveinabottle.barcocktail to javafx.fxml;
    opens com.loveinabottle.barcocktail.model to javafx.fxml;
    exports com.loveinabottle.barcocktail;
    exports com.loveinabottle.barcocktail.model;
}