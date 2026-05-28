package com.back.domain.post.post.controller;

import com.back.domain.post.post.entity.Post;
import com.back.domain.post.post.service.PostService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test") // 테스트 환경에서는 test 프로파일을 활성화합니다.
@SpringBootTest // 스프링부트 테스트 클래스임을 나타냅니다.
@AutoConfigureMockMvc // MockMvc를 자동으로 설정합니다, POSTMAN 처럼 요청을 보낼 수 있음
@Transactional // 각 테스트 메서드가 종료되면 롤백됩니다.
public class ApiV1PostControllerTest {
    @Autowired
    private MockMvc mvc; // MockMvc를 주입받습니다, @AutoConfigureMockMvc 덕분에 가능

    @Autowired
    private PostService postService;

    @Test
    @DisplayName("글 작성")
    void t1() throws Exception {
        // 글 작성 요청을 보냅니다.
        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/posts?actorId=3")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "title": "제목",
                                            "content": "내용"
                                        }
                                        """)
                ).andDo(print()); // 응답결과를 출력합니다.

        Post post = postService.findLatest().get();
        long totalCount = postService.count();

        resultActions
                .andExpect(handler().handlerType(ApiV1PostController.class))
                .andExpect(handler().methodName("write"))
                .andExpect(status().isCreated()) // 201 Created 상태코드 검증
                .andExpect(jsonPath("$.resultCode").value("201-1"))
                .andExpect(jsonPath("$.msg").value("%d번 글이 작성되었습니다.".formatted(post.getId())))
                .andExpect(jsonPath("$.data.id").value(post.getId()))
                .andExpect(jsonPath("$.data.createDate").value(Matchers.startsWith(post.getCreateDate().toString().substring(0, 20))))
                .andExpect(jsonPath("$.data.modifyDate").value(Matchers.startsWith(post.getModifyDate().toString().substring(0, 20))))
                .andExpect(jsonPath("$.data.title").value("제목"))
                .andExpect(jsonPath("$.data.content").value("내용"))
                .andExpect(jsonPath("$.data.author").value("유저1"));
    }

    @Test
    @DisplayName("글 작성, without title")
    void t7() throws Exception {
        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/posts?actorId=1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "title": "",
                                            "content": "내용"
                                        }
                                        """)
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1PostController.class))
                .andExpect(handler().methodName("write"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.resultCode").value("400-1"))
                .andExpect(jsonPath("$.msg").value("""
                        title-NotBlank-must not be blank
                        title-Size-size must be between 2 and 100
                        """.stripIndent().trim()));
    }

    @Test
    @DisplayName("글 작성, without content")
    void t8() throws Exception {
        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/posts?actorId=1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "title": "제목",
                                            "content": ""
                                        }
                                        """)
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1PostController.class))
                .andExpect(handler().methodName("write"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.resultCode").value("400-1"))
                .andExpect(jsonPath("$.msg").value("""
                        content-NotBlank-must not be blank
                        content-Size-size must be between 2 and 5000
                        """.stripIndent().trim()));
    }

    @Test
    @DisplayName("글 작성, with wrong json syntax")
    void t9() throws Exception {

        String wrongJsonBody = """
                {
                    "title": 제목",
                    content": "내용"
                """;

        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/posts?actorId=1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(wrongJsonBody)
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1PostController.class))
                .andExpect(handler().methodName("write"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.resultCode").value("400-1"))
                .andExpect(jsonPath("$.msg").value("요청 본문이 올바르지 않습니다.".stripIndent().trim()));
    }

    @Test
    @DisplayName("글 수정")
    void t2() throws Exception {
        int id = 1;

        Post post = postService.findById(id).get();
        int authorId = post.getMember().getId();

        // 글 수정 요청을 보냅니다.
        ResultActions resultActions = mvc
                .perform(
                        put("/api/v1/posts/" + id + "?actorId=" + authorId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "title": "제목 new",
                                            "content": "내용 new"
                                        }
                                        """)
                ).andDo(print()); // 응답결과를 출력합니다.

        resultActions
                .andExpect(handler().handlerType(ApiV1PostController.class))
                .andExpect(handler().methodName("modify"))
                .andExpect(status().isOk()) // 200 Ok 상태코드 검증
                .andExpect(jsonPath("$.resultCode").value("200-1"))
                .andExpect(jsonPath("$.msg").value("%d번 글이 수정되었습니다.".formatted(id)));

