import com.pingan.ai.face.common.PaFaceConstants


object Tips {
    fun getDescription(id: Int): String {
        when (id) {
            //------- EnvironmentalTips
            PaFaceConstants.EnvironmentalTips.MULTI_FACE -> return "label_fr_detection_multi_face"
            PaFaceConstants.EnvironmentalTips.NO_FACE,
            PaFaceConstants.EnvironmentalTips.FACE_YAW_RIGHT,
            PaFaceConstants.EnvironmentalTips.FACE_YAW_LEFT,
            PaFaceConstants.EnvironmentalTips.FACE_ROLL_LEFT,
            PaFaceConstants.EnvironmentalTips.FACE_ROLL_RIGHT,
            PaFaceConstants.EnvironmentalTips.FACE_NO_CENTER,
            PaFaceConstants.EnvironmentalTips.RETURN_TO_CENTER -> return "label_fr_detection_need_face_center"

            PaFaceConstants.EnvironmentalTips.FACE_PITCH_UP -> return "label_fr_detection_face_pitch_up"
            PaFaceConstants.EnvironmentalTips.FACE_PITCH_DOWN -> return "label_fr_detection_face_pitch_down"
            PaFaceConstants.EnvironmentalTips.TOO_BRIGHT -> return "label_fr_detection_too_bright"
            PaFaceConstants.EnvironmentalTips.TOO_DARK -> return "label_fr_detection_too_dark"
            PaFaceConstants.EnvironmentalTips.TOO_FUZZY -> return "label_fr_detection_too_fuzzy"
            PaFaceConstants.EnvironmentalTips.TOO_CLOSE -> return "label_fr_detection_too_close"
            PaFaceConstants.EnvironmentalTips.TOO_FAR -> return "label_fr_detection_too_far"
            PaFaceConstants.EnvironmentalTips.COVER_MOUTH,
            PaFaceConstants.EnvironmentalTips.COVER_EYE,
            PaFaceConstants.EnvironmentalTips.COVER_FACE,
            PaFaceConstants.EnvironmentalTips.COVER_CHIN,
            PaFaceConstants.EnvironmentalTips.COVER_NOSE -> return "label_fr_detection_cover_face"

            PaFaceConstants.EnvironmentalTips.MOUTH_OPEN_ERROR -> return "label_fr_detection_mouth_open_error"
            PaFaceConstants.EnvironmentalTips.EYE_CLOSE_ERROR -> return "label_fr_detection_eye_close_error"
            PaFaceConstants.EnvironmentalTips.GRAVITY_POSE_ERROR -> return "label_fr_detection_gravity_pose_error"

            //------- MotionTypes
            PaFaceConstants.MotionType.NORMAL -> return "label_fr_liveness_face_straight"
            PaFaceConstants.MotionType.OPEN_MOUTH -> return "label_fr_liveness_open_mouth"
            PaFaceConstants.MotionType.BLINK_EYE -> return "label_fr_liveness_blink_eye"
            PaFaceConstants.MotionType.SHAKE_HEAD -> return "label_fr_liveness_shake_head"
            PaFaceConstants.MotionType.NOD_HEAD -> return "label_fr_liveness_nod_head"
            PaFaceConstants.MotionType.AURORA -> return "label_act_face_detect_tips_aurora"
        }
        return ""
    }
}