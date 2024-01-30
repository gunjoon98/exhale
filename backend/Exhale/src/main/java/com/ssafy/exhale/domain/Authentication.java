package com.ssafy.exhale.domain;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Getter
@Document(collection="authentication")
@NoArgsConstructor
@AllArgsConstructor
public class Authentication {
    @Id
    private String id;
    @Field("member_id")
    private Long memberId;
    @Field("refresh_value")
    private String refreshValue;
    @CreatedDate
    @Field("created_time")
    private LocalDateTime createdTime;
    @Field("expire_at")
    private Long expireAt;

    private Authentication(Long memberId, String refreshValue, LocalDateTime createdTime, Long expireAt) {
        this.memberId = memberId;
        this.refreshValue = refreshValue;
        this.createdTime = createdTime;
        this.expireAt = expireAt;
    }

    public static Authentication of(){
        return new Authentication();
    }

    public static Authentication of(Long memberId, String refreshValue, Long expireAt){
        return new Authentication(memberId,refreshValue,LocalDateTime.now(),expireAt);
    }

}