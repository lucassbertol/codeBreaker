package com.lucassbertol.codebreaker.utils;

import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.lucassbertol.codebreaker.data.Question;

import java.util.ArrayList;
import java.util.List;

public class AnswerValidator {

    public static boolean validateAnswers(List<TextField> inputFields, Question question) {
        ArrayList<ArrayList<String>> correctAnswers = question.getAnswer();

        // Verifica se todas as respostas estão corretas
        for (int i = 0; i < correctAnswers.size(); i++) {
            String userAnswer = inputFields.get(i).getText().trim().toLowerCase();
            List<String> possibleAnswers = correctAnswers.get(i);

            boolean isCorrect = false;
            for (String possibleAnswer : possibleAnswers) {
                if (userAnswer.equalsIgnoreCase(possibleAnswer.trim())) {
                    isCorrect = true;
                    break;
                }
            }

            if (!isCorrect) {
                return false;
            }
        }

        return true;
    }

    // Limpa os campos
    public static void clearInputFields(List<TextField> inputFields) {
        for (TextField field : inputFields) {
            field.setText("");
        }
    }
}

