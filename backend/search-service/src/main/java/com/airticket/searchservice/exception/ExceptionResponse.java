package com.airticket.searchservice.exception;

import lombok.*;

import java.time.LocalDate;
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExceptionResponse {
    private LocalDate timestamp;
    private String message;
    private String details;
    private String httpCodeMessage;
}
