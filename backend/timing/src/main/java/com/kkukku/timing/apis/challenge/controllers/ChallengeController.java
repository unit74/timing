package com.kkukku.timing.apis.challenge.controllers;


import com.kkukku.timing.apis.challenge.requests.ChallengeCreateRequest;
import com.kkukku.timing.apis.challenge.requests.ChallengeRelayRequest;
import com.kkukku.timing.apis.challenge.responses.ChallengePolygonResponse;
import com.kkukku.timing.apis.challenge.responses.ChallengeResponse;
import com.kkukku.timing.apis.challenge.services.ChallengeService;
import com.kkukku.timing.response.ApiResponseUtil;
import com.kkukku.timing.security.utils.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/challenges")
@Tag(name = "2. Challenge", description = "Challenge API")
@RequiredArgsConstructor
public class ChallengeController {

    private final ChallengeService challengeService;

    @Operation(summary = "본인의 Challenge 생성", tags = {"2. Challenge"},
        description = "Challenge가 생성 && 새로운 hashTag 생성 && 연관관계 정보 생성 ")
    @PostMapping(value = "")
    public ResponseEntity<Void> createChallenge(
        @Validated @RequestBody ChallengeCreateRequest challengeCreateRequest) {
        Integer memberId = SecurityUtil.getLoggedInMemberPrimaryKey();

        challengeService.createChallengeProcedure(memberId, challengeCreateRequest);

        return ApiResponseUtil.success();
    }

    @Operation(summary = "본인의 Challenge 목록 가져오기", tags = {"2. Challenge"},
        description = "Main, Mypage에 사용될 본인 Challenge 목록들입니다. ")
    @GetMapping(value = "")
    public ResponseEntity<ChallengeResponse> getChallenge() {
        Integer memberId = SecurityUtil.getLoggedInMemberPrimaryKey();

        ChallengeResponse challengeResponse = challengeService.getChallenge(memberId);

        return ApiResponseUtil.success(challengeResponse);
    }

    @Operation(summary = "본인의 특정 Challenge 삭제하기", tags = {"2. Challenge"},
        description = "본인의 특정 Challenge를 삭제하며, 관련 Snapshot들도 삭제합니다.  ")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteChallenge(@PathVariable Long id) {

        Integer memberId = SecurityUtil.getLoggedInMemberPrimaryKey();

        challengeService.deleteChallenge(memberId, id);

        return ApiResponseUtil.success();
    }

    @Operation(summary = "본인의 특정 Challenge 기간 연장하기", tags = {"2. Challenge"},
        description = "본인의 특정 Challenge 완료 후, 21일을 연장할 수 있습니다. ")
    @PatchMapping(value = "/{id}/extension")
    public ResponseEntity<Void> extendChallenge(@PathVariable Long id) {

        Integer memberId = SecurityUtil.getLoggedInMemberPrimaryKey();

        challengeService.extendChallenge(memberId, id);

        return ApiResponseUtil.success();
    }

    @Operation(summary = "타 멤버의 특정 Challenge 이어하기", tags = {"2. Challenge"},
        description = "타 회원의 Feed 정보를 이어서 본인의 Challenge로 생성합니다. HastTag 정보가 연동됩니다. GoalContent 정보는 연동되지 않습니다.(별도 작성) ")
    @PostMapping(value = "/{id}/relay")
    public ResponseEntity<Void> relayChallenge(@PathVariable Long id,
        @Valid @RequestBody ChallengeRelayRequest request) {

        Integer memberId = SecurityUtil.getLoggedInMemberPrimaryKey();

        challengeService.relayChallenge(memberId, id, request);

        return ApiResponseUtil.success();
    }

    @Operation(summary = "특정 Challenge의 SnapShot 촬영을 위한 Polygon 얻기", tags = {
        "2. Challenge"},
        description = "SnapShot 촬영 시 가이드 윤곽선을 그리기 위한 Polygon을 String 형태로 받아옵니다. ")
    @GetMapping(value = "/{id}/polygon")
    public ResponseEntity<ChallengePolygonResponse> getSnapshotByChallenge(@PathVariable Long id) {

        Integer memberId = SecurityUtil.getLoggedInMemberPrimaryKey();

        ChallengePolygonResponse challengePolygonResponse = challengeService.getPolygonByChallenge(
            memberId, id);

        return ApiResponseUtil.success(challengePolygonResponse);
    }

    @Operation(summary = "특정 Challenge의 Polygon, Object 사진 저장", tags = {
        "2. Challenge"},
        description = "특정 Challenge의 Snapshot 최초 등록시, 사진의 객체 최종 확정을 통해 Polygon, Object가 저장됩니다")
    @PostMapping(value = "/{id}/objects", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Void> savePolygonAndObject(@PathVariable Long id,
        @RequestPart MultipartFile polygon,
        @RequestPart MultipartFile object) {

        Integer memberId = SecurityUtil.getLoggedInMemberPrimaryKey();
        challengeService.saveObjectAndPolygon(memberId, id, polygon, object);

        return ApiResponseUtil.success();
    }

    // 이하로 Python Proxy APIs
    @Operation(summary = "특정 Challenge의 Snapshot 추가(미완)", tags = {"2. Challenge"},
        description = "특정 Challenge의 Snapshot 추가 시, 객체 유사도(현재 미연결) 이후 Snapshot이 저장됩니다. 현재는 수행시 바로 Upload(S3저장, DB 업데이트) 됩니다. ")
    @PostMapping(value = "/{id}/snapshots", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Void> setSnapshot(
        @PathVariable Long id, @RequestPart MultipartFile snapshot) {

        Integer memberId = SecurityUtil.getLoggedInMemberPrimaryKey();
        challengeService.setSnapshotProcedure(memberId, id, snapshot);

        return ApiResponseUtil.success();
    }

    @Operation(summary = "특정 Challenge의 최초 Snapshot 객체 탐지 요청(미완)", tags = {
        "2. Challenge"},
        description = "특정 Challenge의 최초 Snapshot 추가 시, 객체 탐지를 요청(미완) 합니다. 객체가 없을 경우 400에러가 뜹니다. (현재는 무조건 200) ")
    @PostMapping(value = "/{id}/snapshots/objects/detection", consumes = {
        MediaType.MULTIPART_FORM_DATA_VALUE
    })
    public ResponseEntity<byte[]> getObjectInSnapshot(@PathVariable Long id,
        @RequestPart MultipartFile snapshot) {

        byte[] objectsImage = challengeService.getDetectedObject(snapshot);

        HttpHeaders headers = new HttpHeaders();
        headers.setCacheControl(MediaType.IMAGE_PNG_VALUE);

        return ApiResponseUtil.success(headers, objectsImage);
    }


}
