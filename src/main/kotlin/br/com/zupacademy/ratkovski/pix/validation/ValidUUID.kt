package br.com.zupacademy.ratkovski.pix.validation

import javax.validation.Constraint
import javax.validation.ReportAsSingleViolation
import javax.validation.constraints.Pattern
import kotlin.reflect.KClass
import kotlin.annotation.AnnotationTarget.*
import kotlin.annotation.AnnotationRetention.RUNTIME


@ReportAsSingleViolation
@Constraint(validatedBy = [])
@Pattern(regexp = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-5][0-9a-f]{3}-[089ab][0-9a-f]{3}-[0-9a-f]{12}$",
flags = [Pattern.Flag.CASE_INSENSITIVE])
@Retention(RUNTIME)
@Target(FIELD,CONSTRUCTOR,PROPERTY,VALUE_PARAMETER)
annotation class ValidUUID(
    val message:String ="não é um formato válido de UUID",
    val groups:Array<KClass<Any>> =[],
    val payload:Array<KClass<Any>> = [],

)

