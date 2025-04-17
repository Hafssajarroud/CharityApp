package ma.emsi.charityapp.controllers;

import ma.emsi.charityapp.entities.ActionCharite;
import ma.emsi.charityapp.repositories.ActionChariteRepository;
import ma.emsi.charityapp.services.ActionChariteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Controller
@RequestMapping("/actions")
@CrossOrigin("*")
public class ActionChariteController {

    private final ActionChariteService actionChariteService;

    public ActionChariteController(ActionChariteService actionChariteService) {
        this.actionChariteService = actionChariteService;
    }

    @GetMapping
    public String getAllActions(@RequestParam(required = false) String categorie, Model model) {
        List<ActionCharite> actions;
        if (categorie != null && !categorie.isEmpty()) {
            actions = actionChariteService.getActionsByCategorie(categorie);
        } else {
            actions = actionChariteService.getAllActions();
        }
        model.addAttribute("actions", actions);
        model.addAttribute("selectedCategorie", categorie); // 👈 Pour que le HTML sache quelle option est sélectionnée
        return "actions";  // Retourne la vue actions.html
    }


    @PostMapping
    @ResponseBody
    public ActionCharite createAction(@RequestBody ActionCharite action) {
        return actionChariteService.saveAction(action);
    }

    @GetMapping("/{id}")
    public String getActionById(@PathVariable int id, Model model) {
        ActionCharite action = actionChariteService.getActionById(id).orElse(null);
        model.addAttribute("action", action);
        return "action-details";
    }

    @DeleteMapping("/{id}")
    public void deleteAction(@PathVariable int id) {
        actionChariteService.deleteAction(id);
    }
}
