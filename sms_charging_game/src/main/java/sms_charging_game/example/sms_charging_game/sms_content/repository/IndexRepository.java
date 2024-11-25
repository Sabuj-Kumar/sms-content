package sms_charging_game.example.sms_charging_game.sms_content.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sms_charging_game.example.sms_charging_game.sms_content.model.Index;

public interface IndexRepository extends JpaRepository<Index, Long> {
}
