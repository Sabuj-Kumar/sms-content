package sms_charging_game.example.sms_charging_game.sms_content.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Objects;

@Data
@Entity
@Accessors( chain = true )
@Table( name = "charge_config" )
public class ChargeConfig extends AuditableEntity {

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    private Long id;

    @Column( name = "operator" )
    private String operator;

    @Column( name = "charge_code" )
    private String chargeCode;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChargeConfig that = (ChargeConfig) o;
        return Objects.equals( id, that.id );
    }

    @Override
    public int hashCode() {
        if ( id == null )
            return System.identityHashCode(this);
        return Objects.hash( id );
    }
}
