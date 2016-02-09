package edu.gcc.whiletrue.pingit;

/**
 * Created by Zared on 2/8/2016.
 */
public class FAQ {
    private String category;
    private String[][] questionArr;

    public FAQ() {
        category = "Porblem";
        questionArr[0][0] = "A question";
        questionArr[0][1] = "An answer";
    }

    public FAQ(String cat, String[][] arr) {
        this.category = cat;
        this.questionArr = arr;
    }

    public String getCategory() {
        return category;
    }

    public String[][] getQuestionArr() {
        return questionArr;
    }
}
