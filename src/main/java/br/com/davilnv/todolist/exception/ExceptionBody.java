package br.com.davilnv.todolist.exception;

import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;

@Data
public class ExceptionBody {
    @Getter
    public enum Status {
        ERROR("E"),
        WARNING("W"),
        INFO("I");

        private String value;

        Status(String value) {
            this.value = value;
        }

        public static Status fromValue(String value) {
            for (Status status : Status.values()) {
                if (status.getValue().equals(value)) {
                    return status;
                }
            }
            return null;
        }

    }
    private String message;
    private LocalDateTime timestamp = LocalDateTime.now();

    public ExceptionBody(Status status, String message) {
        this.message = message;
    }
}
