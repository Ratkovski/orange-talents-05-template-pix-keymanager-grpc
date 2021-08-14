package br.com.zupacademy.ratkovski.pix.registra.externo.clients

import br.com.zupacademy.ratkovski.DetailsKeyPixResponse
import br.com.zupacademy.ratkovski.pix.carrega.DetailsPixKeyResponse
import br.com.zupacademy.ratkovski.pix.registra.externo.CreatePixKeyRequest
import br.com.zupacademy.ratkovski.pix.registra.externo.CreatePixKeyResponse
import br.com.zupacademy.ratkovski.pix.registra.externo.DeletePixKeyRequest
import br.com.zupacademy.ratkovski.pix.registra.externo.DeletePixKeyResponse
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.micronaut.http.client.annotation.Client


@Client("\${bcb.pix.url}")
interface ChavesDeClientesNoBcbClient {

    @Post("/api/v1/pix/keys")

    @Produces(MediaType.APPLICATION_XML)
    @Consumes(MediaType.APPLICATION_XML)
    fun create(@Body request: CreatePixKeyRequest): HttpResponse<CreatePixKeyResponse>


    @Delete("/api/v1/pix/keys/{key}")

    @Produces(MediaType.APPLICATION_XML)
    @Consumes(MediaType.APPLICATION_XML)
    fun delete(@PathVariable key:String, @Body request: DeletePixKeyRequest): HttpResponse<DeletePixKeyResponse>

    @Get("/api/v1/pix/keys/{key}")

    @Consumes(MediaType.APPLICATION_XML)
    fun findByKey(@PathVariable key:String): HttpResponse<DetailsPixKeyResponse>
}