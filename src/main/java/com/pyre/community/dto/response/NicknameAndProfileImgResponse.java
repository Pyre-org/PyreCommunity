package com.pyre.community.dto.response;

public record NicknameAndProfileImgResponse(
        String nickname,
        String profilePictureUrl
) {
    public static NicknameAndProfileImgResponse makeDto(String nickname, String profilePictureUrl) {
        return new NicknameAndProfileImgResponse(nickname, profilePictureUrl);
    }
}
