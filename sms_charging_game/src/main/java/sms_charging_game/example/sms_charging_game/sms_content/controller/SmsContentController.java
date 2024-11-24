package sms_charging_game.example.sms_charging_game.sms_content.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sms_charging_game.example.sms_charging_game.sms_content.response.ApiResponse;
import sms_charging_game.example.sms_charging_game.sms_content.sevice.ReadCSVFileService;

import java.io.IOException;

@RestController
@RequestMapping( "/sms-content" )
@RequiredArgsConstructor( onConstructor_ = {@Autowired} )
public class SmsContentController {

    private final ReadCSVFileService readCSVFileService;

    @PostMapping( "/upload/config-data" )
    public ApiResponse addPointOfMeasurementVisual() throws IOException {
        readCSVFileService.updateChargeConfigFormCSV();
        return new ApiResponse( "New pom visual Added successfully" );
    }
}
