package sms_charging_game.example.sms_charging_game.sms_content.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import sms_charging_game.example.sms_charging_game.sms_content.model.Index;

import java.util.List;

public interface IndexRepository extends JpaRepository<Index, Long> {

    @Query( "select idx.keyword from Index idx where idx.status = 2" )
    List<Index> getAllNewKeywords();

    @Modifying
    @Query( "update from Index idx set idx.status = 0 where idx.id in ?1 " )
    void updateForSuccess( List<Long> indexIds );

    @Modifying
    @Query( "update from Index idx set idx.status = 2 where idx.id in ?1 " )
    void updateForFails( List<Long> indexIds );
}
