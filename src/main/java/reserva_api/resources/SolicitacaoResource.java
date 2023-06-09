package reserva_api.resources;

import java.net.URI;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.validation.Valid;
import reserva_api.dtos.*;
import reserva_api.dtos.projection.SolicitacaoLocalProjection;
import reserva_api.models.*;
import reserva_api.models.enums.TipoPerfil;
import reserva_api.repositories.filters.RecursoFilter;
import reserva_api.services.PessoaService;
import reserva_api.services.RecursoService;
import reserva_api.services.SolicitacaoService;
import reserva_api.services.ViagemService;
import reserva_api.utils.ApiError;

@RestController
@RequestMapping(value = "/solicitacoes")
public class SolicitacaoResource {

	@Autowired
	private SolicitacaoService solicitacaoService;

	@Autowired
	private ViagemService viagemService;

	@Autowired
	private RecursoService recursoService;

	@Autowired
	private PessoaService pessoaService;

	@GetMapping
	public ResponseEntity<Page<Solicitacao>> buscarTodas(Pageable pageable) {
		return ResponseEntity.ok().body(solicitacaoService.buscarTodas(pageable));
	}

	@GetMapping("/estalivre")
	public ResponseEntity<Boolean> isLivre(RecursoFilter recursoFilter) {
		return ResponseEntity.ok().body(solicitacaoService.IsLivre(recursoFilter));
	}

	/*
	@GetMapping("/resumo")
	public ResponseEntity<Page<ReservaDto>> buscarReservas(RecursoFilter recursoFilter, Pageable pageable) {
		return ResponseEntity.ok().body(solicitacaoService.todasReservaPorData(recursoFilter, pageable));
	}

	@GetMapping("/resumo/locais")
	public ResponseEntity<Page<ReservaDto>> buscarReservasLocal(RecursoFilter recursoFilter, Pageable pageable) {
		return ResponseEntity.ok().body(solicitacaoService.reservaLocalPorData(recursoFilter, pageable));
	}

	@GetMapping("/resumo/transportes")
	public ResponseEntity<Page<ReservaDto>> buscarReservasTransporte(RecursoFilter recursoFilter, Pageable pageable) {
		return ResponseEntity.ok().body(solicitacaoService.reservaTransportePorData(recursoFilter, pageable));
	}*/

	//Lista apenas uma solicitação pelo seu id
	@GetMapping(value = "/{id}")
	public ResponseEntity<Solicitacao> buscarPorId(@PathVariable Long id) {
		Solicitacao solicitacao = solicitacaoService.buscarPorId(id);
		return ResponseEntity.ok().body(solicitacao);
	}

	@GetMapping(value = "/pessoa/{id}")
	public ResponseEntity<List<Solicitacao>> buscarPorPessoa(@PathVariable Long id) {
		return ResponseEntity.ok().body(solicitacaoService.buscarPorPessoa(id));
	}

	@DeleteMapping(value = "/{id}")
	public ResponseEntity<Void> excluirPorId(@PathVariable Long id) {
		solicitacaoService.excluirPorId(id);
		return ResponseEntity.noContent().build();
	}

	//Cadastra Solicitação de Transporte
	@PostMapping(value = "/transporte")
	public ResponseEntity<Object> solicitarTransporte(@RequestBody @Valid SolicitacaoTransporteDto solicitacaoTransporteDto) {

		List<PassageirosDto> passageirosDto = solicitacaoTransporteDto.getPassageiros();

		//pega assentos do veiculo
		var transporteModelOptional = recursoService.buscarTransportePorId(solicitacaoTransporteDto.getIdTransporte());
		var transporte = transporteModelOptional.get();

		var assentos = transporte.getTotalDeAssentos() - 1;

		if(passageirosDto.size() > assentos){
			return ResponseEntity
					.status(HttpStatus.CONFLICT)
					.body(new ApiError("O veículo "+ transporte.getDescricao()+
							" suporta apenas "+ assentos +
							" passageiros"));
		}

		var solicitacao = new Solicitacao();
		BeanUtils.copyProperties(solicitacaoTransporteDto, solicitacao);
		solicitacao = solicitacaoService.solicitarTransporte(solicitacaoTransporteDto, solicitacao);

		var viagem = new Viagem();
		BeanUtils.copyProperties(solicitacaoTransporteDto, viagem);
		viagem.setSolicitacao(new Solicitacao(solicitacao.getId()));
		viagem.setTransporte(new TransporteModel(solicitacaoTransporteDto.getIdTransporte()));
		viagem.setMotorista(new MotoristaModel(solicitacaoTransporteDto.getIdMotorista()));
		viagem = viagemService.salvar(viagem);

		//cadastrar passageiros
		for (PassageirosDto p : passageirosDto) {
			var passageiro = new PassageirosModel();
			BeanUtils.copyProperties(p, passageiro);
			passageiro.setCpf(p.getCpf().replaceAll("[^0-9]", ""));
			passageiro.setViagem(viagem);
			viagemService.salvarPassageiros(passageiro);
		}

		return ResponseEntity.status(HttpStatus.OK).body("Solicitação de transporte enviada com sucesso!");
	}

