package sms_charging_game.example.sms_charging_game.sms_content.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sms_charging_game.example.sms_charging_game.sms_content.model.ChargeFailure;

public interface ChargeFailureRepository extends JpaRepository<ChargeFailure, Long> {
}
