package sms_charging_game.example.sms_charging_game.sms_content.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Objects;

@Data
@Entity
@Accessors( chain = true )
@Table( name = "charge_success" )
public class ChargeSuccess extends AuditableEntity {

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    private Long id;

    @Column( name = "sms_id" )
    private Long smsId;

    @Column( name = "short_code" )
    private Long shortCode;

    @Column( name = "transaction_id" )
    private String transactionId;

    @Column( name = "msisdn" )
    private String msisdn;

    @Column( name = "keyword" )
    private String keyword;

    @Column( name = "game_name" )
    private String gameName;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChargeSuccess that = (ChargeSuccess) o;
        return Objects.equals( id, that.id );
    }

    @Override
    public int hashCode() {
        if ( id == null )
            return System.identityHashCode(this);
        return Objects.hash( id );
    }
}
