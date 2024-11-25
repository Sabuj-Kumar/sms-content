package sms_charging_game.example.sms_charging_game.sms_content.sevice;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import sms_charging_game.example.sms_charging_game.sms_content.model.Index;
import sms_charging_game.example.sms_charging_game.sms_content.model.KeywordDetails;
import sms_charging_game.example.sms_charging_game.sms_content.repository.KeywordDetailsRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor( onConstructor_ = {@Autowired} )
public class KeyDetailsService {

    private final KeywordDetailsRepository keywordDetailsRepository;
    private final TransactionTemplate transactionTemplate;

    public Boolean checkKeyValid( String keyword ) {
        return transactionTemplate.execute( status -> keywordDetailsRepository.existsByKeyword( keyword ) );
    }

    public void saveKeyDetails( Index index ) {
        transactionTemplate.execute( status -> {
            KeywordDetails keywordDetails = new KeywordDetails();
            keywordDetails.setKeyword( index.getKeyword() );
            keywordDetails.setCreatedAt( LocalDateTime.now() );
            keywordDetails.setUpdatedAt( LocalDateTime.now() );

            keywordDetailsRepository.save( keywordDetails );
            return null;
        } );
    }
}