//        Post post = postService.findById(id).get();
//
//        assertThat(post.getTitle()).isEqualTo("제목 new");
//        assertThat(post.getContent()).isEqualTo("내용 new");
    }

    @Test
    @DisplayName("글 삭제 - 본인 글 삭제 성공")
    void t3() throws Exception {
        int id = 1;
        Post post = postService.findById(id).get();
        int authorId = post.getMember().getId(); // 1번 글의 작성자 ID 조회
        ResultActions resultActions = mvc
                .perform(
                        delete("/api/v1/posts/" + id + "?actorId=" + authorId)
                )
                .andDo(print());
        resultActions
                .andExpect(handler().handlerType(ApiV1PostController.class))
                .andExpect(handler().methodName("delete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200-1"))
                .andExpect(jsonPath("$.msg").value("%d번 글이 삭제되었습니다.".formatted(id)));
    }

    @Test
    @DisplayName("글 단건조회")
    void t4() throws Exception {
        int id = 1;

        ResultActions resultActions = mvc
                .perform(
                        get("/api/v1/posts/" + id)
                )
                .andDo(print());

        Post post = postService.findById(id).get();

        resultActions
                .andExpect(handler().handlerType(ApiV1PostController.class))
                .andExpect(handler().methodName("getItem"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(post.getId()))
                .andExpect(jsonPath("$.createDate").value(Matchers.startsWith(post.getCreateDate().toString().substring(0, 20))))
                .andExpect(jsonPath("$.modifyDate").value(Matchers.startsWith(post.getModifyDate().toString().substring(0, 20))))
                .andExpect(jsonPath("$.title").value(post.getTitle()))
                .andExpect(jsonPath("$.content").value(post.getContent()));
    }

    @Test
    @DisplayName("글 단건조회, 404")
    void t6() throws Exception {
        int id = Integer.MAX_VALUE;

        ResultActions resultActions = mvc
                .perform(
                        get("/api/v1/posts/" + id)
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1PostController.class))
                .andExpect(handler().methodName("getItem"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.resultCode").value("404-1"))
                .andExpect(jsonPath("$.msg").value("해당 데이터가 존재하지 않습니다."));
    }

    @Test
    @DisplayName("글 다건조회")
    void t5() throws Exception {
        ResultActions resultActions = mvc
                .perform(
                        get("/api/v1/posts")
                )
                .andDo(print());

        List<Post> posts = postService.findAll();

        resultActions
                .andExpect(handler().handlerType(ApiV1PostController.class))
                .andExpect(handler().methodName("getItems"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(posts.size()));

        for (int i = 0; i < posts.size(); i++) {
            Post post = posts.get(i);
            resultActions
                    .andExpect(jsonPath("$[%d].id".formatted(i)).value(post.getId()))
                    .andExpect(jsonPath("$[%d].createDate".formatted(i)).value(Matchers.startsWith(post.getCreateDate().toString().substring(0, 20))))
                    .andExpect(jsonPath("$[%d].modifyDate".formatted(i)).value(Matchers.startsWith(post.getModifyDate().toString().substring(0, 20))))
                    .andExpect(jsonPath("$[%d].title".formatted(i)).value(post.getTitle()))
                    .andExpect(jsonPath("$[%d].content".formatted(i)).value(post.getContent()));
        }
    }

    @Test
    @DisplayName("글 작성 시 actorId 파라미터가 누락된 경우 401 반환")
    void t10() throws Exception {
        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/posts") // actorId 생략
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                    {
                                        "title": "제목",
                                        "content": "내용"
                                    }
                                    """)
                ).andDo(print());

        resultActions
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.resultCode").value("401-1"))
                .andExpect(jsonPath("$.msg").value("로그인 후 이용해주세요."));
    }

    @Test
    @DisplayName("글 작성 시 존재하지 않는 actorId일 경우 401 반환")
    void t11() throws Exception {
        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/posts?actorId=999999") // 잘못된 actorId
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                    {
                                        "title": "제목",
                                        "content": "내용"
                                    }
                                    """)
                ).andDo(print());

        resultActions
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.resultCode").value("401-1"))
                .andExpect(jsonPath("$.msg").value("로그인 후 이용해주세요."));
    }

    @Test
    @DisplayName("글 삭제 - actorId 누락 시 401 반환")
    void t12() throws Exception {
        int id = 1;
        ResultActions resultActions = mvc
                .perform(
                        delete("/api/v1/posts/" + id) // actorId 없음
                )
                .andDo(print());
        resultActions
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.resultCode").value("401-1"))
                .andExpect(jsonPath("$.msg").value("로그인 후 이용해주세요."));
    }
    @Test
    @DisplayName("글 삭제 - 타인의 글 삭제 시도 시 403 반환")
    void t13() throws Exception {
        int id = 1;
        Post post = postService.findById(id).get();
        int authorId = post.getMember().getId();
        int notAuthorId = authorId == 3 ? 4 : 3; // 작성자가 아닌 다른 회원 ID
        ResultActions resultActions = mvc
                .perform(
                        delete("/api/v1/posts/" + id + "?actorId=" + notAuthorId)
                )
                .andDo(print());
        resultActions
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.resultCode").value("403-1"))
                .andExpect(jsonPath("$.msg").value("권한이 없습니다."));
    }

    @Test
    @DisplayName("글 수정 - actorId 누락 시 401 반환")
    void t14() throws Exception {
        int id = 1;
        ResultActions resultActions = mvc
                .perform(
                        put("/api/v1/posts/" + id) // actorId 없음
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "title": "제목 new",
                                            "content": "내용 new"
                                        }
                                        """)
                )
                .andDo(print());
        resultActions
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.resultCode").value("401-1"))
                .andExpect(jsonPath("$.msg").value("로그인 후 이용해주세요."));
    }
    @Test
    @DisplayName("글 수정 - 타인의 글 수정 시도 시 403 반환")
    void t15() throws Exception {
        int id = 1;
        Post post = postService.findById(id).get();
        int authorId = post.getMember().getId();
        int notAuthorId = authorId == 3 ? 4 : 3; // 작성자가 아닌 다른 회원 ID
        ResultActions resultActions = mvc
                .perform(
                        put("/api/v1/posts/" + id + "?actorId=" + notAuthorId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "title": "제목 new",
                                            "content": "내용 new"
                                        }
                                        """)
                )
                .andDo(print());
        resultActions
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.resultCode").value("403-1"))
                .andExpect(jsonPath("$.msg").value("권한이 없습니다."));
    }

}