package sms_charging_game.example.sms_charging_game.sms_content.model;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Data
@MappedSuperclass
@EntityListeners( AuditingEntityListener.class )
public class AuditableEntity {
    @CreatedDate
    @Column( name = "created_at" )
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column( name = "updated_at" )
    private LocalDateTime updatedAt;

    @Column( name = "is_deleted" )
    private Boolean isDeleted = false;
}
