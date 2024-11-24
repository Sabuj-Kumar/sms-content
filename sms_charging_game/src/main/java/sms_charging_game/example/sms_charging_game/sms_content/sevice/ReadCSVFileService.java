package sms_charging_game.example.sms_charging_game.sms_content.sevice;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sms_charging_game.example.sms_charging_game.sms_content.model.ChargeConfig;
import sms_charging_game.example.sms_charging_game.sms_content.repository.ChargeConfigRepository;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Service
@RequiredArgsConstructor( onConstructor_ = {@Autowired})
public class ReadCSVFileService {

    private final ChargeConfigRepository chargeConfigRepository;

    @Transactional
    public void updateChargeConfigFormCSV() throws IOException {

        String path = "C:/Users/Sabuj/OneDrive/Desktop/MoMagic/requirement-documentation/requirement-documentation/database_tables/charge_config.csv";
        try (BufferedReader br = new BufferedReader( new FileReader( path ) ) ) {
            String line;
            br.readLine();

            while ((line = br.readLine()) != null) {

                String[] fields = line.split(",");

                if (fields.length > 2 ) {
                    ChargeConfig chargeConfig = new ChargeConfig();
                    chargeConfig.setOperator( fields[ 0 ].trim() );
                    chargeConfig.setChargeCode( fields[ 1 ].trim() );
                    chargeConfig.setCreatedAt( LocalDateTime.now() );
                    chargeConfig.setUpdatedAt( LocalDateTime.now() );
                    chargeConfigRepository.save( chargeConfig );
                }
            }
        }
    }
}
