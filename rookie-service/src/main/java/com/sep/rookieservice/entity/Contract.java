package com.sep.rookieservice.entity;

import com.sep.rookieservice.enums.ContractStatus;
import com.sep.rookieservice.enums.IsActived;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "contracts")
public class Contract {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "contract_id", length = 50)
    private String contractId;

    @Column(name = "contract_number", length = 100, unique = true)
    private String contractNumber;

    @Column(name = "title", length = 250)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "document_url", length = 1000)
    private String documentUrl;

    @Column(name = "start_date")
    private Instant startDate;

    @Column(name = "end_date")
    private Instant endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private ContractStatus status = ContractStatus.DRAFT;

    @Enumerated(EnumType.STRING)
    @Column(name = "is_actived", length = 10)
    private IsActived isActived = IsActived.ACTIVE;

    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at")
    private Instant updatedAt = Instant.now();
}
