package sms_charging_game.example.sms_charging_game.sms_content.sevice;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sms_charging_game.example.sms_charging_game.sms_content.model.Index;
import sms_charging_game.example.sms_charging_game.sms_content.model.KeywordDetails;
import sms_charging_game.example.sms_charging_game.sms_content.repository.KeywordDetailsRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor( onConstructor_ = {@Autowired} )
public class KeyDetailsService {

    private final KeywordDetailsRepository keywordDetailsRepository;

    @Transactional
    public Boolean checkKeyValid( String keyword ) {

        return keywordDetailsRepository.existsByKeyword( keyword );
    }

    @Transactional
    public void saveKeyDetails( Index index ) {

        KeywordDetails keywordDetails = new KeywordDetails();
        keywordDetails.setKeyword( index.getKeyword() );
        keywordDetails.setCreatedAt( LocalDateTime.now() );
        keywordDetails.setUpdatedAt( LocalDateTime.now() );

        keywordDetailsRepository.save( keywordDetails );
    }
}
