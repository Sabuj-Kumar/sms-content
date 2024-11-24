package sms_charging_game.example.sms_charging_game.sms_content.response;

import lombok.Data;

@Data
public class UnlockResponse {

    private Long statusCode;
    private String message;
    private String unlockCode;

    private String transactionId;
    private String operator;
    private String shortCode;
    private String msisdn;
    private String keyword;
    private String gameName;
}
