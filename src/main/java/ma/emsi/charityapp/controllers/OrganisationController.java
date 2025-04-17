package ma.emsi.charityapp.controllers;
import ma.emsi.charityapp.repositories.OrganisationRepository;
import ma.emsi.charityapp.entities.Organisation;
import ma.emsi.charityapp.services.OrganisationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
@Controller
@RequestMapping("/organisation")
@CrossOrigin("*")
public class OrganisationController {

    private final OrganisationService organisationService;
    private final OrganisationRepository organisationRepository;

    @Autowired
    public OrganisationController(OrganisationService organisationService, OrganisationRepository organisationRepository) {
        this.organisationService = organisationService;
        this.organisationRepository = organisationRepository;
    }

    @GetMapping("/api")
    @ResponseBody
    public List<Organisation> getAllOrganisations() {
        return organisationService.getAllOrganisations();
    }

    @PostMapping("/api")
    @ResponseBody
    public Organisation createOrganisation(@RequestBody Organisation organisation) {
        return organisationService.saveOrganisation(organisation);
    }

    @GetMapping("/api/{id}")
    @ResponseBody
    public Optional<Organisation> getOrganisationById(@PathVariable int id) {
        return organisationService.getOrganisationById(id);
    }

    @DeleteMapping("/api/{id}")
    public void deleteOrganisation(@PathVariable int id) {
        organisationService.deleteOrganisation(id);
    }

    @GetMapping
    public String afficherOrganisations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            Model model) {

        Page<Organisation> organisationsPage = organisationRepository.findAll(PageRequest.of(page, size));
        model.addAttribute("organisationsPage", organisationsPage);
        return "organisation";
    }
}
