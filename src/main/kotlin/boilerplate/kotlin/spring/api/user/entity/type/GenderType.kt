package boilerplate.kotlin.spring.api.user.entity.type

import boilerplate.kotlin.spring.common.exception.BadRequestException

enum class GenderType {
    MALE,
    FEMALE,
    ;

    companion object {
        fun fromCode(code: Int): GenderType {
            return when (code) {
                1, 3, 5, 7 -> MALE
                2, 4, 6, 8 -> FEMALE
                else -> throw BadRequestException()
            }
        }

        fun fromStr(str: String): GenderType {
            return when (str) {
                "MALE" -> MALE
                "FEMALE" -> FEMALE
                else -> throw BadRequestException()
            }
        }
    }
}