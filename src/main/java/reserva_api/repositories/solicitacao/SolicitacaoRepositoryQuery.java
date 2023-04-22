package reserva_api.repositories.solicitacao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import reserva_api.dto.ReservaDto;
import reserva_api.repositories.filters.RecursoFilter;

public interface SolicitacaoRepositoryQuery {
	public Page<ReservaDto> todasReservaPorData(RecursoFilter recursoFilter, Pageable pageable);
	public Page<ReservaDto> reservaLocalPorData(RecursoFilter recursoFilter, Pageable pageable);
	public Page<ReservaDto> reservaEquipamentoPorData(RecursoFilter recursoFilter, Pageable pageable);
	public Page<ReservaDto> reservaTransportePorData(RecursoFilter recursoFilter, Pageable pageable);

}
