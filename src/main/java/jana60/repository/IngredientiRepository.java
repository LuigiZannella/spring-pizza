package jana60.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import jana60.model.Ingredienti;

@Repository
public interface IngredientiRepository extends CrudRepository<Ingredienti, Integer> {
	public List<Ingredienti> findAllByOrderByNome();
}