	//Cadastra Solicitação de Local
	@PostMapping(value = "/local")
	public ResponseEntity<Object> solicitarLocal(@RequestBody @Valid SolicitacaoLocalDto solicitacaoLocalDto) {

		var solicitacao = new Solicitacao();
		BeanUtils.copyProperties(solicitacaoLocalDto, solicitacao);

		Solicitacao solicitacaoSalva = solicitacaoService.salvar(solicitacaoLocalDto.getIdPessoa(), solicitacaoLocalDto.getIdLocal(), solicitacao);

		//URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(solicitacaoSalva.getId()).toUri();
		//return ResponseEntity.created(uri).body(solicitacaoSalva);

		return ResponseEntity.status(HttpStatus.OK).body("Local reservado com sucesso!");
	}

	//Atualizar Solicitação Geral
	@PutMapping("/{id}")
	public ResponseEntity<Solicitacao> atualizar(@PathVariable Long id, @Valid @RequestBody Solicitacao solicitacao) {
		Solicitacao solicitacaoSalva = solicitacaoService.atualizar(id, solicitacao);
		return ResponseEntity.ok(solicitacaoSalva);
	}

	@GetMapping(value = "/viagens")
	public ResponseEntity<Page<Viagem>> buscarTodasViagens(Pageable pageable, HttpServletRequest request) {
		var emailPessoaLogada = request.getUserPrincipal().getName();
		var pessoa = pessoaService.buscarPorEmail(emailPessoaLogada);
		var pessoaModel = pessoa.get();

		System.out.println("Tipo de perfil do usuário: " + pessoaModel.getTipoPerfil());

		Sort sort = Sort.by("solicitacaoId").descending();
		pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

		if (pessoaModel.getTipoPerfil() == TipoPerfil.ADMIN) {
			return ResponseEntity.ok().body(viagemService.buscarTodas(pageable));
		}

		return ResponseEntity.ok().body(viagemService.buscarPorPessoas(pessoaModel.getId(), pageable));
	}

	@GetMapping(value = "/locais")
	public ResponseEntity<Page<SolicitacaoLocalProjection>> buscarTodosLocais(Pageable pageable, HttpServletRequest request) {
		var emailPessoaLogada = request.getUserPrincipal().getName();
		var pessoa = pessoaService.buscarPorEmail(emailPessoaLogada);
		var pessoaModel = pessoa.get();

		System.out.println("Tipo de perfil do usuário: " + pessoaModel.getTipoPerfil());

		Sort sort = Sort.by("solicitacao_id").descending();
		pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

		if (pessoaModel.getTipoPerfil() == TipoPerfil.ADMIN) {
			return ResponseEntity.ok().body(solicitacaoService.buscarTodosLocais(pageable));
		}

		return ResponseEntity.ok().body(solicitacaoService.buscarTodosLocaisPorPessoa(pessoaModel.getId(), pageable));
	}

	@GetMapping(value = "/viagens/{id}")
	public ResponseEntity<Object> buscarPorSolicitacaoTransporte(@PathVariable Long id) {
		return ResponseEntity.ok().body(viagemService.buscarPorSolicitacao(id));
	}

	@PostMapping(value = "/viagens")
	public ResponseEntity<Viagem> salvarViagem(@Valid @RequestBody Viagem viagem) {
		Viagem viagemSalva = viagemService.salvar(viagem);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(viagemSalva.getId())
				.toUri();
		return ResponseEntity.created(uri).body(viagemSalva);
	}

	@DeleteMapping(value = "/viagens/{id}")
	public ResponseEntity<Void> excluirViagemPorId(@PathVariable Long id) {
		viagemService.excluirPorId(id);
		return ResponseEntity.noContent().build();
	}

	@PutMapping("/viagens/{id}")
	public ResponseEntity<Object> atualizarRespostaTransporte(@PathVariable(value = "id") Long id,
											@RequestBody @Valid SolicitacaoTransporteAtualizarDto transporteAtualizarDto) {

		Solicitacao solicitacaoSalva = solicitacaoService.atualizarResposta(id, transporteAtualizarDto);

		//return ResponseEntity.ok(solicitacaoSalva);
		return ResponseEntity.status(HttpStatus.OK).body("Solicitação de transporte atualizado com sucesso!");
	}

	//	@GetMapping("/resumo/equipamentos")
	//	public ResponseEntity<Page<ReservaDto>> buscarReservasEquipamento(RecursoFilter recursoFilter, Pageable pageable) {
	//		return ResponseEntity.ok().body(solicitacaoService.reservaEquipamentoPorData(recursoFilter, pageable));
	//	}

}
