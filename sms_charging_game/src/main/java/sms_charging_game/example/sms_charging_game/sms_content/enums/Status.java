package sms_charging_game.example.sms_charging_game.sms_content.enums;

public enum Status {
    S( 0 ),
    N( 1 ),
    F( 2 );

    private Integer value;

    Status( Integer value ) {
        this.value = value;
    }
}
