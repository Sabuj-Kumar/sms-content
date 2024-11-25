package sms_charging_game.example.sms_charging_game.sms_content.response;

import lombok.Data;

@Data
public class ErrorResponse {

    private Integer statusCode;
    private String message;
}
