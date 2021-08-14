package br.com.zupacademy.ratkovski.pix.registra.externo.clients

import br.com.zupacademy.ratkovski.pix.registra.DadosDaContaResponse
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.client.annotation.Client


@Client("\${itau.erp.url}")

interface ContasDeClientesNoItauClient {
    @Get("/api/v1/clientes/{clienteId}/contas{?tipo}")
    fun buscaContaPorTipo(@PathVariable clienteId: String, @QueryValue tipo:String):HttpResponse<DadosDaContaResponse>

}
