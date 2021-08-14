package br.com.zupacademy.ratkovski.pix.registra.externo

import java.time.LocalDateTime

data class DeletePixKeyResponse(
    val key:String,
    val participant:String,
    val deletedAt:LocalDateTime
)
