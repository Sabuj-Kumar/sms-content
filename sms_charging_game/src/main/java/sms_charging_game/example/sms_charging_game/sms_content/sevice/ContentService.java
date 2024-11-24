package sms_charging_game.example.sms_charging_game.sms_content.sevice;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sms_charging_game.example.sms_charging_game.sms_content.enums.OperatorType;
import sms_charging_game.example.sms_charging_game.sms_content.enums.Status;
import sms_charging_game.example.sms_charging_game.sms_content.model.Index;
import sms_charging_game.example.sms_charging_game.sms_content.model.KeywordDetails;
import sms_charging_game.example.sms_charging_game.sms_content.repository.IndexRepository;
import sms_charging_game.example.sms_charging_game.sms_content.repository.KeywordDetailsRepository;
import sms_charging_game.example.sms_charging_game.sms_content.response.ContentDTOResponse;
import sms_charging_game.example.sms_charging_game.sms_content.utils.JsonConverter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Data
@RequiredArgsConstructor( onConstructor_ = {@Autowired})
@Service
public class ContentService {

    private final IndexRepository indexRepository;
    private final KeywordDetailsRepository keywordDetailsRepository;
    private final processExecutorService processExecutorService;
    private final JsonConverter jsonConverter;

    private static final String CONTENT_URL = "http://demo.webmanza.com/a55dbz923ace647v/api/v1.0/services/content";

    private final Executor virtualThreadExecutor = Executors.newVirtualThreadPerTaskExecutor();
    private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public void retrieveAndSaveContent() {
        scheduler.scheduleAtFixedRate(() -> {
            virtualThreadExecutor.execute(() -> {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri( URI.create( CONTENT_URL ))
                        .GET()
                        .build();

                try {
                    HttpResponse<String> response = client.send( request, HttpResponse.BodyHandlers.ofString() );
                    if ( response.statusCode() == 200 ) {

                        String jsonResponse = response.body();
                        System.out.println( "Response received: " + jsonResponse);

                        List<ContentDTOResponse> contentList = jsonConverter.convertToListOfContentDTO( jsonResponse );
                        for( ContentDTOResponse content:  contentList ) {

                            Index entity = new Index();
                            entity.setTransactionId( content.getTransactionId() );
                            entity.setOperatorType( getOperator( content.getOperator() ) );
                            entity.setShortCode( Long.parseLong( content.getShortCode() ) );
                            entity.setMsisdn( content.getMsisdn() );
                            entity.setSms( content.getSms() );
                            entity.setStatus( Status.N );
                            entity.setCreatedAt( LocalDateTime.now() );
                            entity.setUpdatedAt( LocalDateTime.now() );

                            setDataFromSms( entity, content.getSms() );

                            try {
                                indexRepository.save( entity );
                            } catch ( Exception e ) {

                                System.out.println( "Duplicate content found. Skipping save." );
                            }
                        }
                    }
                    executeUnlockCodeService();

                } catch (Exception e) {
                    System.err.println("Error during data retrieval: " + e.getMessage());
                    e.printStackTrace();
                }
            });
        }, 0, 10, TimeUnit.SECONDS );
    }

    private void setDataFromSms( Index entity, String sms ) {

        String[] smsInfo = sms.split("," );
        entity.setKeyword( smsInfo[ 0 ] );
        entity.setGameName( smsInfo[ 1 ] );

        KeywordDetails keywordDetails = new KeywordDetails();
        keywordDetails.setKeyword( smsInfo[ 0 ] );
        keywordDetails.setCreatedAt( LocalDateTime.now() );
        keywordDetails.setUpdatedAt( LocalDateTime.now() );

        keywordDetailsRepository.save( keywordDetails );
    }

    private OperatorType getOperator( String operatorType ) {

        return switch ( operatorType ) {
            case "BANGLALINK" -> OperatorType.BANGLALINK;
            case "GRAMEENPHONE" -> OperatorType.GRAMEENPHONE;
            case "ROBI" -> OperatorType.ROBI;
            case "TELETALK" -> OperatorType.TELETALK;
            default -> OperatorType.AIRTEL;
        };
    }

    public void executeUnlockCodeService() {
        virtualThreadExecutor.execute( processExecutorService::processUnlockCodesFromIndex );
    }
}
