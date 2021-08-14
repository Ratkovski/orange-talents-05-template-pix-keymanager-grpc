package br.com.zupacademy.ratkovski.pix.carrega

import br.com.zupacademy.ratkovski.pix.exception.ChavePixNaoEncontradaException
import br.com.zupacademy.ratkovski.pix.registra.externo.clients.ChavesDeClientesNoBcbClient
import br.com.zupacademy.ratkovski.pix.reposytory.ChavePixRepository
import br.com.zupacademy.ratkovski.pix.validation.ValidUUID
import io.micronaut.core.annotation.Introspected
import io.micronaut.http.HttpStatus
import org.slf4j.LoggerFactory
import java.util.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size


@Introspected
sealed class Filtro {

    /**
     * sealed
     * classe que sela seus filhos ou seja ela suporta herança  ||
     * pesquisar mais https://kotlinlang.org/docs/sealed-classes.html
     *
     * As classes e interfaces seladas representam hierarquias de classes restritas que fornecem
     * mais controle sobre a herança. Todas as subclasses de uma classe selada são conhecidas em tempo
     * de compilação. Nenhuma outra subclasse pode aparecer depois que um módulo com a classe selada é compilado.
     * Por exemplo, clientes de terceiros não podem estender sua classe lacrada em seu código.
     * Portanto, cada instância de uma classe lacrada tem um tipo de um conjunto limitado que é conhecido
     * quando essa classe é compilada.O mesmo funciona para interfaces seladas e suas implementações:
     * uma vez que um módulo com uma interface selada é compilado, nenhuma nova implementação pode aparecer.
     * Em certo sentido, as classes seladas são semelhantes às enumclasses:
     * o conjunto de valores para um tipo de enum também é restrito, mas cada constante de enum
     * existe apenas como uma única instância , enquanto uma subclasse de uma classe selada pode
     * ter várias instâncias, cada uma com sua própria Estado.
     *
     * **/

    /**
     * Deve retornar chave encontrada ou lançar um exceção de erro de chave não encontrada
     */
    abstract fun filtra(repository: ChavePixRepository, bcbClient: ChavesDeClientesNoBcbClient): ChavePixInfo

    @Introspected
    data class PorPixId(
        @field:NotBlank @field:ValidUUID val clienteId: String,
        @field:NotBlank @field:ValidUUID val pixId: String,
    ) : Filtro() { // 1

        fun pixIdAsUuid() = UUID.fromString(pixId)
        fun clienteIdAsUuid() = UUID.fromString(clienteId)

        override fun filtra(repository: ChavePixRepository, bcbClient: ChavesDeClientesNoBcbClient): ChavePixInfo {
            return repository.findById(pixIdAsUuid())
                .filter { it.pertenceAo(clienteIdAsUuid())}
                .map(ChavePixInfo::of)
                .orElseThrow { ChavePixNaoEncontradaException("Chave Pix não encontrada") }
        }
    }

    @Introspected
    data class PorChave(@field:NotBlank @Size(max = 77) val chave: String) : Filtro() {

        private val LOGGER = LoggerFactory.getLogger(this::class.java)

        override fun filtra(repository: ChavePixRepository, bcbClient: ChavesDeClientesNoBcbClient): ChavePixInfo {
            return repository.findByChave(chave)
                .map(ChavePixInfo::of)
                .orElseGet {
                    LOGGER.info("Consultando chave Pix '$chave' no Banco Central do Brasil (BCB)")

                    val response = bcbClient.findByKey(chave)
                    when (response.status) {
                        HttpStatus.OK -> response.body()?.toModel()
                        else -> throw ChavePixNaoEncontradaException("Chave Pix não encontrada") // 1
                    }
                }
        }
    }

    @Introspected
    class Invalido() : Filtro() {

        override fun filtra(repository: ChavePixRepository, bcbClient: ChavesDeClientesNoBcbClient): ChavePixInfo {
            throw IllegalArgumentException("Chave Pix inválida ou não informada")
        }
    }
}

