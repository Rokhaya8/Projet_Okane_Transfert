package com.okanetransfer.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PayoutRequestDTO {
    @NotBlank(message = "La pièce d'identité du bénéficiaire est obligatoire")
    private String pieceIdentiteBeneficiaire;
}
