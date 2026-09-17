package co.edu.eci.blueprints.api;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/blueprints")
public class BlueprintController {
    // traido del anterior lab
    private static final List<Map<String, String>> BLUEPRINTS = List.of(
        Map.of("id", "b1", "author", "student", "name", "Casa de campo"),
        Map.of("id", "b2", "author", "assistant", "name", "Edificio urbano")
    );

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_blueprints.read')")
    public List<Map<String, String>> list() {
        return BLUEPRINTS;
    }

    // traido del anterior lab
    @GetMapping("/{author}")
    @PreAuthorize("hasAuthority('SCOPE_blueprints.read')")
    public List<Map<String, String>> byAuthor(@PathVariable String author) {
        return BLUEPRINTS.stream()
            .filter(blueprint -> blueprint.get("author").equals(author))
            .collect(Collectors.toList());
    }

    // traido del anterior lab
    @GetMapping("/{author}/{name}")
    @PreAuthorize("hasAuthority('SCOPE_blueprints.read')")
    public Map<String, String> byAuthorAndName(@PathVariable String author, @PathVariable String name) {
        return BLUEPRINTS.stream()
            .filter(blueprint -> blueprint.get("author").equals(author))
            .filter(blueprint -> blueprint.get("name").equals(name))
            .findFirst()
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Blueprint no encontrado"));
    }

    // traido del anterior lab
    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_blueprints.write')")
    public Map<String, String> create(@RequestBody Map<String, String> in) {
        return Map.of("id", "new", "name", in.getOrDefault("name", "nuevo"));
    }
}
