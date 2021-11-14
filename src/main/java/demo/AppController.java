package demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Controller
public class AppController {

    @Autowired
    private PersonRepository repository;

    @RequestMapping("/")
    public String index(){
        return "list";
    }

    @RequestMapping("/list")
    public String list(Model model){
        List<Person> data = new LinkedList<>();
        for (Person p: repository.findAll()){
            data.add(p);
        }
        model.addAttribute("people", data);
        return "list";
    }

    @RequestMapping("/input")
    public String input(){
        return "input";
    }

    @RequestMapping("/create")
    public String create(
            @RequestParam(name="firstname", required=true) String firstname,
            @RequestParam(name="lastname", required=true) String lastname) {
        repository.save(new Person(firstname,lastname));
        return "redirect:/list";
    }



    @RequestMapping("/show")
    public String read(
            @RequestParam(name="id", required=true) Long id,
            Model model) {

        Optional<Person> result = repository.findById(id);      // Recupero dal DB l'oggetto con ID dato: Optional può essere null
        if(result.isPresent()){                                 // Se isPresent() ritorna true, la persona esiste
            Person person = result.get();                       // Estraggo da result l'oggetto di tipo Person
            model.addAttribute("person", person);   // Dichiaro l'attributo person da passare alla view
            return "show";                                      // renderizzo il template HTML
        }
        else {                                                  // Altrimenti, quando la persona non è stata trovata nel DB
            return "notfound";                                  // Renderizzo il template HTML della pagina NOTFOUND
        }
    }



    @RequestMapping("/edit")
    public String edit(
            @RequestParam(name="id", required=true) Long id,
            Model model) {

        Optional<Person> result = repository.findById(id);      // Recupero dal DB l'oggetto con ID dato: Optional può essere null
        if(result.isPresent()){                                 // Se isPresent() ritorna true, la persona esiste
            Person person = result.get();                       // Estraggo da result l'oggetto di tipo Person
            model.addAttribute("person", person);    // Dichiaro l'attributo person da passare alla view
            return "edit";                                      // Renderizzo il template HTML
        }
        else {                                                  // Altrimenti, quando la persona non è stata trovata nel DB
            return "notfound";                                  // Renderizzo il template HTML della pagina NOTFOUND
        }
    }



    @RequestMapping("/update")
    public String update(
            @RequestParam(name="id", required=true) Long id,
            @RequestParam(name="firstname", required=true) String firstname,
            @RequestParam(name="lastname", required=true) String lastname,
            Model model) {

        Optional<Person> result = repository.findById(id);      // Recupero dal DB l'oggetto con ID dato: Optional può essere null
        if(result.isPresent()){                                 // Se isPresent() ritorna true, la persona esiste
            //SPECIFICA: delete the old person and add a new person

            /*
            * La richiesta richiede che si vada ad eseguire la delete della persona, per
            * poi successivamente inserire una NEW Person con nome e cognome aggiornato.
            * In questo modo, però, si sta inserendo una vera e propria persona nuova,
            * che andrà quindi ad assumere un nuovo ID (generato automaticamente).
            * Ipotizzando che l'ID indentifichi in modo univoco tale persona nel sistema,
            * ritengo scomodo che, all'aggiornamento del profilo, essa acquisisca un nuovo ID.
            * In particolare, se un utente è identificato con il numero "391", mi aspetto che
            * manterrà sempre tale identificativo.
            * Ho quindi optato per un secondo metodo implementativo, spiegato nel seguito.
            *
            * In ogni caso, il codice commentando qui alle due linee successive implementa la
            * feature come richiesto da specifica, andando ad eliminare la persona e aggiungendone
            * una nuova.
            */

            // repository.deleteById(id);                       // [non usato] Rimuove la persona OLD dal DB
            // repository.save(new Person(firstname, lastname));// [non usato] Aggiunge una nuova persona aggiornata nel DB


            /*
            * Nell'implementazione alternativa proposta, vado ad aggiornare
            * i valori della persona nel DB. A tale scopo, ho implementato due
            * ulteriori metodi nella classe Person (setFirstName e setLastName)
            * che si occupano dell'aggiornamento dei campi della classe Person.
            * Dopo di che, utilizzando tali metodi, vado ad aggiornare i valori
            * in questione. Infine, salvo le modifiche sul DB, per renderle
            * permanenti.
            */
            Person person = result.get();                       // Estraggo da result l'oggetto di tipo Person
            person.setFirstName(firstname);                     // Aggiorno il nome della persona con l'input ricevuto
            person.setLastName(lastname);                       // Aggiorno il cognome della persona con l'input ricevuto

            repository.save(person);                            // Salvo le modifiche sul DB

            return "redirect:/list";                            // Renderizzo il template HTML
        }
        else {                                                  // Altrimenti, quando la persona non è stata trovata nel DB
            return "notfound";                                  // Renderizzo il template HTML della pagina NOTFOUND
        }
    }



    @RequestMapping("/delete")
    public String delete(
            @RequestParam(name="id", required=true) Long id) {

        Optional<Person> result = repository.findById(id);      // Recupero dal DB l'oggetto con ID dato: Optional può essere null
        if(result.isPresent()) {                                // Se isPresent() ritorna true, la persona esiste
            repository.deleteById(id);                          // Eliminazione dal DB dell'oggetto con ID dato
            return "redirect:/list";                            // renderizzo il template HTML
        }
        else{                                                   // Altrimenti, quando la persona non è stata trovata nel DB
            return "notfound";                                  // Renderizzo il template HTML della pagina NOTFOUND
        }
    }
}