package com.jisu.projectboard.repository;

import com.jisu.projectboard.config.JpaConfig;
import com.jisu.projectboard.domain.Article;
import com.jisu.projectboard.domain.UserAccount;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DisplayName("JPA 연결 테스트")
@Import(JpaRepositoryTest.TestJpaConfig.class)
@DataJpaTest
class JpaRepositoryTest {

    private final ArticleRepository articleRepository;
    private final ArticleCommentRepository articleCommentRepository;
    private final UserAccountRepository userAccountRepository;


    public JpaRepositoryTest(@Autowired ArticleRepository articleRepository,
                             @Autowired ArticleCommentRepository articleCommentRepository,
                             @Autowired UserAccountRepository userAccountRepository) {
        this.articleRepository = articleRepository;
        this.articleCommentRepository = articleCommentRepository;
        this.userAccountRepository = userAccountRepository;
    }

    @DisplayName("select 테스트")
    @Test
    void givenTestData_whenSelecting_thenWorksFine(){
        //given

        //when
        List<Article> articles = articleRepository.findAll();


        //then
        assertThat(articles).isNotNull().hasSize(123);
    }

    @DisplayName("insert 테스트")
    @Test
    void givenTestData_whenInserting_thenWorksFine(){
        //given
        long count = articleRepository.count();
        UserAccount userAccount = userAccountRepository.save(UserAccount.of("jisu", "pw", null, null, null));
        Article article = Article.of(userAccount, "new article", "new content", "#spring");

        //when
        articleRepository.save(article);

        //then
        assertThat(articleRepository.count()).isEqualTo(count+1);
    }

    @DisplayName("update 테스트")
    @Test
    void givenTestData_whenUpdating_thenWorksFine(){
        //given
        Article findArticle = articleRepository.findById(1L).orElseThrow();
        String updatedHashtag = "#springboot";
        findArticle.setHashtag(updatedHashtag);
        //when
        Article savedArticle= articleRepository.saveAndFlush(findArticle);
        //update쿼리 보기위해 flush해줌

        //then
        assertThat(savedArticle).hasFieldOrPropertyWithValue("hashtag", updatedHashtag);
    }

    @DisplayName("delete 테스트")
    @Test
    void givenTestData_whenDeleting_thenWorksFine(){
        //given
        Article findArticle = articleRepository.findById(1L).orElseThrow();
        long articleCount = articleRepository.count();
        long articleCommentCount = articleCommentRepository.count();
        long deletedCommentSize = findArticle.getArticleComments().size();

        //when
        articleRepository.delete(findArticle);

        //then
        assertThat(articleRepository.count()).isEqualTo(articleCount -1);
        assertThat(articleCommentRepository.count()).isEqualTo(articleCommentCount -deletedCommentSize);
    }

    @EnableJpaAuditing
    @TestConfiguration
    public static class TestJpaConfig {
        @Bean
        public AuditorAware<String> auditorAware() {
            return () -> Optional.of("jisu");
        }
    }
}
