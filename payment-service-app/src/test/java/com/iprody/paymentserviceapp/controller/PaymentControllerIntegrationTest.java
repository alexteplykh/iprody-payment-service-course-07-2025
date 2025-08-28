package com.iprody.paymentserviceapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iprody.paymentserviceapp.TestJwtFactory;
import com.iprody.paymentserviceapp.dto.NoteUpdateDto;
import com.iprody.paymentserviceapp.dto.PaymentDto;
import com.iprody.paymentserviceapp.dto.PaymentStatusUpdateDto;
import com.iprody.paymentserviceapp.persistence.PaymentRepository;
import com.iprody.paymentserviceapp.persistence.entity.Currency;
import com.iprody.paymentserviceapp.persistence.entity.Payment;
import com.iprody.paymentserviceapp.persistence.entity.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
public class PaymentControllerIntegrationTest extends AbstractPostgresIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private PaymentDto dto;

    @BeforeEach
    public void setUp() {
        UUID inquiryRefId = UUID.randomUUID();
        OffsetDateTime createdAt = OffsetDateTime.now();
        OffsetDateTime updatedAt = OffsetDateTime.now();

        dto = new PaymentDto();
        dto.setAmount(new BigDecimal("123.45"));
        dto.setInquiryRefId(inquiryRefId);
        dto.setCurrency(Currency.EUR);
        dto.setStatus(PaymentStatus.PENDING);
        dto.setCreatedAt(createdAt);
        dto.setUpdatedAt(updatedAt);
    }

    @Test
    void shouldReturnOnlyLiquibasePayments() throws Exception {
        mockMvc.perform(get("/payments/search")
                        .with(TestJwtFactory.jwtWithRole("test-user","admin"))
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[?(@.guid=='00000000-0000-0000-0000-000000000001')]").exists())
                .andExpect(jsonPath("$.content[?(@.guid=='00000000-0000-0000-0000-000000000002')]").exists());
    }

    @Test
    void shouldReturn403WhenSearch() throws Exception {
        mockMvc.perform(get("/payments/search")
                        .with(TestJwtFactory.jwtWithRole("test-user","user"))
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldCreatePaymentAndVerifyInDatabase() throws Exception {
        String json = objectMapper.writeValueAsString(dto);

        String response = mockMvc.perform(post("/payments")
                        .with(TestJwtFactory.jwtWithRole("test-user","admin"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.guid").exists())
                .andExpect(jsonPath("$.currency").value("EUR"))
                .andExpect(jsonPath("$.amount").value(123.45))
                .andExpect(jsonPath("$.inquiryRefId").value(dto.getInquiryRefId().toString()))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        PaymentDto created = objectMapper.readValue(response, PaymentDto.class);
        Optional<Payment> saved = paymentRepository.findById(created.getGuid());
        assertThat(saved).isPresent();
        assertThat(saved.get().getCurrency()).isEqualTo(Currency.EUR);
        assertThat(saved.get().getAmount()).isEqualByComparingTo("123.45");
        assertThat(saved.get().getInquiryRefId()).isEqualByComparingTo(dto.getInquiryRefId());
        assertThat(saved.get().getStatus()).isEqualTo(PaymentStatus.PENDING);
    }

    @Test
    void shouldReturn403WhenCreating() throws Exception {
        String json = objectMapper.writeValueAsString(dto);

        mockMvc.perform(post("/payments")
                        .with(TestJwtFactory.jwtWithRole("test-user","reader"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnPaymentById() throws Exception {
        UUID existingId = UUID.fromString("00000000-0000-0000-0000-000000000002");
        mockMvc.perform(get("/payments/" + existingId)
                        .with(TestJwtFactory.jwtWithRole("test-user","reader"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.guid").value(existingId.toString()))
                .andExpect(jsonPath("$.currency").value("EUR"))
                .andExpect(jsonPath("$.amount").value(50.00));
    }

    @Test
    void shouldReturn404ForNonExistentPayment() throws Exception {
        UUID nonexistentId = UUID.randomUUID();
        mockMvc.perform(get("/payments/" + nonexistentId)
                .with(TestJwtFactory.jwtWithRole("test-user","reader"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorMessage").value("Платеж не найден"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.operation").value("get"))
                .andExpect(jsonPath("$.id").value(nonexistentId.toString()));
    }

    @Test
    void shouldUpdatedPaymentAndVerifyInDatabase() throws Exception {
        String json = objectMapper.writeValueAsString(dto);
        UUID existingId = UUID.fromString("00000000-0000-0000-0000-000000000003");
        String response = mockMvc.perform(put("/payments/" + existingId)
                        .with(TestJwtFactory.jwtWithRole("test-user","admin"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.guid").exists())
                .andExpect(jsonPath("$.currency").value("EUR"))
                .andExpect(jsonPath("$.amount").value(123.45))
                .andExpect(jsonPath("$.inquiryRefId").value(dto.getInquiryRefId().toString()))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        PaymentDto updated = objectMapper.readValue(response, PaymentDto.class);
        Optional<Payment> saved = paymentRepository.findById(updated.getGuid());
        assertThat(saved).isPresent();
        assertThat(saved.get().getCurrency()).isEqualTo(Currency.EUR);
        assertThat(saved.get().getAmount()).isEqualByComparingTo("123.45");
        assertThat(saved.get().getInquiryRefId()).isEqualByComparingTo(dto.getInquiryRefId());
        assertThat(saved.get().getStatus()).isEqualTo(PaymentStatus.PENDING);
    }

    @Test
    void shouldReturn403AtUpdating() throws Exception {
        String json = objectMapper.writeValueAsString(dto);
        UUID existingId = UUID.fromString("00000000-0000-0000-0000-000000000002");
        mockMvc.perform(put("/payments/" + existingId)
                        .with(TestJwtFactory.jwtWithRole("test-user","reader"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldDeletePaymentAndVerifyInDatabase() throws Exception {
        UUID existingId = UUID.fromString("00000000-0000-0000-0000-000000000003");
        mockMvc.perform(delete("/payments/" + existingId)
                        .with(TestJwtFactory.jwtWithRole("test-user","admin")))
                .andExpect(status().isNoContent());

        Optional<Payment> saved = paymentRepository.findById(existingId);
        assertThat(saved).isEmpty();
    }

    @Test
    void shouldReturn403AtDeleting() throws Exception {
        UUID existingId = UUID.fromString("00000000-0000-0000-0000-000000000002");
        mockMvc.perform(delete("/payments/" + existingId)
                        .with(TestJwtFactory.jwtWithRole("test-user","reader")))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldUpdatedStatusAndVerifyInDatabase() throws Exception {
        PaymentStatusUpdateDto statusUpdateDto = new PaymentStatusUpdateDto();
        statusUpdateDto.setStatus(PaymentStatus.APPROVED);

        String json = objectMapper.writeValueAsString(statusUpdateDto);

        UUID existingId = UUID.fromString("00000000-0000-0000-0000-000000000002");
        String response = mockMvc.perform(patch("/payments/" + existingId + "/status")
                        .with(TestJwtFactory.jwtWithRole("test-user","admin"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.guid").exists())
                .andExpect(jsonPath("$.currency").value("EUR"))
                .andExpect(jsonPath("$.amount").value(50))
                .andExpect(jsonPath("$.inquiryRefId").value("10000000-0000-0000-0000-000000000002"))
                .andExpect(jsonPath("$.status").value("APPROVED"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        PaymentDto updated = objectMapper.readValue(response, PaymentDto.class);

        Optional<Payment> saved = paymentRepository.findById(updated.getGuid());
        assertThat(saved).isPresent();
        assertThat(saved.get().getCurrency()).isEqualTo(Currency.EUR);
        assertThat(saved.get().getAmount()).isEqualByComparingTo("50");
        assertThat(saved.get().getInquiryRefId()).isEqualByComparingTo(UUID.fromString("10000000-0000-0000-0000-000000000002"));
        assertThat(saved.get().getStatus()).isEqualTo(PaymentStatus.APPROVED);
    }

    @Test
    void shouldReturn404AtUpdatingStatus() throws Exception {
        UUID nonexistentId = UUID.randomUUID();
        PaymentStatusUpdateDto statusUpdateDto = new PaymentStatusUpdateDto();
        statusUpdateDto.setStatus(PaymentStatus.APPROVED);

        String json = objectMapper.writeValueAsString(statusUpdateDto);

        mockMvc.perform(patch("/payments/" + nonexistentId + "/status")
                        .with(TestJwtFactory.jwtWithRole("test-user","admin"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorMessage").value("Платеж не найден"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.operation").value("updateStatus"))
                .andExpect(jsonPath("$.id").value(nonexistentId.toString()));
    }

    @Test
    void shouldReturn403AtUpdatingStatus() throws Exception {
        PaymentStatusUpdateDto statusUpdateDto = new PaymentStatusUpdateDto();
        statusUpdateDto.setStatus(PaymentStatus.APPROVED);

        String json = objectMapper.writeValueAsString(statusUpdateDto);

        UUID existingId = UUID.fromString("00000000-0000-0000-0000-000000000002");
        mockMvc.perform(patch("/payments/" + existingId + "/status")
                        .with(TestJwtFactory.jwtWithRole("test-user","reader"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldUpdatedNoteAndVerifyInDatabase() throws Exception {
        NoteUpdateDto noteUpdateDto = new NoteUpdateDto();
        noteUpdateDto.setNote("newNote");

        String json = objectMapper.writeValueAsString(noteUpdateDto);

        UUID existingId = UUID.fromString("00000000-0000-0000-0000-000000000002");
        String response = mockMvc.perform(patch("/payments/" + existingId + "/note")
                        .with(TestJwtFactory.jwtWithRole("test-user","admin"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.guid").exists())
                .andExpect(jsonPath("$.currency").value("EUR"))
                .andExpect(jsonPath("$.amount").value(50))
                .andExpect(jsonPath("$.inquiryRefId").value("10000000-0000-0000-0000-000000000002"))
                .andExpect(jsonPath("$.status").value("APPROVED"))
                .andExpect(jsonPath("$.note").value("newNote"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        PaymentDto updated = objectMapper.readValue(response, PaymentDto.class);

        Optional<Payment> saved = paymentRepository.findById(updated.getGuid());
        assertThat(saved).isPresent();
        assertThat(saved.get().getCurrency()).isEqualTo(Currency.EUR);
        assertThat(saved.get().getAmount()).isEqualByComparingTo("50");
        assertThat(saved.get().getInquiryRefId()).isEqualByComparingTo(UUID.fromString("10000000-0000-0000-0000-000000000002"));
        assertThat(saved.get().getStatus()).isEqualTo(PaymentStatus.APPROVED);
        assertThat(saved.get().getNote()).isEqualTo("newNote");
    }

    @Test
    void shouldReturn404AtUpdatingNote() throws Exception {
        UUID nonexistentId = UUID.randomUUID();
        NoteUpdateDto noteUpdateDto = new NoteUpdateDto();
        noteUpdateDto.setNote("newNote");

        String json = objectMapper.writeValueAsString(noteUpdateDto);

        mockMvc.perform(patch("/payments/" + nonexistentId + "/note")
                        .with(TestJwtFactory.jwtWithRole("test-user","admin"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorMessage").value("Платеж не найден"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.operation").value("updateNote"))
                .andExpect(jsonPath("$.id").value(nonexistentId.toString()));
    }

    @Test
    void shouldReturn403AtUpdatingNote() throws Exception {
        NoteUpdateDto noteUpdateDto = new NoteUpdateDto();
        noteUpdateDto.setNote("newNote");

        String json = objectMapper.writeValueAsString(noteUpdateDto);

        UUID existingId = UUID.fromString("00000000-0000-0000-0000-000000000002");
        mockMvc.perform(patch("/payments/" + existingId + "/note")
                        .with(TestJwtFactory.jwtWithRole("test-user","reader"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isForbidden());
    }
}
