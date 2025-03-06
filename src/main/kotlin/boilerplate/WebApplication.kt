package boilerplate

import boilerplate.kotlin.spring.common.annotation.custom.TaskOnly
import org.springframework.aop.framework.AopProxyUtils
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import kotlin.reflect.full.functions
import kotlin.system.exitProcess

@SpringBootApplication
class WebApplication

fun main(args: Array<String>) {
    runApplication<WebApplication>(*args)
}

// region
/**
 * aws cli run-task등으로, 외부 트리거로 단발성 잡을 실행하는 기능
 * 동작 후 exitProcess(0)으로 인해 프로세스 종료
 *
 * run-task 예시 (FARGATE로 동작시)
 * aws ecs run-task \
 *   --profile [sso login profile] \
 *   --cluster [ecs 클러스터 환경] \
 *   --task-definition [돌리고싶은 테스크 디피니션] \
 *   --region ap-northeast-2 \
 *   --launch-type FARGATE \
 *   --network-configuration "awsvpcConfiguration={subnets=[subnet-xxxx,subnet-xxxx],securityGroups=[sg-xxxxx],assignPublicIp=ENABLED}" \
 *   --overrides '{
 *     "containerOverrides": [
 *       {
 *         "name": "Container",
 *         "environment": [
 *           { "name": "RUN_TARGET", "value": "TASK" }
 *         ]
 *       }
 *     ]
 *   }'
 */
@Component
class ServiceOnlyRunner(private val context: ApplicationContext) : ApplicationRunner {
    override fun run(args: ApplicationArguments?) {
        val target = System.getenv("RUN_TARGET")?.toString() ?: return // 타겟이 없으면 러너 무시

        when {
            target == "TASK" -> {
                println("RUN_TARGET=TASK 시작")
                context
                    .getBeansWithAnnotation(Service::class.java) // @Service 어노테이션을 가진 빈 검색, Service가 아닌곳은 새로 추가
                    .values
                    .forEach { it ->
                        AopProxyUtils
                            .ultimateTargetClass(it) // @Transactional 같은 프록시 객체가 아닌 원본 객체로 가져오기 위함
                            .kotlin
                            .functions
                            .filter { function -> function.annotations.any { it is TaskOnly } }
                            .forEach { function -> function.call(it) }
                    }

                println("모든 작업 완료, 애플리케이션 종료")
            }

            else -> { }
        }

        exitProcess(0) // 서비스 완료 후 종료
    }
}
// endregion