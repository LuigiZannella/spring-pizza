package jana60.controller;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jana60.model.Pizza;
import jana60.repository.PizzaRepository;

@Controller
@RequestMapping("/")
public class PizzaController {

	@Autowired 
	private PizzaRepository repo;
	
	@GetMapping("/pizza")
	public String pizza(Model model) {
		List<Pizza> PizzaList = (List<Pizza>)repo.findAll();
		model.addAttribute("PizzaList", PizzaList);
		return "pizza";
	}
	
	@GetMapping("/edit")
	  public String pizzaForm(Model model) {
	    model.addAttribute("pizza", new Pizza());
	    return "edit";
	  }
	
	//Salvare nuovi dati
	 @PostMapping("/edit")
	  public String save(@Valid @ModelAttribute("pizza") Pizza formPizza, BindingResult br) {
		 boolean hasErrors = br.hasErrors();
			boolean validNome = true;
			if(formPizza.getId() != null) {
				Pizza pizzaOld = repo.findById(formPizza.getId()).get();
				if(pizzaOld.getNome().equalsIgnoreCase(formPizza.getNome()))
					validNome = false;
			}
			if(validNome && repo.countByNome(formPizza.getNome()) > 0)
			{
				
				br.addError(new FieldError("pizza", "nome", "Hai già una pizza con questo nome"));
				hasErrors = true;
				
			}
		    if (hasErrors)
		    	return "/edit";
		    
		    else
		    {
		    	
		    	repo.save(formPizza);
			      return "redirect:/pizza"; 

		    }
			
		}
	 
	 //Eliminare dati
	 @GetMapping("/delete/{id}")
	 public String delete(@PathVariable("id") Integer pizzaId, RedirectAttributes ra) {
	Optional<Pizza> result = repo.findById(pizzaId);
	if (result.isPresent()) {
		repo.delete(result.get());
		ra.addFlashAttribute("successMessage", "La Pizza" + result.get().getNome() + "è stato cancellato con successo!");
		return "redirect:/pizza";
	}else {
		throw new ResponseStatusException(HttpStatus.NOT_FOUND,
				"Pizza con id " + pizzaId + "Non esiste");
	}
	
	 } 
	 @GetMapping("/update/{id}")
	 public String update(@PathVariable("id") Integer pizzaId,Model model) {
		 Optional<Pizza> result = repo.findById(pizzaId);
			if (result.isPresent()) {
				model.addAttribute("pizza", result.get());
				repo.save(result.get());
				return "edit";
			}else {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND,
						"Pizza con id " + pizzaId + "Non esiste");
			}
		 
	 }


@GetMapping("/search")
public String search(@RequestParam(name = "query") String query, Model model) {

	if (query != null && query.isEmpty()) {
		query = null;
	}

	List<Pizza> pizze = repo.findByNomeContaining(query);
	model.addAttribute("PizzaList", pizze);
	return "/pizza";
}
}