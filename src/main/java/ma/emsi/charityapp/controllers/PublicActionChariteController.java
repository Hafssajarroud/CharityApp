package ma.emsi.charityapp.controllers;

import ma.emsi.charityapp.entities.ActionCharite;
import ma.emsi.charityapp.services.ActionChariteService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/actions")
@CrossOrigin("*")
@org.springframework.context.annotation.Primary
public class PublicActionChariteController {

    private final ActionChariteService actionChariteService;

    public PublicActionChariteController(ActionChariteService actionChariteService) {
        this.actionChariteService = actionChariteService;
    }

    @GetMapping
    public String getAllActions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size,
            @RequestParam(required = false) String categorie,
            Model model) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ActionCharite> actionsPage;

        if (categorie != null && !categorie.isEmpty()) {
            actionsPage = actionChariteService.getActionsByCategorie(categorie, pageable);
        } else {
            actionsPage = actionChariteService.getAllActions(pageable);
        }

        model.addAttribute("page", actionsPage);
        model.addAttribute("selectedCategorie", categorie);
        return "actions";
    }

    @PostMapping
    @ResponseBody
    public ActionCharite createAction(@RequestBody ActionCharite action) {
        return actionChariteService.saveAction(action);
    }

    @DeleteMapping("/{id}")
    public void deleteAction(@PathVariable int id) {
        actionChariteService.deleteAction(id);
    }
}
