package com.sep.rookieservice.model;

import com.sep.rookieservice.enums.IsActived;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "users")
public class User implements Serializable {
    @Id
    @Size(max = 50)
    private String userId;

    @NotNull
    @Size(max = 50)
    @Field("full_name")
    private String fullName;

    @Field("birth_date")
    private LocalDate birthDate;

    @Size(max = 10)
    @Field("gender")
    private String gender;

    @Indexed(unique = true)
    @Size(max = 254)
    private String email;

    @Size(max = 20)
    @Field("phone_number")
    private String phoneNumber;

    @Size(max = 1000)
    @Field("avatar_url")
    private String avatarUrl;

    @NotNull
    @Size(max = 10)
    @Field("role")
    private String role;

    @NotNull
    @Field("is_actived")
    private IsActived isActived = IsActived.ACTIVE;
}
