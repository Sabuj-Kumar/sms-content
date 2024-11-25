package sms_charging_game.example.sms_charging_game.sms_content.response;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors( chain = true )
public class ContentWrapperResponse {

    private int statusCode;
    private String message;
    private int contentCount;
    private List<ContentDTOResponse> contents;
}
