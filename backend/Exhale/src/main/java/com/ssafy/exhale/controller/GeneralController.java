package com.ssafy.exhale.controller;

import com.ssafy.exhale.dto.logicDto.AuthenticationDto;
import com.ssafy.exhale.dto.logicDto.CustomUserDetails;
import com.ssafy.exhale.dto.requestDto.EmailRequest;
import com.ssafy.exhale.dto.requestDto.MemberRequest;
import com.ssafy.exhale.dto.responseDto.MemberResponse;
import com.ssafy.exhale.dto.responseDto.TokenInfo;
import com.ssafy.exhale.service.AuthenticationService;
import com.ssafy.exhale.service.MemberService;
import com.ssafy.exhale.util.JWTUtil;
import com.ssafy.exhale.util.TokenPayloadUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/general")
public class GeneralController {
    private final MemberService memberService;
    private final TokenPayloadUtil tokenPayloadUtil;
    private final AuthenticationService authenticationService;

    @PostMapping("/join")
    public void join(@RequestBody MemberRequest memberRequest){
        memberService.join(memberRequest.toDto());
        // todo: 회원가입 응답 구현
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(){

        long memberId = tokenPayloadUtil.getMemberId();
        String nickname = tokenPayloadUtil.getNickname();
        String loginId = tokenPayloadUtil.getLoginId();

        if(memberService.checkWithdraw(memberId)){
            // todo 탈퇴 회원 예외 처리
            return ResponseEntity.ok("탈퇴 회원");
        }

        String jwt = tokenPayloadUtil.createJWT();
        String refreshToken = tokenPayloadUtil.createRefreshToken();

        AuthenticationDto authenticationDto = AuthenticationDto.of();
        authenticationDto.setMemberId(memberId);
        authenticationDto.setRefreshValue(refreshToken);
        authenticationService.saveRefreshValue(authenticationDto);

        TokenInfo tokeninfo = new TokenInfo("Bearer " + jwt,"Bearer " + refreshToken);
        MemberResponse memberResponse = new MemberResponse(memberId,nickname,loginId);

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("token",tokeninfo);
        responseBody.put("member",memberResponse);


        return ResponseEntity.ok(responseBody);
        // todo : 예외 처리
    }

    @PostMapping("/id")
    public ResponseEntity<?> checkLoginId(@RequestBody MemberRequest memberRequest){
        if(memberService.checkLoginId(memberRequest.getLoginId()))return ResponseEntity.ok("{result: false}");
        return ResponseEntity.status(200).body("{result: true}");
    }

    @PostMapping("/email")
    public ResponseEntity<?> checkEmail(@RequestBody EmailRequest emailRequest){
        if(!memberService.checkEmail(emailRequest)) return ResponseEntity.status(200).body("이메일 사용 가능");
        return ResponseEntity.status(400).body("이미 존재하는 이메일");
    }
}
