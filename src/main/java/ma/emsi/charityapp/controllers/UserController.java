package ma.emsi.charityapp.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.emsi.charityapp.dtos.UserRegistrationDto;
import ma.emsi.charityapp.entities.Role;
import ma.emsi.charityapp.entities.User;
import ma.emsi.charityapp.services.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String showLoginForm(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("error", "Email ou mot de passe incorrect");
        }
        return "auth/login";
    }

    @PostMapping("/login")
    public String processLogin() {
        // Spring Security handles the actual login process
        return "redirect:/dashboard";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new UserRegistrationDto());
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") UserRegistrationDto registrationDto,
                               BindingResult result, Model model,
                               RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "auth/register";
        }

        if (!registrationDto.getMotDePasse().equals(registrationDto.getConfirmMotDePasse())) {
            result.rejectValue("confirmMotDePasse", "error.user", "Les mots de passe ne correspondent pas");
            return "auth/register";
        }

        if (userService.existsByEmail(registrationDto.getEmail())) {
            result.rejectValue("email", "error.user", "Cet email est déjà utilisé");
            return "auth/register";
        }

        User user = new User();
        user.setNom(registrationDto.getNom());
        user.setEmail(registrationDto.getEmail());
        user.setMotDePasse(passwordEncoder.encode(registrationDto.getMotDePasse()));
        user.setTelephone(registrationDto.getTelephone());

        // Conversion sécurisée du TypeCompte vers Role
        Role role = switch (registrationDto.getTypeCompte()) {
            case DONATEUR -> Role.DONATEUR;
            case ORGANISATION -> Role.ORGANISATION;
        };
        user.setRole(role);

        userService.saveUser(user);

        // Rediriger vers le formulaire approprié selon le type de compte
        if (user.getRole() == Role.ORGANISATION) {
            redirectAttributes.addFlashAttribute("success", "Compte créé avec succès. Veuillez compléter les informations de votre organisation.");
            return "redirect:/organisation/new";
        } else {
            redirectAttributes.addFlashAttribute("success", "Compte créé avec succès. Vous pouvez maintenant vous connecter.");
            return "redirect:/auth/login?success";
        }
    }
}
