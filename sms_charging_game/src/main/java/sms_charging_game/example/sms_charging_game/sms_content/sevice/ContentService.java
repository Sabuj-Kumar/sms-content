package sms_charging_game.example.sms_charging_game.sms_content.sevice;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import sms_charging_game.example.sms_charging_game.sms_content.enums.OperatorType;
import sms_charging_game.example.sms_charging_game.sms_content.enums.Status;
import sms_charging_game.example.sms_charging_game.sms_content.model.Index;
import sms_charging_game.example.sms_charging_game.sms_content.repository.IndexRepository;
import sms_charging_game.example.sms_charging_game.sms_content.response.ContentDTOResponse;
import sms_charging_game.example.sms_charging_game.sms_content.response.ContentWrapperResponse;
import sms_charging_game.example.sms_charging_game.sms_content.utils.JsonConverter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor( onConstructor_ = {@Autowired})
public class ContentService {

    private final IndexRepository indexRepository;
    private final processExecutorService processExecutorService;
    private final KeyDetailsService keyDetailsService;
    private final JsonConverter jsonConverter;

    private static final String CONTENT_URL = "http://demo.webmanza.com/a55dbz923ace647v/api/v1.0/services/content";
    private static final String PING_URL = "http://demo.webmanza.com/a55dbz923ace647v/api/v1.0/ping";

    private final TransactionTemplate transactionTemplate;
    private final Executor virtualThreadExecutor = Executors.newVirtualThreadPerTaskExecutor();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    @Transactional
    public void pingRequest() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri( URI.create( PING_URL ))
                .GET()
                .build();

        HttpResponse<String> response = client.send( request, HttpResponse.BodyHandlers.ofString() );
        if( response.statusCode() == 200 )
            retrieveAndSaveContent();
    }

    public void retrieveAndSaveContent() {
        scheduler.scheduleAtFixedRate(() -> {
            transactionTemplate.execute( status -> {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri( URI.create( CONTENT_URL ))
                        .GET()
                        .build();

                try {
                    HttpResponse<String> response = client.send( request, HttpResponse.BodyHandlers.ofString() );
                    if ( response.statusCode() == 200 ) {

                        String jsonResponse = response.body();
                        System.out.println( "Response received: " + jsonResponse );

                        ContentWrapperResponse contentWrapperResponse = jsonConverter.convertToListOfContentDTO( jsonResponse );
                        List<ContentDTOResponse> contentList = contentWrapperResponse.getContents();

                        List<Index> indexList = contentList.stream().
                                map( content -> {
                                    Index index = new Index();
                                    index.setTransactionId( content.getTransactionId() );
                                    index.setOperatorType( getOperator( content.getOperator() ) );
                                    index.setShortCode( Long.parseLong( content.getShortCode() ) );
                                    index.setMsisdn( content.getMsisdn() );
                                    index.setSms( content.getSms() );
                                    index.setStatus( Status.N );
                                    index.setCreatedAt( LocalDateTime.now() );
                                    index.setUpdatedAt( LocalDateTime.now() );

                                    setDataFromSms( index, content.getSms() );
                                    return index;
                                })
                                .toList();

                        executeVirtualThread( indexList );
                    }
                } catch (Exception e) {

                    System.err.println( "Error during data retrieval: " + e.getMessage() );
                }
                return null;
            });
        }, 0, 10, TimeUnit.SECONDS );
    }

    private void setDataFromSms( Index entity, String sms ) {
        String[] smsInfo = sms.split( " " );
        entity.setKeyword( smsInfo[ 0 ] );
        entity.setGameName( smsInfo[ 1 ] );
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

    private void executeVirtualThread( List<Index> indexList ) {

        for( Index index : indexList ) {
            try {
                indexRepository.save( index );
                if( keyDetailsService.checkKeyValid( index.getKeyword() ) )
                    virtualThreadExecutor.execute( processExecutorService.processUnlockCodesFromIndex( index ) );

                keyDetailsService.saveKeyDetails( index );

            } catch ( Exception e ) {
                System.out.println( "Duplicate content found. Skipping save." );
            }
        }
    }
}
