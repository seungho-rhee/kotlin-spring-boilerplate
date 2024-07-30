package boilerplate.kotlin.spring.api.user.entity

import boilerplate.kotlin.spring.api.user.entity.type.GenderType
import jakarta.persistence.*
import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate
import java.time.LocalDateTime

@DynamicInsert
@DynamicUpdate
@Entity
@Table(name = "users")
class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    val id: Long = 0,

    @Column(name = "name", nullable = false)
    val name: String,

    @Column(name = "birth", nullable = false)
    val birth: String,

    @Enumerated(value = EnumType.STRING)
    @Column(name = "gender", nullable = false)
    val gender: GenderType,

    @Column(name = "phone_number", nullable = true)
    var phoneNumber: String? = null,

    @Column(name = "joined_at", nullable = false)
    val joinedAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "left_at", nullable = true)
    var leftAt: LocalDateTime? = null,
) {

    fun updatePhoneNumber(phoneNumber: String?) {
        this.phoneNumber = phoneNumber
    }

    fun updateLeftAt(leftAt: LocalDateTime) {
        this.leftAt = leftAt
    }

}
