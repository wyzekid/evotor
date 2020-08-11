package ru.evotor.processing.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Запрос на эндпойнт текущего сервиса
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InputRequest {

    private Long id;
    private String name;
    private LocalDate createDate;

}
