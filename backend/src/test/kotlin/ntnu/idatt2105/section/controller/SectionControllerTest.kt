package ntnu.idatt2105.section.controller

import com.fasterxml.jackson.databind.ObjectMapper
import ntnu.idatt2105.section.dto.SectionCreateDto
import ntnu.idatt2105.section.dto.SectionDto
import ntnu.idatt2105.section.factory.SectionFactory
import ntnu.idatt2105.section.model.Section
import ntnu.idatt2105.section.repository.SectionRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.modelmapper.ModelMapper
import org.springframework.http.MediaType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.util.*
import org.hamcrest.Matchers.hasItem
import org.assertj.core.api.Assertions.assertThat



@SpringBootTest
@AutoConfigureMockMvc
class SectionControllerTest {

    private val URL = "/sections/"

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var mvc: MockMvc

    @Autowired
    private lateinit var sectionRepository: SectionRepository

    private lateinit var section : Section

    @Autowired
    private lateinit var modelMapper: ModelMapper


    @BeforeEach
    fun setUp(){
        section = SectionFactory().`object`
        section = sectionRepository.save(section)


    }

    @Test
    @WithMockUser(value = "spring")
    fun `test section controller GET all returns OK and page of sections`() {
        val newSection =  SectionFactory().`object`
        newSection.parent = section
        sectionRepository.save(newSection)
        this.mvc.perform(get(URL))
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content.[*].name", hasItem(section.name)))
                .andExpect(jsonPath("$.content.[*].name", hasItem(newSection.name)))


    }


    @Test
    @WithMockUser(value = "spring")
    fun `test section controller GET returns OK and the section`() {
        this.mvc.perform(get("$URL{sectionId}/", section.id.toString()))
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("\$.name").value(section.name))
                .andExpect(jsonPath("\$.type").value(section.getType().toString()))

    }

    @Test
    @WithMockUser(value = "spring")
    fun `test section controller GET returns not found`() {
        this.mvc.perform(get("$URL{sectionId}", UUID.randomUUID().toString()))
                .andExpect(status().isNotFound)
    }

    @Test
    @WithMockUser(value = "spring")
    fun `test section controller POST returns Created and the created section`() {

        this.mvc.perform(post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(section)))
                .andExpect(status().isCreated)
                .andExpect(jsonPath("\$.name").value(section.name))
    }

    @Test
    @WithMockUser(value = "spring")
    fun `test section controller PUT returns OK and the updated section`() {
        val name = "new name"
        section.name = name

        this.mvc.perform(put("$URL{sectionId}/", section.id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(section)))
                .andExpect(status().isOk)
                .andExpect(jsonPath("\$.name").value(name))
    }

    @Test
    @WithMockUser(value = "spring")
    fun `test section controller PUT returns not found`() {
        this.mvc.perform(put("$URL{sectionId}/", UUID.randomUUID().toString())
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(section)))
                .andExpect(status().isNotFound)
    }

    @Test
    @WithMockUser(value = "spring")
    fun `test secton controller DELETE returns OK`() {
        this.mvc.perform(delete("$URL{sectionId}/", section.id))
                .andExpect(status().isOk)
    }

    @Test
    @WithMockUser(value = "spring")
    fun `test section controller DELETE return NotFound`() {
        this.mvc.perform(delete("$URL{sectionId}/", UUID.randomUUID().toString()))
                .andExpect(status().isNotFound)
    }


    @Test
    @WithMockUser(value = "spring")
    fun `test section controller POST with parentId adds parent`() {

        val newSection = SectionCreateDto(name = section.name, parentId = section.id)
        this.mvc.perform(post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newSection)))
                .andExpect(status().isCreated)
                .andExpect(jsonPath("\$.name").value(section.name))

        assertThat(sectionRepository.findById(section.id).get().children?.isNotEmpty())



    }
}