package sms_charging_game.example.sms_charging_game.sms_content.sevice;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import sms_charging_game.example.sms_charging_game.sms_content.enums.Status;
import sms_charging_game.example.sms_charging_game.sms_content.model.ChargeConfig;
import sms_charging_game.example.sms_charging_game.sms_content.model.ChargeFailure;
import sms_charging_game.example.sms_charging_game.sms_content.model.ChargeSuccess;
import sms_charging_game.example.sms_charging_game.sms_content.model.Index;
import sms_charging_game.example.sms_charging_game.sms_content.repository.ChargeConfigRepository;
import sms_charging_game.example.sms_charging_game.sms_content.repository.ChargeFailureRepository;
import sms_charging_game.example.sms_charging_game.sms_content.repository.ChargeSuccessRepository;
import sms_charging_game.example.sms_charging_game.sms_content.repository.IndexRepository;
import sms_charging_game.example.sms_charging_game.sms_content.request.ChargingRequest;
import sms_charging_game.example.sms_charging_game.sms_content.utils.JsonConverter;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor( onConstructor_ = {@Autowired} )
public class ChargeService {

    private final IndexRepository indexRepository;
    private final ChargeSuccessRepository chargeSuccessRepository;
    private final ChargeFailureRepository chargeFailureRepository;
    private final ChargeConfigRepository chargeConfigRepository;
    private final JsonConverter jsonConverter;
    private final TransactionTemplate transactionTemplate;

    private final String CHARGE_URL = "http://demo.webmanza.com/a55dbz923ace647v/api/v1.0/services/charge";

    public void requestToCharge( Index index ) {

        ChargeConfig chargeConfig = chargeConfigRepository.findByOperator( index.getOperatorType().name() );
        ChargingRequest chargingRequest = new ChargingRequest();

        chargingRequest.setTransactionId( index.getTransactionId() );
        chargingRequest.setShortCode( index.getShortCode().toString() );
        chargingRequest.setOperator( index.getOperatorType().name() );
        chargingRequest.setMsisdn( index.getMsisdn() );
        chargingRequest.setChargeCode( chargeConfig.getChargeCode() );

        charging( chargingRequest, index );
    }

    public void charging( ChargingRequest chargingRequest, Index index ) {

        transactionTemplate.execute( status -> {
            try {
                String jsonBody = jsonConverter.createJsonRequest( chargingRequest );

                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri( URI.create( CHARGE_URL ) )
                        .header("Content-Type", "application/json" )
                        .POST( HttpRequest.BodyPublishers.ofString( jsonBody ) )
                        .build();

                HttpResponse<String> response = client.send( request, HttpResponse.BodyHandlers.ofString() );
                if ( response.statusCode() == 200 )
                    saveSuccessCharge( index );
                else
                    saveFailCharge( index, (long) response.statusCode(), response.body() );

            } catch ( Exception e ) {
                e.printStackTrace();
            }
            return null;
        } );
    }

    public void saveSuccessCharge( Index index ) {

        ChargeSuccess chargeSuccess = new ChargeSuccess();

        chargeSuccess.setTransactionId( index.getTransactionId() );
        chargeSuccess.setMsisdn( index.getMsisdn() );
        chargeSuccess.setSmsId( index.getId() );
        chargeSuccess.setGameName( index.getGameName() );
        chargeSuccess.setKeyword( index.getKeyword() );
        chargeSuccess.setShortCode( index.getShortCode() );
        chargeSuccess.setOperatorType( index.getOperatorType() );
        chargeSuccess.setCreatedAt( LocalDateTime.now() );
        chargeSuccess.setUpdatedAt( LocalDateTime.now() );

        index.setStatus( Status.S );
        indexRepository.save( index );
        chargeSuccessRepository.save( chargeSuccess );
    }

    public void saveFailCharge( Index index, Long statusCode, String message ) {

        ChargeFailure chargeFailure = new ChargeFailure();

        chargeFailure.setTransactionId( index.getTransactionId() );
        chargeFailure.setMsisdn( index.getMsisdn() );
        chargeFailure.setSmsId( index.getId() );
        chargeFailure.setGameName( index.getGameName() );
        chargeFailure.setKeyword( index.getKeyword() );
        chargeFailure.setOperatorType( index.getOperatorType() );
        chargeFailure.setCreatedAt( LocalDateTime.now() );
        chargeFailure.setUpdatedAt( LocalDateTime.now() );

        chargeFailure.setShortCode( statusCode );
        chargeFailure.setMessage( message );

        index.setStatus( Status.F );
        indexRepository.save( index );
        chargeFailureRepository.save( chargeFailure );
    }
}
