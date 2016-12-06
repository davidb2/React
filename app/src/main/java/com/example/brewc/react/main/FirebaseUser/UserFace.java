package com.example.brewc.react.main.FirebaseUser;

import com.microsoft.projectoxford.face.contract.Face;
import com.microsoft.projectoxford.face.contract.FaceLandmarks;

/**
 * Allows Face to have reflection
 */

public class UserFace extends Face {
    private UserUUID faceId;
    private UserFaceRectangle faceRectangle;
    private UserFaceLandmarks faceLandmarks;
    private UserFaceAttribute faceAttributes;

    public UserFace(Face face) {
        this.faceId =
                new UserUUID(
                        face.faceId.getLeastSignificantBits(),
                        face.faceId.getMostSignificantBits()
                );

        this.faceAttributes =
                new UserFaceAttribute(
                        face.faceAttributes.age,
                        face.faceAttributes.smile,
                        face.faceAttributes.gender,
                        new UserHeadPose(
                                face.faceAttributes.headPose.pitch,
                                face.faceAttributes.headPose.roll,
                                face.faceAttributes.headPose.yaw
                        ),
                        new UserFacialHair(
                                face.faceAttributes.facialHair.beard,
                                face.faceAttributes.facialHair.moustache,
                                face.faceAttributes.facialHair.sideburns
                        )
                );

        this.faceRectangle =
                new UserFaceRectangle(
                        face.faceRectangle.height,
                        face.faceRectangle.left,
                        face.faceRectangle.top,
                        face.faceRectangle.width
                );

        FaceLandmarks faceLandmarks = face.faceLandmarks;

        this.faceLandmarks =
                new UserFaceLandmarks(
                    new UserFeatureCoordinate(faceLandmarks.pupilLeft.x, faceLandmarks.pupilLeft.y),
                    new UserFeatureCoordinate(faceLandmarks.pupilRight.x, faceLandmarks.pupilRight.y),
                    new UserFeatureCoordinate(faceLandmarks.noseTip.x, faceLandmarks.noseTip.y),
                    new UserFeatureCoordinate(faceLandmarks.mouthLeft.x, faceLandmarks.mouthLeft.y),
                    new UserFeatureCoordinate(faceLandmarks.mouthRight.x, faceLandmarks.mouthRight.y),
                    new UserFeatureCoordinate(faceLandmarks.eyebrowLeftOuter.x, faceLandmarks.eyebrowLeftOuter.y),
                    new UserFeatureCoordinate(faceLandmarks.eyebrowLeftInner.x, faceLandmarks.eyebrowLeftInner.y),
                    new UserFeatureCoordinate(faceLandmarks.eyeLeftOuter.x, faceLandmarks.eyeLeftOuter.y),
                    new UserFeatureCoordinate(faceLandmarks.eyeLeftTop.x, faceLandmarks.eyeLeftTop.y),
                    new UserFeatureCoordinate(faceLandmarks.eyeLeftBottom.x, faceLandmarks.eyeLeftBottom.y),
                    new UserFeatureCoordinate(faceLandmarks.eyeLeftInner.x, faceLandmarks.eyeLeftInner.y),
                    new UserFeatureCoordinate(faceLandmarks.eyebrowRightInner.x, faceLandmarks.eyebrowRightInner.y),
                    new UserFeatureCoordinate(faceLandmarks.eyebrowRightOuter.x, faceLandmarks.eyebrowRightOuter.y),
                    new UserFeatureCoordinate(faceLandmarks.eyeRightInner.x, faceLandmarks.eyeRightInner.y),
                    new UserFeatureCoordinate(faceLandmarks.eyeRightTop.x, faceLandmarks.eyeRightTop.y),
                    new UserFeatureCoordinate(faceLandmarks.eyeRightBottom.x, faceLandmarks.eyeRightBottom.y),
                    new UserFeatureCoordinate(faceLandmarks.eyeRightOuter.x, faceLandmarks.eyeRightOuter.y),
                    new UserFeatureCoordinate(faceLandmarks.noseRootLeft.x, faceLandmarks.noseRootLeft.y),
                    new UserFeatureCoordinate(faceLandmarks.noseRootRight.x, faceLandmarks.noseRootRight.y),
                    new UserFeatureCoordinate(faceLandmarks.noseLeftAlarTop.x, faceLandmarks.noseLeftAlarTop.y),
                    new UserFeatureCoordinate(faceLandmarks.noseRightAlarTop.x, faceLandmarks.noseRightAlarTop.y),
                    new UserFeatureCoordinate(faceLandmarks.noseLeftAlarOutTip.x, faceLandmarks.noseLeftAlarOutTip.y),
                    new UserFeatureCoordinate(faceLandmarks.noseRightAlarOutTip.x, faceLandmarks.noseRightAlarOutTip.y),
                    new UserFeatureCoordinate(faceLandmarks.upperLipTop.x, faceLandmarks.upperLipTop.y),
                    new UserFeatureCoordinate(faceLandmarks.upperLipBottom.x, faceLandmarks.upperLipBottom.y),
                    new UserFeatureCoordinate(faceLandmarks.underLipTop.x, faceLandmarks.underLipTop.y),
                    new UserFeatureCoordinate(faceLandmarks.underLipBottom.x, faceLandmarks.underLipBottom.y)
                );
    }

    private UserFace() {}

    public UserUUID getFaceId() {
        return faceId;
    }

    public UserFaceRectangle getFaceRectangle() {
        return faceRectangle;
    }

    public UserFaceLandmarks getFaceLandmarks() {
        return faceLandmarks;
    }

    public UserFaceAttribute getFaceAttributes() {
        return faceAttributes;
    }
}
