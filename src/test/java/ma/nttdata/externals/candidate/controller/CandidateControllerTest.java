package ma.nttdata.externals.candidate.controller;

import ma.nttdata.externals.module.candidate.dto.*;
import ma.nttdata.externals.module.candidate.constants.GenderEnum;
import ma.nttdata.externals.module.candidate.service.CandidateSrv;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import ma.nttdata.externals.module.candidate.controller.CandidateController;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@WebMvcTest(CandidateController.class)
class CandidateControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Mock
  private CandidateSrv candidateSrv;

  @InjectMocks
  private CandidateController candidateController;

  @BeforeEach
  void setUp() {

  }

  @Test
  void createCandidate_ShouldReturnCreated() throws Exception {

    CountryDTO countryDTO = new CountryDTO(
        UUID.randomUUID(),
        "TestCountry",
        "TestCountry",
        List.of() // No cities
    );

    CityDTO cityDTO = new CityDTO(
        UUID.randomUUID(),
        "TestCity",
        countryDTO);

    AddressDTO addressDTO = new AddressDTO(
        UUID.randomUUID(),
        "123 Main St",
        "12345",
        "123 Main St, City, State, 12345",
        cityDTO,
        null,
        countryDTO);

    CandidateDTO candidateDTO = new CandidateDTO(
        UUID.randomUUID(),
        "John Doe",
        LocalDate.of(1990, 1, 1),
        5,
        GenderEnum.M,
        "Java",
        "Experienced Java developer",
        List.of(),
        List.of(),
        List.of(),
        List.of(),
        List.of(),
        addressDTO,
        List.of());
    when(candidateSrv.save(any(CandidateDTO.class))).thenReturn(candidateDTO);
    mockMvc.perform(post("/candidates")
        .contentType(APPLICATION_JSON)
        .content("""
            {
              "fullName": "John Doe",
              "birthDate": "1990-01-01",
              "yearsOfExperience": 5,
              "gender": "MALE",
              "mainTech": "Java",
              "summary": "Experienced Java developer",
              "contacts": [],
              "experiences": [],
              "skills": [],
              "educations": [],
              "cvFiles": [],
              "address": {
                "street": "123 Main St",
                "postalCode": "12345",
                "fullAddress": "123 Main St, City, State, 12345",
                "city": {
                  "cityName": "TestCity"
                },
                "country": {
                  "countryName": "TestCountry"
                }
              },
              "naturalLanguages": []
            }
            """))
        .andExpect(status().isCreated());
  }
}
