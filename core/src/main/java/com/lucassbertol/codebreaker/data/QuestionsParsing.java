package com.lucassbertol.codebreaker.data;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.lucassbertol.codebreaker.config.Constants;
import java.util.List;
import java.util.Random;

public class QuestionsParsing {
    private final Json json;
    private final Random random;

    public QuestionsParsing() {
        this.json = new Json();
        this.random = new Random();
    }

    public QuestionsData loadEasyQuestions() {
        FileHandle file = Gdx.files.internal(Constants.QUESTIONS_EASY);
        String jsonContent = file.readString();
        return json.fromJson(QuestionsData.class, jsonContent);
    }

    public QuestionsData loadHardQuestions() {
        FileHandle file = Gdx.files.internal(Constants.QUESTIONS_HARD);
        String jsonContent = file.readString();
        return json.fromJson(QuestionsData.class, jsonContent);
    }

    public QuestionsData loadQuestionsByDifficulty(String difficulty) {
        String path = Constants.QUESTIONS_PATH + difficulty.toLowerCase() + ".json";
        FileHandle file = Gdx.files.internal(path);
        String jsonContent = file.readString();
        return json.fromJson(QuestionsData.class, jsonContent);
    }

    public Question getRandomQuestion(String difficulty) {
        QuestionsData data = loadQuestionsByDifficulty(difficulty);
        List<Question> questions = data.getQuestoes();
        int randomIndex = random.nextInt(questions.size());
        return questions.get(randomIndex);
    }

    public Question getRandomQuestionExcluding(String difficulty, List<Integer> excludedIds) {
        QuestionsData data = loadQuestionsByDifficulty(difficulty);
        List<Question> questions = data.getQuestoes();

        // Filtra as questões que ainda não foram usadas
        List<Question> availableQuestions = new java.util.ArrayList<>();
        for (Question question : questions) {
            if (!excludedIds.contains(question.getId())) {
                availableQuestions.add(question);
            }
        }

        // Se não há questões disponíveis, retorna uma questão aleatória qualquer
        if (availableQuestions.isEmpty()) {
            int randomIndex = random.nextInt(questions.size());
            return questions.get(randomIndex);
        }

        // Retorna uma questão aleatória das disponíveis
        int randomIndex = random.nextInt(availableQuestions.size());
        return availableQuestions.get(randomIndex);
    }
}
