package com.zxz.recognition.srevice;

import java.util.List;

public class RecognitionResult {

    private String log_id;

    private int words_result_num;

    private List<WordResult> words_result;

    public String getLog_id() {
        return log_id;
    }

    public void setLog_id(String log_id) {
        this.log_id = log_id;
    }

    public int getWords_result_num() {
        return words_result_num;
    }

    public void setWords_result_num(int words_result_num) {
        this.words_result_num = words_result_num;
    }

    public List<WordResult> getWords_result() {
        return words_result;
    }

    public void setWords_result(List<WordResult> words_result) {
        this.words_result = words_result;
    }
}
