package com.airTicket.ticketService.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class PassengerInfoDTO {
    private String fullName;
    private Integer age;
    private String gender;
    private String seatNumber;

}
