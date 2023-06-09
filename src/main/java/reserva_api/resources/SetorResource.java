package reserva_api.resources;

import java.net.URI;

import org.hibernate.annotations.Where;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
import reserva_api.dtos.LocalDto;
import reserva_api.dtos.SetorDto;
import reserva_api.dtos.TransporteDto;
import reserva_api.models.SetorModel;
import reserva_api.models.TransporteModel;
import reserva_api.services.PessoaService;
import reserva_api.services.SetorService;
import reserva_api.utils.ApiError;
import reserva_api.utils.ApiSuccess;

@RestController
@RequestMapping(value = "/setores")
public class SetorResource {

	@Autowired
	private SetorService setorService;

	@Autowired
	private PessoaService pessoaService;

	@GetMapping
	public ResponseEntity<Page<SetorModel>> buscarTodos(Pageable pageable) {
		return ResponseEntity.ok().body(setorService.buscarTodos(pageable));
	}

	@GetMapping(value = "/{id}")
	public ResponseEntity<SetorModel> buscarPorId(@PathVariable Long id) {
		SetorModel setorModel = setorService.buscarPorId(id);
		return ResponseEntity.ok().body(setorModel);
	}

	@PostMapping
	public ResponseEntity<Object> salvar(@RequestBody @Valid SetorDto setorDto) {

		if(setorService.existsByNome(setorDto.getNome())){
			return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiError( "Setor já existe!"));
		}

		var setorModel = new SetorModel();
		BeanUtils.copyProperties(setorDto, setorModel);
		setorService.salvar(setorModel);

		//return ResponseEntity.status(HttpStatus.CREATED).body(setorService.salvar(setorModel));
		return ResponseEntity.status(HttpStatus.CREATED).body(new ApiSuccess("Setor cadastrado com sucesso"));
	}

	@PutMapping("/{id}")
	public ResponseEntity<Object> atualizar(@PathVariable(value = "id") Long id,
											@RequestBody @Valid SetorDto setorDto) {

		setorService.atualizar(id, setorDto);

		//return ResponseEntity.ok(setorService.atualizar(id, setorDto));
		return ResponseEntity.status(HttpStatus.OK).body(new ApiSuccess("Atualização realizada com sucesso!"));
	}

	@DeleteMapping(value = "/{id}")
	public ResponseEntity<Object> excluirPorId(@PathVariable Long id) {
		if (pessoaService.existsBySetorId(id)) {
			return ResponseEntity
					.status(HttpStatus.BAD_REQUEST)
					.body(new ApiError("Não foi possível deletar o setor, pois ele se encontra em uso!"));
		}

		setorService.excluirPorId(id);
		return ResponseEntity.noContent().build();
	}

}
