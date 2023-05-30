package me.nogari.nogari.api.response;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

/*
* API Response 결과의 반환 값을 관리
* */

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BaseResponse<T> {


    private T result;//API 응답 결과 response
    private int resultCode;//API 응답 결과 response
    private String resultMessage;//API 응답 결과 message

    @Builder
    public BaseResponse(T result, int resultCode, String resultMsg) {
        this.result = result;
        this.resultCode = resultCode;
        this.resultMessage = resultMsg;
    }

}



