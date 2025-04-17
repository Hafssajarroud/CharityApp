package ma.emsi.charityapp.controllers;

import ma.emsi.charityapp.entities.Don;
import ma.emsi.charityapp.services.DonService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dons")
@CrossOrigin("*")
public class DonController {

    private final DonService donService;

    public DonController(DonService donService) {
        this.donService = donService;
    }

    @GetMapping
    public List<Don> getAllDons() {
        return donService.getAllDons();
    }

    @PostMapping
    public Don createDon(@RequestBody Don don) {
        return donService.createDon(don);
    }

    @DeleteMapping("/{id}")
    public void deleteDon(@PathVariable int id) {
        donService.deleteDon(id);
    }
}
