package sw2.lab5.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import sw2.lab5.Entity.Post;
import sw2.lab5.Entity.Usuario;
import sw2.lab5.Repository.PostRepository;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/post")
public class PostController {

    @Autowired
    PostRepository postRepository;

    /**
     * Un usuario puede modificar/borrar un post si es admin o si es el autor del post.
     */
    private boolean puedeGestionar(Post post, HttpSession session) {
        String rol = (String) session.getAttribute("rol");
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return false;
        }
        if ("admin".equals(rol)) {
            return true;
        }
        return post.getUser() != null && post.getUser().getId_user() == usuario.getId_user();
    }

    @GetMapping("")
    public String listaPost(Model model, HttpSession session) {
        List<Post> listapost = postRepository.findAll();
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        model.addAttribute("lista", listapost);
        model.addAttribute("usuarioActualId", usuario != null ? usuario.getId_user() : -1);
        return "post/lista";
    }

    @GetMapping("/create")
    public String nuevoPost(Model model) {
        model.addAttribute("post", new Post());
        return "post/form";
    }

    @GetMapping("/edit")
    public String editPost(Model model, @RequestParam("id") int id, HttpSession session) {
        Optional<Post> optionalPost = postRepository.findById(id);
        if (optionalPost.isEmpty()) {
            return "redirect:/post";
        }
        Post post = optionalPost.get();
        if (!puedeGestionar(post, session)) {
            return "redirect:/post";
        }
        model.addAttribute("post", post);
        return "post/form";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute("post") Post post, HttpSession session) {
        Usuario usuarioSesion = (Usuario) session.getAttribute("usuario");
        if (usuarioSesion == null) {
            return "redirect:/loginForm";
        }

        if (post.getId_Post() == 0) {
            // Nuevo post: el autor siempre es el usuario logueado.
            post.setUser(usuarioSesion);
            post.setPublished(true);
            postRepository.save(post);
        } else {
            Optional<Post> optionalPost = postRepository.findById(post.getId_Post());
            if (optionalPost.isEmpty()) {
                return "redirect:/post";
            }
            Post existente = optionalPost.get();
            if (!puedeGestionar(existente, session)) {
                return "redirect:/post";
            }
            existente.setTitle(post.getTitle());
            existente.setSummary(post.getSummary());
            existente.setContent(post.getContent());
            postRepository.save(existente);
        }
        return "redirect:/post";
    }

    @GetMapping("/borrar")
    public String borrarPost(@RequestParam("id") int id, HttpSession session) {
        Optional<Post> opt = postRepository.findById(id);
        if (opt.isPresent() && puedeGestionar(opt.get(), session)) {
            postRepository.deleteById(id);
        }
        return "redirect:/post";
    }
}
