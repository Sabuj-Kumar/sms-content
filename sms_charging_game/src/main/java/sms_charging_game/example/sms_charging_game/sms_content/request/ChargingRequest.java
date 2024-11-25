package sms_charging_game.example.sms_charging_game.sms_content.request;

import lombok.Data;

@Data
public class ChargingRequest {
    private String transactionId;
    private String operator;
    private String shortCode;
    private String msisdn;
    private String chargeCode;
}
