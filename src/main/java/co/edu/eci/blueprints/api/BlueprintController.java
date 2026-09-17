package co.edu.eci.blueprints.api;

import co.edu.eci.blueprints.model.Blueprint;
import co.edu.eci.blueprints.model.Point;
import co.edu.eci.blueprints.persistence.BlueprintNotFoundException;
import co.edu.eci.blueprints.persistence.BlueprintPersistenceException;
import co.edu.eci.blueprints.services.BlueprintsServices;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/blueprints")
public class BlueprintController {

    private final BlueprintsServices services;

    public BlueprintController(BlueprintsServices services) {
        this.services = services;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_blueprints.read')")
    public Set<Blueprint> list() {
        return services.getAllBlueprints();
    }

    // traido del anterior lab
    @GetMapping("/{author}")
    @PreAuthorize("hasAuthority('SCOPE_blueprints.read')")
    public Set<Blueprint> byAuthor(@PathVariable String author) {
        try {
            return services.getBlueprintsByAuthor(author);
        } catch (BlueprintNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    // traido del anterior lab
    @GetMapping("/{author}/{name}")
    @PreAuthorize("hasAuthority('SCOPE_blueprints.read')")
    public Blueprint byAuthorAndName(@PathVariable String author, @PathVariable String name) {
        try {
            return services.getBlueprint(author, name);
        } catch (BlueprintNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    // traido del anterior lab
    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_blueprints.write')")
    public Blueprint create(@RequestBody NewBlueprintRequest req) {
        try {
            Blueprint bp = new Blueprint(req.author(), req.name(), req.points());
            services.addNewBlueprint(bp);
            return bp;
        } catch (BlueprintPersistenceException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    // traido del anterior lab
    @PostMapping ("/{author}/{name}/points")
    @PreAuthorize ("hasAuthority('SCOPE_blueprints.write')")
    public void addPoint(@PathVariable String author, @PathVariable String name, @RequestBody Point p) {
        try {
            services.addPoint(author, name, p.x(), p.y());
        } catch (BlueprintNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}
