package boilerplate.kotlin.spring.api.user.repository

import boilerplate.kotlin.spring.api.user.entity.QUserEntity.userEntity
import boilerplate.kotlin.spring.api.user.entity.UserEntity
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface UserRepository : JpaRepository<UserEntity, Long>, UserQRepository

interface UserQRepository {
    fun getUsersWithoutLeft(): List<UserEntity>
}

class UserQRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : UserQRepository {

    override fun getUsersWithoutLeft(): List<UserEntity> {

        val conditions = arrayOf(
            leftAtIsNotNull(),
        )

        return queryFactory
            .selectFrom(userEntity)
            .where(*conditions)
            .orderBy(userEntity.id.desc())
            .fetch()
    }

    private fun leftAtIsNotNull(): BooleanExpression {
        return userEntity.leftAt.isNotNull
    }
}