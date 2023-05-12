package com.dxvalley.nedajpaymnetbackend.sendOtpAndConfirm;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "send_confirm_otp")
public class OtpModel {
  @Id
  @SequenceGenerator(
    name = "student_Sequence",
    sequenceName = "student_Sequence",
    allocationSize = 1
  )
  @GeneratedValue(
    strategy = GenerationType.SEQUENCE,
    generator = "student_Sequence"
  )
  private  Long id;
  private  String text;
  private  String mobile;
  private  String phone;
}
