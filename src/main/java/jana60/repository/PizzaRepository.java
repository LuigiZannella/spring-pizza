package jana60.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import jana60.model.Pizza;

@Repository
public interface PizzaRepository extends CrudRepository<Pizza, Integer> {

	public Integer countByNome(String nome);

	public List<Pizza> findByNomeContaining(String query);
}