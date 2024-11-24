package sms_charging_game.example.sms_charging_game.sms_content.sevice;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sms_charging_game.example.sms_charging_game.sms_content.model.Index;
import sms_charging_game.example.sms_charging_game.sms_content.repository.IndexRepository;
import sms_charging_game.example.sms_charging_game.sms_content.repository.KeywordDetailsRepository;
import sms_charging_game.example.sms_charging_game.sms_content.request.UnlockRequest;
import sms_charging_game.example.sms_charging_game.sms_content.response.UnlockResponse;
import sms_charging_game.example.sms_charging_game.sms_content.utils.JsonConverter;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor( onConstructor_ = {@Autowired} )
public class processExecutorService {

    private final IndexRepository indexRepository;
    private final KeywordDetailsRepository keywordDetailsRepository;
    private final JsonConverter jsonConverter;

    private static final String UNLOCK_URL = "http://demo.webmanza.com/a55dbz923ace647v/api/v1.0/services/unlockCode";

    public void processUnlockCodesFromIndex() {

        List<Index> indexList = indexRepository.getAllNewKeywords();
        List<String> keywords = indexList.stream().map( Index::getKeyword ).toList();
        List<String> validKeys = keywordDetailsRepository.getAllValidKeywordsFromKeywords( keywords );
        List<Long> successIndexIds = new ArrayList<>();
        List<Long> failsIndexIds = new ArrayList<>();

        indexList.stream()
                .filter( index -> validKeys.contains( index.getKeyword() ) )
                .forEach( index -> {

                    UnlockRequest unlockRequest = new UnlockRequest();
                    unlockRequest.setTransactionId( index.getTransactionId() );
                    unlockRequest.setOperator( index.getOperatorType().name() );
                    unlockRequest.setShortCode( index.getShortCode().toString() );
                    unlockRequest.setMsisdn( index.getMsisdn() );
                    unlockRequest.setKeyword( index.getKeyword() );
                    unlockRequest.setGameName( index.getGameName() );

                    retrieveAndProcessUnlockCodes( unlockRequest, successIndexIds, failsIndexIds, index.getId() );
                });

        indexRepository.updateForSuccess( successIndexIds );
        indexRepository.updateForFails( failsIndexIds );
    }

    public void retrieveAndProcessUnlockCodes( UnlockRequest unlockRequest, List<Long> successIds, List<Long> failsIds ,Long indexId ) {

        try {
            String jsonBody = jsonConverter.createJsonRequest( unlockRequest );

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri( URI.create( UNLOCK_URL ) )
                    .header("Content-Type", "application/json" )
                    .POST( HttpRequest.BodyPublishers.ofString( jsonBody ) )
                    .build();

            HttpResponse<String> response = client.send( request, HttpResponse.BodyHandlers.ofString() );

            if ( response.statusCode() == 200 ) {
                String jsonResponse = response.body();
                UnlockResponse unlockResponse = jsonConverter.convertToUnlockResponse( jsonResponse );

                if( unlockResponse.getUnlockCode() != null )
                    successIds.add( indexId );

                System.out.println( "Unlock code response: " + jsonResponse );

            } else {
                failsIds.add( indexId );
                System.err.println( "Failed to retrieve unlock code. Status code: " + response.statusCode() );
            }
        } catch ( Exception e ) {
            System.err.println( "Error during unlock request: " + e.getMessage() );
            e.printStackTrace();
        }
    }
}
