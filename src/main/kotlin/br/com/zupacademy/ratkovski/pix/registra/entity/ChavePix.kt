package br.com.zupacademy.ratkovski.pix.registra.entity


import br.com.zupacademy.ratkovski.pix.grpcenum.TipoChave
import br.com.zupacademy.ratkovski.pix.grpcenum.TipoConta
import br.com.zupacademy.ratkovski.pix.registra.ContaAssociada
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*
import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Entity
//@Introspected
@Table(uniqueConstraints = [UniqueConstraint(name = "uk_chave_pix",columnNames =["chave"])])
class ChavePix (
    @field:NotNull
    @Column(name="cliente_id", length = 16, nullable = false)
    val clienteId:UUID,

    @field:NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val tipo: TipoChave,

    @field:NotBlank
    @field:Size(max = 77)
    @Column(unique =true,length = 77,nullable = false)
    var chave:String,

    @field:NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val tipoConta: TipoConta,

    @field:Valid
    @Embedded
    val conta: ContaAssociada

    ) {

    @Id
    @GeneratedValue

    @Column(length = 16)
    var pixId: UUID? = null

    @Column(nullable = false)
    val criadaEm: LocalDateTime = LocalDateTime.now()


    //quando for registrar no bcb e for chave aleatoria o outro sistema vai gerar
    fun chaveAleatoria(): Boolean {
        return tipo == TipoChave.RANDOM
    }

    fun atualiza(chave: String): Boolean {
        if (chaveAleatoria()) {
            this.chave = chave
            return true
        }
        return false
    }


    override fun toString(): String {
        return "ChavePix(clienteId=$clienteId, tipo=$tipo, chave='$chave', tipoConta=$tipoConta, conta=$conta, pixId=$pixId, criadaEm=$criadaEm)"
    }


    /**
     * Verifica se esta chave pertence a este cliente
     */
    fun pertenceAo(clienteId: UUID) = this.clienteId.equals(clienteId)
}

