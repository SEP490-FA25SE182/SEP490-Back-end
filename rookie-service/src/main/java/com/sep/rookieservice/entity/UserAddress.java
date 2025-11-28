package com.sep.rookieservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sep.rookieservice.enums.IsActived;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.Nationalized;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user_addresses")
public class UserAddress implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_address_id", length = 50)
    private String userAddressId;

    @NotNull
    @Size(max = 100)
    @Nationalized
    @Column(name = "address_infor", length = 100, nullable = false)
    private String addressInfor;

    @Size(max = 10)
    @Column(name = "phone_number", length = 10)
    private String phoneNumber;

    @Size(max = 50)
    @Nationalized
    @Column(name = "full_name", length = 50)
    private String fullName;

    @Size(max = 10)
    @Nationalized
    @Column(name = "type", length = 10)
    private String type;

    @NotNull
    @Column(name = "user_id", length = 50)
    private String userId;

    @Column(name = "is_default")
    private boolean isDefault;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at")
    private Instant updatedAt = Instant.now();

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "is_actived", nullable = false, length = 10)
    private IsActived isActived = IsActived.ACTIVE;

    @Column(name = "province_id", length = 20)
    private String provinceId;

    @Column(name = "district_id", length = 20)
    private String districtId;

    @Column(name = "ward_code", length = 20)
    private String wardCode;

    // RELATIONSHIPS
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    private User user;

    @JsonIgnore
    @OneToMany(mappedBy = "userAddress", fetch = FetchType.LAZY)
    private List<Order> orders;
}
