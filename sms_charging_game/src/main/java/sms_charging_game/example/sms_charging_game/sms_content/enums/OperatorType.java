package sms_charging_game.example.sms_charging_game.sms_content.enums;

public enum OperatorType {
    BANGLALINK( 0L ),
    GRAMEENPHONE( 1L ),
    ROBI( 2L ),
    TELETALK( 3L ),
    AIRTEL( 4L );

    private Long value;

    OperatorType( Long value ) {
        this.value = value;
    }
}
