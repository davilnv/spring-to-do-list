package br.com.davilnv.todolist.task;

import br.com.davilnv.todolist.exception.ExceptionBody;
import br.com.davilnv.todolist.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final ITaskRepository taskRepository;

    @Autowired
    public TaskController(ITaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody TaskModel taskModel, HttpServletRequest request) {
        taskModel.setUserId(UUID.fromString(request.getAttribute("userId").toString()));

        var currentDate = LocalDateTime.now();

        if (currentDate.isAfter(taskModel.getStartAt()) || currentDate.isAfter(taskModel.getEndAt())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ExceptionBody(ExceptionBody.Status.ERROR, "A data de início /término não pode ser menor que a data atual"));
        } else if (taskModel.getEndAt().isBefore(taskModel.getStartAt())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ExceptionBody(ExceptionBody.Status.ERROR, "A data de término não pode ser menor que a data de início"));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(this.taskRepository.save(taskModel));
    }

    @GetMapping
    public ResponseEntity<?> list(HttpServletRequest request) {
        var userId = UUID.fromString(request.getAttribute("userId").toString());
        var tasks = this.taskRepository.findByUserId(userId);
        return ResponseEntity.ok(tasks);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@RequestBody TaskModel taskModel, HttpServletRequest request, @PathVariable UUID id) {
        var userId = UUID.fromString(request.getAttribute("userId").toString());

        var task = this.taskRepository.findById(id).orElse(null);

        if (task == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ExceptionBody(ExceptionBody.Status.ERROR, "Tarefa não encontrada"));
        } else if (!task.getUserId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ExceptionBody(ExceptionBody.Status.ERROR, "Você não tem permissão para editar essa tarefa"));
        }

        Utils.copyNonNullProperties(taskModel, task);

        return ResponseEntity.ok(this.taskRepository.save(task));
    }
}
