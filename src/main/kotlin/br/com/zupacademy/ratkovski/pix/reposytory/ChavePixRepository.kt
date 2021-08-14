package br.com.zupacademy.ratkovski.pix.reposytory

import br.com.zupacademy.ratkovski.pix.registra.entity.ChavePix
import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository
import java.util.*

@Repository
interface ChavePixRepository:JpaRepository<ChavePix,UUID> {
    fun existsByChave(chave: String?): Boolean
    fun findByIdAndClienteId(pixId: UUID, clienteId: UUID): Optional<ChavePix>

    fun findByChave(chave: String): Optional<ChavePix>
    fun findAllByClienteId(clienteId: UUID): List<ChavePix>


}


