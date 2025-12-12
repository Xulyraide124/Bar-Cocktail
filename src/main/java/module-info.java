module com.loveinabottle.barcocktail {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.loveinabottle.barcocktail to javafx.fxml;
    exports com.loveinabottle.barcocktail;
}