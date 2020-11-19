package org.youtube.articles;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseArticleScenario {

    List<String> mainArticleClicked;

    List<String> articleRead;

    int numberArticleRead;

    abstract String getHomeUrl();

    abstract String[] getTopArticleViewPosition();

    abstract void visitHomePage();

    // For test
    abstract void visitRandomArticle();

    abstract void clickLogoToHomePage();

    abstract void clickTopArticle();

    abstract void clickMostViewArticle() throws Exception;

    abstract void clickRelatedTopicArticle() throws Exception;

    abstract void clickRelatedCategoryArticle() throws Exception;

    abstract void readArticle();

    abstract void readComments();

    abstract void decideNextArticle();

    abstract void onFinishReading();

    public void runDefaultScript() {
        mainArticleClicked = new ArrayList<>();
        articleRead = new ArrayList<>();
        visitHomePage();
        clickTopArticle();
    }

}
