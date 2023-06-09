package reserva_api.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import reserva_api.dtos.projection.MotoristaProjection;
import reserva_api.models.MotoristaModel;

@Repository
public interface MotoristaRepository extends JpaRepository<MotoristaModel, Long>  {

    //@Query(value = "SELECT p.id, p.cpf, p.data_nascimento, p.nome, p.siape, p.numero, p.tipo, p.tipo_perfil, p.setor_id, p.email, p.senha, p.status, p.codigo_ativacao, m.numero_cnh FROM pessoa p JOIN motorista m ON p.id = m.id", nativeQuery = true)
    @Query(value = "SELECT p.id, p.nome, m.numero_cnh FROM pessoa p JOIN motorista m ON p.id = m.id WHERE m.deleted=false ORDER BY m.id DESC", nativeQuery = true)
    List<MotoristaProjection> buscarTodosMotoristas();

    @Query(value = "SELECT p.id, p.nome, m.numero_cnh FROM pessoa p JOIN motorista m ON p.id = m.id WHERE m.id= ?", nativeQuery = true)
    Optional<MotoristaProjection> buscaPorId(Long id);

    boolean existsByNumeroCnh(String numeroCnh);

}
