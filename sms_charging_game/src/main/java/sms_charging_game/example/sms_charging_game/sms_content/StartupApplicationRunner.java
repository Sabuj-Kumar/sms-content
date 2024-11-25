package sms_charging_game.example.sms_charging_game.sms_content;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import sms_charging_game.example.sms_charging_game.sms_content.sevice.ContentService;

@Component
public class StartupApplicationRunner implements ApplicationRunner {

    @Autowired
    private ContentService contentService;

    @Override
    public void run( ApplicationArguments args ) throws Exception {
        contentService.pingRequest();
    }
}
