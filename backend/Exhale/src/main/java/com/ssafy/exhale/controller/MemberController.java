package com.ssafy.exhale.controller;

import com.ssafy.exhale.dto.logicDto.CustomUserDetails;
import com.ssafy.exhale.dto.requestDto.MemberRequest;
import com.ssafy.exhale.dto.responseDto.MemberResponse;
import com.ssafy.exhale.dto.responseDto.TokenInfo;
import com.ssafy.exhale.service.MemberService;
import com.ssafy.exhale.util.TokenPayloadUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final TokenPayloadUtil tokenPayloadUtil;

    @PostMapping("/refresh")
    public ResponseEntity<?> getRefreshToken(HttpServletRequest request) {
        System.out.println("refresh");

        // DB와 토큰 비교
        String token_value = request.getHeader("Authorization").split(" ")[1];
        int memberId = tokenPayloadUtil.getMemberId();
        Map<String, Object> responseBody = new HashMap<>();

        if (memberService.compareRefreshToken(memberId, token_value)) {
            // 새로운 토큰 발행
            String jwt = tokenPayloadUtil.createJWT();
            String refresh_token = tokenPayloadUtil.createRefreshToken();
            memberService.saveRefreshValue(memberId, refresh_token);

            TokenInfo tokeninfo = new TokenInfo(jwt, refresh_token);

            responseBody.put("token", tokeninfo);

        } else {
            return ResponseEntity.status(401).body("Unauthorized: Invalid token");
        }
        return ResponseEntity.ok(responseBody);
    }

    @PostMapping("/check-password")
    public ResponseEntity<?> checkPassword(@RequestBody MemberRequest memberRequest){
        if(memberService.checkPassword(tokenPayloadUtil.getMemberId(),memberRequest.getPassword()))return ResponseEntity.ok("{result: true}");
        return ResponseEntity.status(200).body("{result: false}");
    }
}