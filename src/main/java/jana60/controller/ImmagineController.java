package jana60.controller;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jana60.model.Immagine;
import jana60.model.ImmagineForm;
import jana60.repository.ImmagineRepository;
import jana60.service.ImageService;

@Controller
@RequestMapping("/immagini")
public class ImmagineController {

	@Autowired
	private ImageService service;
	@Autowired
	private ImmagineRepository repo;

	@GetMapping("/list/{pizzaId}")
	public String pizzaImages(@PathVariable("pizzaId") Integer pizzaId, Model model) {
		// chiedo al service la lista delle immagini legate a quel bookId
		List<Immagine> immagine = service.getImmagineByPizzaId(pizzaId);
		// chiedo al service di istanziare una ImageForm inizializzata con quel Book
		ImmagineForm immagineForm = service.createImmagineForm(pizzaId);

		model.addAttribute("listaImmagini", immagine);
		model.addAttribute("immagineForm", immagineForm);
		return "/immagini/list";
	}

	@PostMapping("/save")
	public String saveImage(@ModelAttribute("immagineForm") ImmagineForm immagineForm) {
		// devo salvare l'immagine su database
		try {
			Immagine savedImmagine = service.createImmagine(immagineForm);
			return "redirect:/immagini/list/" + savedImmagine.getPizza().getId();
		} catch (IOException e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to save image");
		}
	}

	@RequestMapping(value = "/{pizzaId}/content", produces = MediaType.IMAGE_JPEG_VALUE)
	public ResponseEntity<byte[]> getImmagineContent(@PathVariable("pizzaId") Integer pizzaId) {
		// recupero il content dal database
		byte[] content = service.getImmagineContent(pizzaId);
		// preparo gli headers della response con il tipo di contenuto
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.IMAGE_JPEG);
		// ritorno il contenuto, gli headers e lo status http
		return new ResponseEntity<byte[]>(content, headers, HttpStatus.OK);
	}

	@GetMapping("/delete/{id}")
	public String delete(@PathVariable("id") Integer immagineId, RedirectAttributes ra, Model model) {
		Optional<Immagine> result = repo.findById(immagineId);
		if (result.isPresent()) {
			repo.delete(result.get());
			ra.addFlashAttribute("successMessage", "L'immagine è stata cancellata con successo!");
			return "redirect:/immagini/list/" + result.get().getPizza().getId();

		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "L'immagine con id non esiste");
		}

	}
}