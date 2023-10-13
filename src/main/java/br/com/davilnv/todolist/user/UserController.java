package br.com.davilnv.todolist.user;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.davilnv.todolist.exception.ExceptionBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    private final IUserRepository userRepository;

    @Autowired
    public UserController(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody UserModel userModel) {
        var user = this.userRepository.findByUsername(userModel.getUsername());
        if (user != null) {
            return ResponseEntity.badRequest().body(new ExceptionBody(ExceptionBody.Status.ERROR, "Usuário já existe!"));
        }

        var passwordHash = BCrypt.withDefaults().hashToString(12, userModel.getPassword().toCharArray());
        userModel.setPassword(passwordHash);

        var created = this.userRepository.save(userModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

}
