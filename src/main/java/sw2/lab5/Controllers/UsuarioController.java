package sw2.lab5.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import sw2.lab5.Entity.Amistad;
import sw2.lab5.Entity.EstadoAmistad;
import sw2.lab5.Entity.Usuario;
import sw2.lab5.Repository.AmistadRepository;
import sw2.lab5.Repository.UsuarioRepository;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user")
public class UsuarioController {

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    AmistadRepository amistadRepository;

    private Usuario usuarioActual(HttpSession session) {
        return (Usuario) session.getAttribute("usuario");
    }

    private boolean esAdmin(HttpSession session) {
        return "admin".equals(session.getAttribute("rol"));
    }

    // ---------- Perfil propio ----------

    @GetMapping("")
    public String verMiPerfil(Model model, HttpSession session) {
        Usuario usuario = usuarioActual(session);
        if (usuario == null) {
            return "redirect:/loginForm";
        }
        // Volvemos a cargar desde BD para tener los datos mas recientes.
        Usuario actualizado = usuarioRepository.findById(usuario.getId_user()).orElse(usuario);
        model.addAttribute("usuario", actualizado);
        return "user/perfil";
    }

    @GetMapping("/editar")
    public String editar(Model model, @RequestParam(value = "id", required = false) Integer id, HttpSession session) {
        Usuario sesion = usuarioActual(session);
        if (sesion == null) {
            return "redirect:/loginForm";
        }
        // Un usuario normal solo puede editar su propio perfil; el admin puede editar cualquiera.
        int idObjetivo = (id != null && esAdmin(session)) ? id : sesion.getId_user();

        Optional<Usuario> optionalUsuario = usuarioRepository.findById(idObjetivo);
        if (optionalUsuario.isEmpty()) {
            return "redirect:/user";
        }
        model.addAttribute("usuario", optionalUsuario.get());
        return "user/form";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute("usuario") Usuario usuarioForm, HttpSession session) {
        Usuario sesion = usuarioActual(session);
        if (sesion == null) {
            return "redirect:/loginForm";
        }
        boolean editaPropio = usuarioForm.getId_user() == sesion.getId_user();
        if (!editaPropio && !esAdmin(session)) {
            return "redirect:/user";
        }

        Optional<Usuario> optionalUsuario = usuarioRepository.findById(usuarioForm.getId_user());
        if (optionalUsuario.isEmpty()) {
            return "redirect:/user";
        }
        Usuario existente = optionalUsuario.get();
        existente.setFirstname(usuarioForm.getFirstname());
        existente.setLastname(usuarioForm.getLastname());
        existente.setIntro(usuarioForm.getIntro());
        existente.setProfile(usuarioForm.getProfile());
        usuarioRepository.save(existente);

        if (editaPropio) {
            session.setAttribute("usuario", existente);
        }
        return editaPropio ? "redirect:/user" : "redirect:/user/list";
    }

    // ---------- Administración (solo admin, reforzado en WebSecurityConfig) ----------

    @GetMapping("/list")
    public String listaUsuario(Model model) {
        model.addAttribute("listausuarios", usuarioRepository.findAll());
        return "user/list";
    }

    @GetMapping("/borrar")
    public String borrar(@RequestParam("id") int id, HttpSession session) {
        if (!esAdmin(session)) {
            return "redirect:/user";
        }
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        if (usuario.isPresent()) {
            usuarioRepository.deleteById(id);
        }
        return "redirect:/user/list";
    }

    // ---------- Amistades ----------

    @GetMapping("/amigos")
    public String listaAmigos(Model model, HttpSession session) {
        Usuario sesion = usuarioActual(session);
        if (sesion == null) {
            return "redirect:/loginForm";
        }

        List<Usuario> otros = usuarioRepository.findAll().stream()
                .filter(u -> u.getId_user() != sesion.getId_user())
                .collect(Collectors.toList());

        model.addAttribute("usuarios", otros);
        model.addAttribute("solicitudesPendientes",
                amistadRepository.findByReceptor_Id_userAndEstado(sesion.getId_user(), EstadoAmistad.PENDIENTE));
        model.addAttribute("amistades", amistadRepository.findAmigosDe(sesion.getId_user()));
        model.addAttribute("usuarioActualId", sesion.getId_user());
        return "user/amigos";
    }

    @PostMapping("/amistad/solicitar")
    public String solicitarAmistad(@RequestParam("id") int idReceptor, HttpSession session) {
        Usuario sesion = usuarioActual(session);
        if (sesion == null || idReceptor == sesion.getId_user()) {
            return "redirect:/user/amigos";
        }
        boolean yaExiste = amistadRepository.findEntreUsuarios(sesion.getId_user(), idReceptor).isPresent();
        if (!yaExiste) {
            Optional<Usuario> receptor = usuarioRepository.findById(idReceptor);
            receptor.ifPresent(r -> {
                Amistad amistad = new Amistad();
                amistad.setSolicitante(sesion);
                amistad.setReceptor(r);
                amistad.setEstado(EstadoAmistad.PENDIENTE);
                amistadRepository.save(amistad);
            });
        }
        return "redirect:/user/amigos";
    }

    @PostMapping("/amistad/aceptar")
    public String aceptarAmistad(@RequestParam("id") int idAmistad, HttpSession session) {
        Usuario sesion = usuarioActual(session);
        Optional<Amistad> optionalAmistad = amistadRepository.findById(idAmistad);
        if (sesion != null && optionalAmistad.isPresent()) {
            Amistad amistad = optionalAmistad.get();
            if (amistad.getReceptor().getId_user() == sesion.getId_user()) {
                amistad.setEstado(EstadoAmistad.ACEPTADA);
                amistadRepository.save(amistad);
            }
        }
        return "redirect:/user/amigos";
    }

    @PostMapping("/amistad/rechazar")
    public String rechazarAmistad(@RequestParam("id") int idAmistad, HttpSession session) {
        Usuario sesion = usuarioActual(session);
        Optional<Amistad> optionalAmistad = amistadRepository.findById(idAmistad);
        if (sesion != null && optionalAmistad.isPresent()) {
            Amistad amistad = optionalAmistad.get();
            if (amistad.getReceptor().getId_user() == sesion.getId_user()) {
                amistad.setEstado(EstadoAmistad.RECHAZADA);
                amistadRepository.save(amistad);
            }
        }
        return "redirect:/user/amigos";
    }
}
