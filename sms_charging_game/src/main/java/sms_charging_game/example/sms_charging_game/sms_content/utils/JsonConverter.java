package sms_charging_game.example.sms_charging_game.sms_content.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sms_charging_game.example.sms_charging_game.sms_content.request.UnlockRequest;
import sms_charging_game.example.sms_charging_game.sms_content.response.ContentDTOResponse;
import sms_charging_game.example.sms_charging_game.sms_content.response.ContentWrapperResponse;
import sms_charging_game.example.sms_charging_game.sms_content.response.ErrorResponse;
import sms_charging_game.example.sms_charging_game.sms_content.response.UnlockResponse;

import java.util.List;

@Service
@Data
@RequiredArgsConstructor( onConstructor_ = {@Autowired} )
public class JsonConverter {

    private final ObjectMapper objectMapper;

    public String createJsonRequest( Object unlockRequest ) {

        try {
            return objectMapper.writeValueAsString(unlockRequest);
        } catch ( JsonProcessingException e ) {
            System.err.println( "Error creating JSON request: " + e.getMessage() );
            return "{}";
        }
    }

    public UnlockResponse convertToUnlockResponse( String unlockResponseJson ) throws JsonProcessingException {

        return objectMapper.readValue( unlockResponseJson, UnlockResponse.class );
    }

    public ContentWrapperResponse convertToListOfContentDTO( String contentJson ) throws JsonProcessingException {

        return objectMapper.readValue( contentJson, ContentWrapperResponse.class );
    }

    public ErrorResponse convertToErrorResponse( String contentJson ) throws JsonProcessingException {

        return objectMapper.readValue( contentJson, ErrorResponse.class );
    }
}
