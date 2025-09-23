package com.sep.rookieservice.model;

import com.sep.rookieservice.enums.IsActived;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "userAddresses")
public class UserAddress implements Serializable {
    @Id
    @Size(max = 50)
    private String userAddressId;

    @NotNull
    @Size(max = 100)
    @Field("address_infor ")
    private String addressInfor;

    @CreatedDate
    @Field("created_at")
    private Instant createdAt;

    @CreatedDate
    @Field("updated_at")
    private Instant updatedAt;

    @NotNull
    @Field("is_actived")
    private IsActived isActived = IsActived.ACTIVE;
}
