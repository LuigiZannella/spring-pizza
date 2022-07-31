package jana60.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import jana60.model.Immagine;
import jana60.model.Pizza;

public interface ImmagineRepository extends CrudRepository<Immagine, Integer> {

	public List<Immagine> findByPizza(Pizza pizza);

}