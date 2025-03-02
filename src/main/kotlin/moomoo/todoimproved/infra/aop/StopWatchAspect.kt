package moomoo.todoimproved.infra.aop

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.util.StopWatch

@Aspect
@Component
class StopWatchAspect {

    private val logger = LoggerFactory.getLogger("Execution Time Logger")


    @Around("@annotation(moomoo.todoimproved.infra.aop.StopWatch)")
    fun run(joinPoint: ProceedingJoinPoint): Any? {
        val stopWatch = StopWatch()

        stopWatch.start()
        val result = joinPoint.proceed()
        stopWatch.stop()

        val timeElapsedMs = stopWatch.totalTimeMillis

        val methodName = joinPoint.signature.name
        val methodArguments = joinPoint.args

        logger.info("Method Name: $methodName | Arguments: ${methodArguments.joinToString(", ")} | Execution Time: ${timeElapsedMs}ms")

        return result
    }
}