package sms_charging_game.example.sms_charging_game.sms_content.response;

import lombok.Data;

@Data
public class ApiResponse {

    private boolean success;
    private String message;

    public ApiResponse( String message ) {
        this.success = true;
        this.message = message;
    }
}
