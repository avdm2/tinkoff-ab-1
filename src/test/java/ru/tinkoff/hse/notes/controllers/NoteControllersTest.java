package ru.tinkoff.hse.notes.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.tinkoff.hse.Main;
import ru.tinkoff.hse.notes.dto.notes.CreateNoteDto;
import ru.tinkoff.hse.notes.dto.notes.GetNotesDto;
import ru.tinkoff.hse.notes.dto.notes.ResponseNoteDto;
import ru.tinkoff.hse.notes.dto.notes.UpdateNoteDto;
import ru.tinkoff.hse.notes.entities.Note;
import ru.tinkoff.hse.notes.repositories.NoteRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;

@Deprecated
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Main.class)
public class NoteControllersTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private NoteRepository noteRepository;

    @BeforeEach
    void setUp() {
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getName()).thenReturn("admin");

        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        noteRepository.deleteAll();
    }

    @Test
    void testCreateNote() {
        ResponseEntity<ResponseNoteDto> response = sendCreateRequest("name", "login", "password", "url.com");

        assertThat(response.getStatusCode(), is(HttpStatus.OK));

        ResponseNoteDto note = response.getBody();
        assertThat(note.getName(), is("name"));
        assertThat(note.getLogin(), is("login"));
        assertThat(note.getPassword(), is("password"));
        assertThat(note.getUrl(), is("url.com"));
    }

    @Test
    void testCreateNoteWithEmptyNotRequiredFields() {
        ResponseEntity<ResponseNoteDto> response = sendCreateRequest(null, "name", null, "password");

        // Возможно, для более крупного проекта стоит изменить логику генерации логина и ссылки (убрать оттуда секунды)
        String expectedLogin = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss"));

        assertThat(response.getStatusCode(), is(HttpStatus.OK));

        ResponseNoteDto note = response.getBody();
        assertThat(note.getName(), is("name"));
        assertThat(note.getLogin(), is(expectedLogin));
        assertThat(note.getPassword(), is("password"));
        assertThat(note.getUrl(), is(expectedLogin + ".com"));
    }

    @Test
    void testCreateNoteWithEmptyRequiredFields() {
        ResponseEntity<ResponseNoteDto> response = sendCreateRequest("name", "login", null, "url.com");

        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }

    @Test
    void testGetAllNotes() {
        sendCreateRequest("firstName", "firstLogin", "firstPassword", "firstUrl.com");
        sendCreateRequest("secondName", "secondLogin", "secondPassword", "secondUrl.com");
        sendCreateRequest("thirdName", "thirdLogin", "thirdPassword", "thirdUrl.com");
        sendCreateRequest("fourthName", "fourthLogin", "fourthPassword", "fourthUrl.com");

        ResponseEntity<List<GetNotesDto>> response = restTemplate.exchange("/notes/list?pageNumber=1&pageSize=4", HttpMethod.GET, null,
                new ParameterizedTypeReference<>() {
                }
        );

        List<GetNotesDto> list = response.getBody();
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(list.size(), is(4));

        GetNotesDto firstNote = list.get(0);
        assertThat(firstNote.getName(), is("firstName"));

        GetNotesDto secondNote = list.get(1);
        assertThat(secondNote.getName(), is("secondName"));

        GetNotesDto thirdNote = list.get(2);
        assertThat(thirdNote.getName(), is("thirdName"));

        GetNotesDto fourthNote = list.get(3);
        assertThat(fourthNote.getName(), is("fourthName"));
    }

    @Test
    void testGetNotesPagination() {
        sendCreateRequest("firstName", "firstLogin", "firstPassword", "firstUrl.com");
        sendCreateRequest("secondName", "secondLogin", "secondPassword", "secondUrl.com");
        sendCreateRequest("thirdName", "thirdLogin", "thirdPassword", "thirdUrl.com");
        sendCreateRequest("fourthName", "fourthLogin", "fourthPassword", "fourthUrl.com");

        ResponseEntity<List<GetNotesDto>> response = restTemplate.exchange("/notes/list?pageNumber=2&pageSize=2", HttpMethod.GET, null,
                new ParameterizedTypeReference<>() {
                }
        );

        List<GetNotesDto> list = response.getBody();
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(list.size(), is(2));

        GetNotesDto firstNote = list.get(0);
        assertThat(firstNote.getName(), is("thirdName"));

        GetNotesDto secondNote = list.get(1);
        assertThat(secondNote.getName(), is("fourthName"));
    }

    @Test
    void testGetNotesWithIncorrectPaginationValue() {
        sendCreateRequest("firstName", "firstLogin", "firstPassword", "firstUrl.com");
        sendCreateRequest("secondName", "secondLogin", "secondPassword", "secondUrl.com");
        sendCreateRequest("thirdName", "thirdLogin", "thirdPassword", "thirdUrl.com");
        sendCreateRequest("fourthName", "fourthLogin", "fourthPassword", "fourthUrl.com");

        ResponseEntity<Void> response = restTemplate.exchange("/notes/list?pageNumber=-1&pageSize=-1", HttpMethod.GET, null, Void.class);

        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }

    @Test
    void testGetNotesWithPaginationAmountBiggerThanActualListSize() {
        sendCreateRequest("firstName", "firstLogin", "firstPassword", "firstUrl.com");
        sendCreateRequest("secondName", "secondLogin", "secondPassword", "secondUrl.com");

        ResponseEntity<List<GetNotesDto>> response = restTemplate.exchange("/notes/list?pageNumber=1&pageSize=100", HttpMethod.GET, null,
                new ParameterizedTypeReference<>() {
                }
        );

        List<GetNotesDto> list = response.getBody();
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(list.size(), is(2));

        GetNotesDto firstNote = list.get(0);
        assertThat(firstNote.getName(), is("firstName"));

        GetNotesDto secondNote = list.get(1);
        assertThat(secondNote.getName(), is("secondName"));
    }


    @Test
    void testGetNote() {
        sendCreateRequest("firstName", "firstLogin", "firstPassword", "firstUrl.com");
        sendCreateRequest("secondName", "secondLogin", "secondPassword", "secondUrl.com");
        sendCreateRequest("thirdName", "thirdLogin", "thirdPassword", "thirdUrl.com");
        sendCreateRequest("fourthName", "fourthLogin", "fourthPassword", "fourthUrl.com");

        UUID third = noteRepository.findAllByNoteOwner("admin")
                .stream()
                .filter(note -> note.getName().equals("thirdName"))
                .toList().get(0).getId();

        ResponseEntity<Note> response = restTemplate.exchange("/notes/" + third, HttpMethod.GET, null, Note.class);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));

        Note note = response.getBody();
        assertThat(note.getName(), is("thirdName"));
        assertThat(note.getLogin(), is("thirdLogin"));
        assertThat(note.getPassword(), is("thirdPassword"));
        assertThat(note.getUrl(), is("thirdUrl.com"));
    }

    @Test
    void testGetNoteByIncorrectUUID() {
        ResponseEntity<Void> response = restTemplate.exchange("/notes/" + UUID.randomUUID(), HttpMethod.GET, null, Void.class);

        assertThat(response.getStatusCode(), is(HttpStatus.NOT_FOUND));
    }

    @Test
    void testUpdateNote() {
        sendCreateRequest("firstName", "firstLogin", "firstPassword", "firstUrl.com");
        UUID first = noteRepository.findAllByNoteOwner("admin")
                .stream()
                .filter(note -> note.getName().equals("firstName"))
                .toList().get(0).getId();

        UpdateNoteDto newNote = new UpdateNoteDto()
                .setName("newFirstName")
                .setLogin("newFirstLogin")
                .setPassword("newFirstPassword")
                .setUrl("newFirstUrl.com");
        ResponseEntity<ResponseNoteDto> response = restTemplate.exchange("/notes/" + first, HttpMethod.PUT, new HttpEntity<>(newNote), ResponseNoteDto.class);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));

        ResponseNoteDto updatedNote = response.getBody();
        assertThat(updatedNote.getName(), is("newFirstName"));
        assertThat(updatedNote.getLogin(), is("newFirstLogin"));
        assertThat(updatedNote.getPassword(), is("newFirstPassword"));
        assertThat(updatedNote.getUrl(), is("newFirstUrl.com"));
    }

    @Test
    void testUpdateNoteWithNullFields() {
        sendCreateRequest("firstName", "firstLogin", "firstPassword", "firstUrl.com");

        UpdateNoteDto newNote = new UpdateNoteDto()
                .setName("newFirstName")
                .setUrl("newFirstUrl.com");

        UUID first = noteRepository.findAllByNoteOwner("admin")
                .stream()
                .filter(note -> note.getName().equals("firstName"))
                .toList().get(0).getId();

        ResponseEntity<ResponseNoteDto> response = restTemplate.exchange("/notes/" + first, HttpMethod.PUT, new HttpEntity<>(newNote), ResponseNoteDto.class);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));

        ResponseNoteDto updatedNote = response.getBody();
        assertThat(updatedNote.getName(), is("newFirstName"));
        assertThat(updatedNote.getLogin(), is("firstLogin"));
        assertThat(updatedNote.getPassword(), is("firstPassword"));
        assertThat(updatedNote.getUrl(), is("newFirstUrl.com"));
    }

    private ResponseEntity<ResponseNoteDto> sendCreateRequest(String name, String login, String password, String url) {
        CreateNoteDto createNoteDto = new CreateNoteDto()
                .setName(name)
                .setLogin(login)
                .setPassword(password)
                .setUrl(url);

        return restTemplate.postForEntity("/notes/create", createNoteDto, ResponseNoteDto.class);
    }
}
