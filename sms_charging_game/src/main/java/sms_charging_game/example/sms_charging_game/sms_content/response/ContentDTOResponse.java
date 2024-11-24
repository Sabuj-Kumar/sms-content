package sms_charging_game.example.sms_charging_game.sms_content.response;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors( chain = true )
public class ContentDTOResponse {

    private String transactionId;
    private String operator;
    private String shortCode;
    private String msisdn;
    private String sms;
}
