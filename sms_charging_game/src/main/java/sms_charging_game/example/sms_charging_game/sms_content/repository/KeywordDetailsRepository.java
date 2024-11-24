package sms_charging_game.example.sms_charging_game.sms_content.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sms_charging_game.example.sms_charging_game.sms_content.model.KeywordDetails;

import java.util.List;

public interface KeywordDetailsRepository extends JpaRepository<KeywordDetails, Long> {

    @Query( "select kd.keyword from KeywordDetails kd where kd.keyword in ?1 " )
    List<String> getAllValidKeywordsFromKeywords( List<String> keywords );
}
