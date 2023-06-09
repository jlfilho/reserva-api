package reserva_api.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import reserva_api.dtos.CriarSenhaDto;
import reserva_api.dtos.PessoaDto;
import reserva_api.models.MotoristaModel;
import reserva_api.models.PessoaModel;
import reserva_api.repositories.MotoristaRepository;
import reserva_api.repositories.PessoaRepository;
import reserva_api.repositories.filters.PessoaFilter;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PessoaService {

	@Autowired
	private PessoaRepository pessoaRepository;

	public Page<PessoaModel> buscarTodos(Pageable pageable) {
		return pessoaRepository.findAll(pageable);
	}

	public List<PessoaModel> filtrarUsuarios(List<PessoaModel> listaUsuarios) {
		List<PessoaModel> filteredList = new ArrayList<>();

		for (PessoaModel usuario : listaUsuarios) {
			if (usuario.getStatus() != null) {
				filteredList.add(usuario);
			}
		}

		return filteredList;
	}

	public Page<PessoaDto> filtarTodas(PessoaFilter pessoaFilter,Pageable pageable) {
		return pessoaRepository.filtrarPessoa(pessoaFilter, pageable);
	}

	public Optional<PessoaModel> buscarPorId(Long id) {
		return pessoaRepository.findById(id);
	}

	public Optional<PessoaModel> buscarPorEmail(String email) {
		return pessoaRepository.findByEmail(email);
	}

	public PessoaModel salvar(PessoaModel pessoa) {
		return pessoaRepository.save(pessoa);
	}

	public void excluirPorId(Long id) {
		pessoaRepository.deleteById(id);
	}

	//funcoes de validações
	public boolean existsByCpf(String cpf) {
		return pessoaRepository.existsByCpf(cpf);
	}

	public boolean existsBySiape(String siape) {
		return pessoaRepository.existsBySiape(siape);
	}

	public boolean existsByEmail(String email) {
		return pessoaRepository.existsByEmail(email);
	}

	public boolean existsBySetorId(Long id) {
		return pessoaRepository.existsBySetorId(id);
	}

	public Optional<PessoaModel> findById(Long id) {
		return pessoaRepository.findById(id);
	}

}
