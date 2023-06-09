package reserva_api.models;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import reserva_api.models.enums.StatusConta;
import reserva_api.models.enums.TipoPerfil;

@Entity
@Table(name = "pessoa")
@Inheritance(strategy = InheritanceType.JOINED)
public class PessoaModel implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	//Gera os ids automaticamente de forma sequencial
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 255)
	private String nome;

	@Column(nullable = false, unique = true, length = 255)
	private String cpf;

	@Column(nullable = false, unique = true, length = 255)
	private String siape;

	@Column(nullable = false, length = 255)
	private LocalDate dataNascimento;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 255)
	private TipoPerfil tipoPerfil;

	@Embedded
	private TelefoneModel telefone;

	@ManyToOne
	@JoinColumn(name = "setor_id")
	private SetorModel setor;

	@Column(nullable = false, length = 255)
	private String email;

	@Column(nullable = false, length = 255)
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String senha;

	@Column(nullable = false, length = 255)
	private String codigoAtivacao;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 255)
	private StatusConta status;

	@JsonIgnore
	@OneToMany(mappedBy = "solicitante")
	private Set<Solicitacao> solicitacoes = new HashSet<>();

	@OneToOne
	@JoinColumn(name = "id")
	@JsonIgnoreProperties({"id"})
	private MotoristaModel motorista;

	public PessoaModel() {

	}

	public PessoaModel(Long id) {
		this.id = id;
	}

	public PessoaModel(Long id, String nome, String cpf, String siape, LocalDate dataNascimento, SetorModel setor,
					   TipoPerfil tipoPerfil, TelefoneModel telefone, StatusConta status) {
		super();
		this.id = id;
		this.nome = nome;
		this.cpf = cpf;
		this.siape = siape;
		this.dataNascimento = dataNascimento;
		this.setor = setor;
		this.tipoPerfil = tipoPerfil;
		this.telefone = telefone;
		this.status = status;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public String getSiape() {
		return siape;
	}

	public void setSiape(String siape) {
		this.siape = siape;
	}

	public LocalDate getDataNascimento() {
		return dataNascimento;
	}

	public void setDataNascimento(LocalDate dataNascimento) {
		this.dataNascimento = dataNascimento;
	}

	public SetorModel getSetor() {
		return setor;
	}

	public void setSetor(SetorModel setor) {
		this.setor = setor;
	}

	public Set<Solicitacao> getSolicitacoes() {
		return solicitacoes;
	}

	public TipoPerfil getTipoPerfil() {
		return tipoPerfil;
	}

	public void setTipoPerfil(TipoPerfil tipoPerfil) {
		this.tipoPerfil = tipoPerfil;
	}

	public void setSolicitacoes(Set<Solicitacao> solicitacoes) {
		this.solicitacoes = solicitacoes;
	}

	public TelefoneModel getTelefone() {
		return telefone;
	}

	public void setTelefone(TelefoneModel telefone) {
		this.telefone = telefone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	public StatusConta getStatus() { return status;	}

	public void setStatus(StatusConta status) {	this.status = status; }

	public String getCodigoAtivacao() { return codigoAtivacao; }

	public void setCodigoAtivacao(String codigoAtivacao) { 	this.codigoAtivacao = codigoAtivacao; }

	public MotoristaModel getMotorista() {
		return motorista;
	}

	public void setMotorista(MotoristaModel motorista) {
		this.motorista = motorista;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PessoaModel other = (PessoaModel) obj;
		return Objects.equals(id, other.id);
	}

}