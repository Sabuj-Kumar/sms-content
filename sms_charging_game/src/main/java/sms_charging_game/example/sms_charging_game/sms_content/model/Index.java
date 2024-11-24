package sms_charging_game.example.sms_charging_game.sms_content.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.experimental.Accessors;
import jakarta.persistence.*;
import sms_charging_game.example.sms_charging_game.sms_content.enums.OperatorType;
import sms_charging_game.example.sms_charging_game.sms_content.enums.Status;

import java.util.Objects;

@Data
@Entity
@Accessors( chain = true )
@Table( name = "index" )
public class Index extends AuditableEntity {

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    private Long id;

    @Column( name = "transaction_id", unique = true )
    private String transactionId;

    @Column( name = "short_code" )
    private Long shortCode;

    @Column( name = "msisdn" )
    private String msisdn;

    @Column( name = "keyword" )
    private String keyword;

    @Column( name = "game_name" )
    private String gameName;

    @Column( name = "sms" )
    private String sms;

    @Column( name = "operator" )
    private OperatorType operatorType;

    @Column( name = "status" )
    private Status status;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Index that = (Index) o;
        return Objects.equals( id, that.id );
    }

    @Override
    public int hashCode() {
        if ( id == null )
            return System.identityHashCode(this);
        return Objects.hash( id );
    }
}
